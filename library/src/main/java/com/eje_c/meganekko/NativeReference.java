/*
 * Copyright 2016 eje inc.
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class NativeReference extends WeakReference<HybridObject> {

    static final ReferenceQueue<HybridObject> sReferenceQueue = new ReferenceQueue<>();

    private static final Set<NativeReference> sNativeReferences = new HashSet<>();
    private long mNativePointer;

    private NativeReference(HybridObject r, long nativePointer, ReferenceQueue<? super HybridObject> q) {
        super(r, q);
        this.mNativePointer = nativePointer;
    }

    private static native void delete(long nativePointer);

    /**
     * Get {@link NativeReference} from nativePointer.
     *
     * @param hybridObject
     * @param nativePointer
     * @return
     */
    public static NativeReference get(HybridObject hybridObject, long nativePointer) {

        for (NativeReference ref : sNativeReferences) {
            if (ref.mNativePointer == nativePointer) {
                return ref;
            }
        }

        NativeReference ref = new NativeReference(hybridObject, nativePointer, sReferenceQueue);
        sNativeReferences.add(ref);

        return ref;
    }

    /**
     * Called from {@link MeganekkoApp#update()} when {@link HybridObject} was Garbage Collected.
     */
    synchronized void delete() {
        if (mNativePointer != 0) {
            delete(mNativePointer);
            mNativePointer = 0;
        }
        sNativeReferences.remove(this);
    }

    /**
     * @return Referenced native pointer.
     */
    public long getNativePointer() {
        return mNativePointer;
    }
}