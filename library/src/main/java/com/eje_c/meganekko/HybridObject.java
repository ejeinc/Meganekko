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

/**
 * Root of the Meganekko object hierarchy.
 * Descendant classes all have native (JNI) implementations; this base class
 * manages the native life cycles.
 */
public abstract class HybridObject {

    private static final String TAG = Log.tag(HybridObject.class);
    private final NativeReference mNativeReference;

    /**
     * Normal constructor
     */
    protected HybridObject() {
        long nativePointer = initNativeInstance();

        if (nativePointer == 0l) {
            throw new IllegalStateException("You must override initNativeInstance to get native pointer.");
        }

        mNativeReference = NativeReference.get(this, nativePointer);
    }

    protected HybridObject(long nativePointer) {

        if (nativePointer == 0l) {
            throw new IllegalStateException("You must pass valid native pointer.");
        }

        mNativeReference = NativeReference.get(this, nativePointer);
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
     * This is an internal method that may be useful in diagnostic code.
     *
     * @return Native pointer value.
     */
    public long getNative() {
        return mNativeReference.getNativePointer();
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
