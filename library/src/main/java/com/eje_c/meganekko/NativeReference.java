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