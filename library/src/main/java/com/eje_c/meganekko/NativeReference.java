package com.eje_c.meganekko;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class NativeReference extends WeakReference<HybridObject> {

    static final ReferenceQueue<HybridObject> referenceQueue = new ReferenceQueue<>();

    private static final Set<NativeReference> NATIVE_REFERENCES = new HashSet<>();
    private long mNativePointer;

    private static native void delete(long nativePointer);

    private NativeReference(HybridObject r, long nativePointer, ReferenceQueue<? super HybridObject> q) {
        super(r, q);
        this.mNativePointer = nativePointer;
    }

    /**
     * Get {@link NativeReference} from nativePointer.
     *
     * @param hybridObject
     * @param nativePointer
     * @return
     */
    public static NativeReference get(HybridObject hybridObject, long nativePointer) {

        for (NativeReference ref : NATIVE_REFERENCES) {
            if (ref.mNativePointer == nativePointer) {
                return ref;
            }
        }

        NativeReference ref = new NativeReference(hybridObject, nativePointer, referenceQueue);
        NATIVE_REFERENCES.add(ref);

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
        NATIVE_REFERENCES.remove(this);
    }

    /**
     * @return Referenced native pointer.
     */
    public long getNativePointer() {
        return mNativePointer;
    }
}