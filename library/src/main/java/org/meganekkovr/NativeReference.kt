package org.meganekkovr

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * This class handles native resource garbage collection.
 */
internal class NativeReference(nativePointer: NativePointer) : WeakReference<NativePointer>(nativePointer, sReferenceQueue) {

    private var mNativePointer = nativePointer.get()

    /**
     * Delete native pointer.
     */
    private fun delete() {
        if (mNativePointer != 0L) {
            delete(mNativePointer)
            mNativePointer = 0
        }
    }

    private external fun delete(nativePointer: Long)

    companion object {

        private val sReferenceQueue = ReferenceQueue<NativePointer>()

        /**
         * Called from [org.meganekkovr.MeganekkoApp.update].
         */
        @JvmStatic
        fun gc() {

            while (true) {
                val ref = NativeReference.sReferenceQueue.poll() ?: break
                (ref as? NativeReference)?.delete()
            }
        }
    }
}