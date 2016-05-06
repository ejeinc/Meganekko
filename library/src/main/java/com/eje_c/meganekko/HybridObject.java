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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Root of the Meganekko object hierarchy.
 * <p/>
 * Descendant classes all have native (JNI) implementations; this base class
 * manages the native life cycles.
 */
public abstract class HybridObject {

    static final ReferenceQueue<HybridObject> referenceQueue = new ReferenceQueue<>();
    private static final Set<NativeReference> NATIVE_REFERENCES = new HashSet<>();
    private static final String TAG = Log.tag(HybridObject.class);
    private final NativeReference nativeReference;

    private static native void delete(long nativePointer);

    /**
     * Holds native pointer.
     */
    static class NativeReference extends WeakReference<HybridObject> {
        private long mNativePointer;

        public NativeReference(HybridObject r, long nativePointer, ReferenceQueue<? super HybridObject> q) {
            super(r, q);
            this.mNativePointer = nativePointer;
        }

        /**
         * Called from {@link MeganekkoApp#update()} when {@link HybridObject} was Garbage Collected.
         */
        synchronized void delete() {
            if (mNativePointer != 0) {
                HybridObject.delete(mNativePointer);
                mNativePointer = 0;
            }
            NATIVE_REFERENCES.remove(this);
        }
    }

    /**
     * Normal constructor
     */
    protected HybridObject() {
        long nativePointer = initNativeInstance();

        if (nativePointer == 0l) {
            throw new IllegalStateException("You must override initNativeInstance to get native pointer.");
        }

        nativeReference = new NativeReference(this, nativePointer, referenceQueue);
        NATIVE_REFERENCES.add(nativeReference);
    }

    protected HybridObject(long nativePointer) {

        if (nativePointer == 0l) {
            throw new IllegalStateException("You must pass valid native pointer.");
        }

        nativeReference = new NativeReference(this, nativePointer, referenceQueue);
        NATIVE_REFERENCES.add(nativeReference);
    }

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
        return nativeReference.mNativePointer;
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
}
