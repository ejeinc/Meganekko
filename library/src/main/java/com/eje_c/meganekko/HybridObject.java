/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko;

import com.eje_c.meganekko.utility.Log;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

/**
 * Root of the Meganekko object hierarchy.
 * <p/>
 * Descendant classes all have native (JNI) implementations; this base class
 * manages the native life cycles.
 */
public abstract class HybridObject implements Closeable {

    private static final String TAG = Log.tag(HybridObject.class);

    /*
     * Instance fields
     */
    /**
     * Our {@linkplain Reference references} are placed on this queue, once
     * they've been finalized
     */
    private static final ReferenceQueue<HybridObject> sReferenceQueue = new ReferenceQueue<>();
    /**
     * We need hard references to {@linkplain Reference our references} -
     * otherwise, the references get garbage collected (usually before their
     * objects) and never get enqueued.
     */
    private static final Set<Reference> sReferenceSet = new HashSet<>();

    /*
     * Constructors
     */

    static {
        new FinalizeThread().start();
    }

    /**
     * This is not {@code final}: the first call to {@link #close()} sets
     * {@link #mNativePointer} to 0, so that {@link #close()} can safely be
     * called multiple times.
     */
    private long mNativePointer;

    /**
     * Normal constructor
     */
    protected HybridObject() {
        mNativePointer = initNativeInstance();

        if (mNativePointer == 0l) {
            throw new IllegalStateException("You must override initNativeInstance to get native pointer.");
        }

        sReferenceSet.add(new Reference(this, mNativePointer));
    }

    protected HybridObject(long nativePointer) {
        mNativePointer = nativePointer;

        if (mNativePointer == 0l) {
            throw new IllegalStateException("You must pass valid native pointer.");
        }

        sReferenceSet.add(new Reference(this, mNativePointer));
    }

    /**
     * Explicitly close()ing an object is going to be relatively rare - most
     * native memory will be freed when the owner-objects are garbage collected.
     * Doing a lookup in these rare cases means that we can avoid giving every @link
     * {@link HybridObject} a hard reference to its {@link Reference}.
     */
    private static Reference findReference(long nativePointer) {
        for (Reference reference : sReferenceSet) {
            if (reference.mNativePointer == nativePointer) {
                return reference;
            }
        }
        // else
        return null;
    }

    private static native void delete(long nativePointer);

    /**
     * You must override this method if you use constructor that don't take
     * nativePointer.
     *
     * @return native pointer
     */
    protected long initNativeInstance() {
        return 0l;
    }

    /**
     * The actual address of the native object.
     * <p/>
     * This is an internal method that may be useful in diagnostic code.
     */
    public long getNative() {
        return mNativePointer;
    }

    @Override
    public boolean equals(Object o) {
        // FIXME Since there is a 1:1 relationship between wrappers and native
        // objects, `return this == o` should be all we need ...
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof HybridObject) {
            HybridObject other = (HybridObject) o;
            boolean nativeEquality = getNative() == other.getNative();
            if (nativeEquality) {
                Log.d(TAG, "%s.equals(%s), but %s %c= %s", //
                        this, o, //
                        this, (this == o) ? '=' : '!', o);
            }
            return nativeEquality;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Long nativePointer = getNative();
        return nativePointer.hashCode();
    }

    /**
     * Close this object, releasing any native resources.
     * <p/>
     * Most objects will be automatically closed when Java's garbage collector
     * detects that they are no longer being used: Explicitly closing an object
     * that's still linked into the scene graph will almost certainly crash your
     * Meganekko app. You should only {@code close()} transient objects (especially
     * those that use lots of memory, like large textures) that you
     * <em>know</em> are no longer being used.
     */
    @Override
    public final void close() throws IOException {
        if (mNativePointer != 0L) {
            Reference reference = findReference(mNativePointer);
            if (reference != null) {
                reference.close();
                mNativePointer = 0L;
            }
        }
    }

    private static class Reference extends PhantomReference<HybridObject> {

        private long mNativePointer;

        private Reference(HybridObject object, long nativePointer) {
            super(object, sReferenceQueue);

            mNativePointer = nativePointer;
        }

        private void close() {
            if (mNativePointer != 0) {
                delete(mNativePointer);
            }

            sReferenceSet.remove(this);
        }
    }

    private static class FinalizeThread extends Thread {

        private FinalizeThread() {
            setName("Finalize Thread");
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Reference reference = (Reference) sReferenceQueue.remove();
                    reference.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
