package org.meganekkovr

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View

/**
 * This gives geometry to [Entity] for rendering.
 */
class GeometryComponent : Component() {

    private val _nativePointer = NativePointer.getInstance(newInstance())

    val nativePointer: Long
        get() {
            return _nativePointer.get()
        }

    private external fun setEntityGeometry(entityPtr: Long, nativePtr: Long)

    private external fun build(nativePtr: Long, positions: FloatArray, colors: FloatArray, uvs: FloatArray, triangles: IntArray)

    private external fun buildGlobe(nativePtr: Long)

    private external fun buildDome(nativePtr: Long, latRads: Float)

    private external fun buildSpherePatch(nativePtr: Long, fov: Float)

    private external fun newInstance(): Long

    override fun onAttach(entity: Entity) {
        super.onAttach(entity)
        setEntityGeometry(entity.nativePointer, nativePointer)
    }

    /**
     * Build big sphere with inverted normals.
     * This is often used for a projecting equirectangular photo or video.
     */
    fun buildGlobe() {
        buildGlobe(nativePointer)
    }

    fun buildDome(latRads: Float) {
        buildDome(nativePointer, latRads)
    }

    /**
     * Make a square patch on a sphere that can rotate with the viewer so it always covers the screen.
     *
     * @param fov
     */
    fun buildSpherePatch(fov: Float) {
        buildSpherePatch(nativePointer, fov)
    }

    fun build(positions: FloatArray, colors: FloatArray, uvs: FloatArray, triangles: IntArray) {

        require(positions.size % 3 == 0) { "positions element count must be multiple of 3." }
        require(colors.size % 4 == 0) { "positions element count must be multiple of 4." }
        require(uvs.size % 2 == 0) { "positions element count must be multiple of 2." }
        require(triangles.size % 3 == 0) { "triangles element count must be multiple of 3." }

        val positionSize = positions.size / 3
        val colorSize = colors.size / 4
        val uvSize = uvs.size / 2

        require(positionSize == colorSize) { "position elements are $positionSize but color elements are $colorSize." }
        require(colorSize == uvSize) { "color elements are $colorSize but uv elements are $uvSize." }

        build(nativePointer, positions, colors, uvs, triangles)
    }

    /**
     * Build quad plane mesh geometry.
     *
     * @param width  Plane's width
     * @param height Plane's height
     */
    fun buildQuad(width: Float, height: Float) {

        /*
         * 0    2
         * *----*
         * |  / |
         * | /  |
         * *----*
         * 1    3
         */

        val positions = floatArrayOf(
                width * -0.5f, height * 0.5f, 0.0f, // Left Top
                width * -0.5f, height * -0.5f, 0.0f, // Left Bottom
                width * 0.5f, height * 0.5f, 0.0f, // Right Top
                width * 0.5f, height * -0.5f, 0.0f   // Right Bottom
        )

        val colors = floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f)

        val uvs = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f)

        val triangles = intArrayOf(
                0, 1, 2,
                1, 3, 2)

        build(positions, colors, uvs, triangles)
    }

    companion object {

        /**
         * Build plane geometry from [View].
         *
         * @param view View
         * @return new instance
         */
        @JvmStatic
        fun from(view: View): GeometryComponent {

            view.measure(0, 0)
            val width = view.measuredWidth
            val height = view.measuredHeight
            view.layout(0, 0, width, height)

            val geometryComponent = GeometryComponent()
            geometryComponent.buildQuad(width * 0.01f, height * 0.01f)

            return geometryComponent
        }

        /**
         * Build plane geometry from [Drawable].
         *
         * @param drawable Drawable
         * @return new instance
         */
        @JvmStatic
        fun from(drawable: Drawable): GeometryComponent {

            val geometryComponent = GeometryComponent()
            geometryComponent.buildQuad(drawable.intrinsicWidth * 0.01f, drawable.intrinsicHeight * 0.01f)

            return geometryComponent
        }

        /**
         * Build plane geometry from [Bitmap].
         *
         * @param bitmap Bitmap
         * @return new instance
         */
        @JvmStatic
        fun from(bitmap: Bitmap): GeometryComponent {

            val geometryComponent = GeometryComponent()
            geometryComponent.buildQuad(bitmap.width * 0.01f, bitmap.height * 0.01f)

            return geometryComponent
        }
    }
}
