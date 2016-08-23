package com.eje_c.meganekko;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents native pointer.
 * Many {@link HybridObject}s can have single {@link NativePointer}.
 * If no {@link HybridObject} has reference to {@link NativePointer}, it will be garbage collected.
 * If {@link NativePointer} is garbage collected, {@link NativeReference#gc()} will delete native pointer.
 * <p>
 * {@link HybridObject} *---1 {@link NativePointer} 1---1 {@link NativeReference}
 */
class NativePointer {
    // This holds all instance references
    private static final Set<WeakReference<NativePointer>> sInstances = new HashSet<>();

    private final long mPtr;

    private NativePointer(long ptr) {
        this.mPtr = ptr;
        sInstances.add(new NativeReference(this));
    }

    /**
     * @return Native pointer value
     */
    public long get() {
        return mPtr;
    }

    public static NativePointer getInstance(long ptr) {

        // Find existing instance for ptr
        for (WeakReference<NativePointer> instanceRef : sInstances) {
            NativePointer instance = instanceRef.get();
            if (instance == null) continue;

            if (instance.mPtr == ptr) {
                return instance;
            }
        }

        // create new one
        return new NativePointer(ptr);
    }
}
