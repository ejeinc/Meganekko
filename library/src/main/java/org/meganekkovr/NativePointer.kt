package org.meganekkovr

import java.lang.ref.WeakReference

/**
 * Represents native pointer.
 * Many objects can have single [NativePointer].
 * If no object has reference to [NativePointer], it will be garbage collected.
 * If [NativePointer] is garbage collected, [NativeReference.gc] will delete native pointer.
 * object *---1 [NativePointer] 1---1 [NativeReference]
 */
internal class NativePointer private constructor(private val mPtr: Long) {

    init {
        sInstances.add(NativeReference(this))
    }

    /**
     * @return Native pointer value
     */
    fun get(): Long {
        return mPtr
    }

    companion object {
        // This holds all instance references
        private val sInstances = mutableSetOf<WeakReference<NativePointer>>()

        @JvmStatic
        fun getInstance(ptr: Long): NativePointer {

            // Find existing instance for ptr
            for (instanceRef in sInstances) {
                val instance = instanceRef.get() ?: continue

                if (instance.mPtr == ptr) {
                    return instance
                }
            }

            // create new one
            return NativePointer(ptr)
        }
    }
}
