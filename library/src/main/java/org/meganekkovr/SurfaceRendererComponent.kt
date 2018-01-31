package org.meganekkovr

import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.graphics.drawable.Drawable
import android.view.Surface
import android.view.View
import android.view.ViewGroup

/**
 * This gives appearance to [Entity] for rendering.
 * Surface can be defined with [Drawable], [View], or [android.graphics.Bitmap].
 * You can use [Canvas] to render custom drawings.
 * [.getSurfaceTexture] or [.getSurface] are usefull if you wish to render video
 * or camera images with [android.media.MediaPlayer] or [android.hardware.Camera].
 */
class SurfaceRendererComponent : Component() {

    private val nativePointer = NativePointer.getInstance(newInstance())
    var canvasRenderer: CanvasRenderer? = null
        set(canvasRenderer) {
            field = canvasRenderer

            if (canvasRenderer != null) {
                surfaceTexture.setDefaultBufferSize(canvasRenderer.width, canvasRenderer.height)
            }
        }

    var continuousUpdate: Boolean = false

    /**
     * Get actual opacity for rendering. This value is inherited from parent [Entity].
     *
     * @return Actual opacity for rendering
     */
    var opacity = 1.0f
        set(opacity) {

            field = if (opacity < 0) {
                0f
            } else if (opacity > 1) {
                1f
            } else {
                opacity
            }

            setOpacity(nativePointer.get(), opacity)
        }

    var stereoMode = StereoMode.NORMAL
        set(stereoMode) {
            field = stereoMode
            setStereoMode(nativePointer.get(), stereoMode.ordinal)
        }

    val surfaceTexture: SurfaceTexture
        get() = getSurfaceTexture(nativePointer.get())

    val surface: Surface
        get() = getSurface(nativePointer.get())

    var useChromaKey: Boolean
        get() = getUseChromaKey(nativePointer.get())
        set(useChromaKey) = setUseChromaKey(nativePointer.get(), useChromaKey)

    var chromaKeyThreshold: Float
        get() = getChromaKeyThreshold(nativePointer.get())
        set(chromaKeyThreshold) = setChromaKeyThreshold(nativePointer.get(), chromaKeyThreshold)

    var chromaKeyBlend: Float
        get() = getChromaKeyBlend(nativePointer.get())
        set(chromaKeyBlend) = setChromaKeyBlend(nativePointer.get(), chromaKeyBlend)

    private external fun getSurfaceTexture(nativePtr: Long): SurfaceTexture

    private external fun getSurface(nativePtr: Long): Surface

    private external fun setEntityTexture(entityPtr: Long, nativePtr: Long)

    private external fun removeEntityTexture(entityPtr: Long, nativePtr: Long)

    private external fun setOpacity(nativePtr: Long, opacity: Float)

    private external fun setStereoMode(nativePtr: Long, stereoMode: Int)

    private external fun setUseChromaKey(nativePtr: Long, useChromaKey: Boolean)

    private external fun getUseChromaKey(nativePtr: Long): Boolean

    private external fun setChromaKeyThreshold(nativePtr: Long, chromaKeyThreshold: Float)

    private external fun getChromaKeyThreshold(nativePtr: Long): Float

    private external fun setChromaKeyBlend(nativePtr: Long, chromaKeyBlend: Float)

    private external fun getChromaKeyBlend(nativePtr: Long): Float

    private external fun setChromaKeyColor(nativePtr: Long, r: Float, g: Float, b: Float)

    private external fun newInstance(): Long

    override fun onAttach(entity: Entity) {
        super.onAttach(entity)
        setEntityTexture(entity.nativePointer, nativePointer.get())
    }

    override fun onDetach(entity: Entity) {
        super.onDetach(entity)
        removeEntityTexture(entity.nativePointer, nativePointer.get())
    }

    override fun update(frame: FrameInput) {

        val renderer = this.canvasRenderer
        if (renderer != null) {

            if (renderer.isDirty) {
                val surface = getSurface(nativePointer.get())
                val canvas = surface.lockCanvas(null)

                // One time draw if return true
                if (renderer.render(canvas)) {
                    renderer.isDirty = false
                }

                surface.unlockCanvasAndPost(canvas)

                // One time update texture
                if (!continuousUpdate) surfaceTexture.updateTexImage()
            }
        }

        // Continuous update texture
        if (continuousUpdate) {
            surfaceTexture.updateTexImage()
        }

        super.update(frame)
    }

    fun setChromaKeyColor(r: Float, g: Float, b: Float) {
        setChromaKeyColor(nativePointer.get(), r, g, b)
    }

    enum class StereoMode {
        NORMAL, // 0
        TOP_BOTTOM, // 1
        BOTTOM_TOP, // 2
        LEFT_RIGHT, // 3
        RIGHT_LEFT, // 4
        TOP_ONLY, // 5
        BOTTOM_ONLY, // 6
        LEFT_ONLY, // 7
        RIGHT_ONLY   // 8
    }

    abstract class CanvasRenderer(val width: Int, val height: Int) {
        open var isDirty = true

        /**
         * @param canvas Canvas to draw.
         * @return `true` to end drawing. No more called in next update. If redrawing is required, call [.invalidate].
         * `false` to continues draw. This behavior can be modified overriding [.isDirty].
         */
        abstract fun render(canvas: Canvas): Boolean

        fun invalidate() {
            isDirty = true
        }
    }

    class DrawableRenderer(private val drawable: Drawable) : CanvasRenderer(drawable.intrinsicWidth, drawable.intrinsicHeight) {

        override fun render(canvas: Canvas): Boolean {
            drawable.draw(canvas)
            return true
        }
    }

    class ViewRenderer protected constructor(val view: View, width: Int, height: Int) : CanvasRenderer(width, height) {

        override var isDirty: Boolean
            get() = isDirty(view)
            set(value) {
                super.isDirty = value
            }

        override fun render(canvas: Canvas): Boolean {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            view.draw(canvas)
            return true
        }

        companion object {

            fun from(view: View): ViewRenderer {
                view.measure(0, 0)
                val width = view.measuredWidth
                val height = view.measuredHeight
                view.layout(0, 0, width, height)
                return ViewRenderer(view, width, height)
            }

            /**
             * Check dirty state of view recursively.
             *
             * @param view Checked View.
             * @return Returns true if at least one View is dirty in hierarchy.
             */
            private fun isDirty(view: View): Boolean {

                if (view.isDirty) return true

                // Apply this method to all children of view if view is ViewGroup
                if (view is ViewGroup) {

                    var i = 0
                    val count = view.childCount
                    while (i < count) {
                        if (isDirty(view.getChildAt(i))) return true
                        ++i
                    }
                }

                return false
            }
        }
    }

    companion object {

        @JvmStatic
        fun from(view: View): SurfaceRendererComponent {

            view.measure(0, 0)
            val width = view.measuredWidth
            val height = view.measuredHeight
            view.layout(0, 0, width, height)

            val component = SurfaceRendererComponent()
            component.canvasRenderer = ViewRenderer.from(view)

            return component
        }

        @JvmStatic
        fun from(drawable: Drawable): SurfaceRendererComponent {

            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            val component = SurfaceRendererComponent()
            component.canvasRenderer = DrawableRenderer(drawable)

            return component
        }
    }
}
