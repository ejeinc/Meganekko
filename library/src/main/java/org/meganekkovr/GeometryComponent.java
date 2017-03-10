package org.meganekkovr;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * This gives geometry to {@link Entity} for rendering.
 */
public class GeometryComponent extends Component {

    private final NativePointer nativePointer;

    public GeometryComponent() {
        nativePointer = NativePointer.getInstance(newInstance());
    }

    private static native void setEntityGeometry(long entityPtr, long nativePtr);

    private static native void build(long nativePtr, float[] positions, float[] colors, float[] uvs, int[] triangles);

    private static native void buildGlobe(long nativePtr);

    private static native void buildDome(long nativePtr, float latRads);

    private static native void buildSpherePatch(long nativePtr, float fov);

    /**
     * Build plane geometry from {@link View}.
     *
     * @param view View
     * @return new instance
     */
    @NonNull
    public static GeometryComponent from(@NonNull View view) {

        view.measure(0, 0);
        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();
        view.layout(0, 0, width, height);

        GeometryComponent geometryComponent = new GeometryComponent();
        geometryComponent.buildQuad(width * 0.01f, height * 0.01f);

        return geometryComponent;
    }

    /**
     * Build plane geometry from {@link Drawable}.
     *
     * @param drawable Drawable
     * @return new instance
     */
    @NonNull
    public static GeometryComponent from(@NonNull Drawable drawable) {

        GeometryComponent geometryComponent = new GeometryComponent();
        geometryComponent.buildQuad(drawable.getIntrinsicWidth() * 0.01f, drawable.getIntrinsicHeight() * 0.01f);

        return geometryComponent;
    }

    /**
     * Build plane geometry from {@link Bitmap}.
     *
     * @param bitmap Bitmap
     * @return new instance
     */
    @NonNull
    public static GeometryComponent from(@NonNull Bitmap bitmap) {

        GeometryComponent geometryComponent = new GeometryComponent();
        geometryComponent.buildQuad(bitmap.getWidth() * 0.01f, bitmap.getHeight() * 0.01f);

        return geometryComponent;
    }

    protected native long newInstance();

    @Override
    public void onAttach(@NonNull Entity entity) {
        super.onAttach(entity);
        setEntityGeometry(entity.getNativePointer(), nativePointer.get());
    }

    /**
     * Build big sphere with inverted normals.
     * This is often used for a projecting equirectangular photo or video.
     */
    public void buildGlobe() {
        buildGlobe(nativePointer.get());

        if (isAttached()) {
            setEntityGeometry(getEntity().getNativePointer(), nativePointer.get());
        }
    }

    public void buildDome(float latRads) {
        buildDome(nativePointer.get(), latRads);

        if (isAttached()) {
            setEntityGeometry(getEntity().getNativePointer(), nativePointer.get());
        }
    }

    /**
     * Make a square patch on a sphere that can rotate with the viewer so it always covers the screen.
     *
     * @param fov
     */
    public void buildSpherePatch(float fov) {
        buildSpherePatch(nativePointer.get(), fov);

        if (isAttached()) {
            setEntityGeometry(getEntity().getNativePointer(), nativePointer.get());
        }
    }

    public void build(@NonNull float[] positions, @NonNull float[] colors, @NonNull float[] uvs, @NonNull int[] triangles) {

        if (positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 3.");
        } else if (colors.length % 4 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 4.");
        } else if (uvs.length % 2 != 0) {
            throw new IllegalArgumentException("positions element count must be multiple of 2.");
        } else if (triangles.length % 3 != 0) {
            throw new IllegalArgumentException("triangles element count must be multiple of 3.");
        }

        int positionSize = positions.length / 3;
        int colorSize = colors.length / 4;
        int uvSize = uvs.length / 2;

        if (positionSize != colorSize) {
            throw new IllegalArgumentException("position elements are " + positionSize + " but color elements are " + colorSize + ".");
        } else if (colorSize != uvSize) {
            throw new IllegalArgumentException("color elements are " + colorSize + " but uv elements are " + uvSize + ".");
        }

        build(nativePointer.get(), positions, colors, uvs, triangles);
    }

    /**
     * Build quad plane mesh geometry.
     *
     * @param width  Plane's width
     * @param height Plane's height
     */
    public void buildQuad(float width, float height) {

        /*
         * 0    2
         * *----*
         * |  / |
         * | /  |
         * *----*
         * 1    3
         */

        float[] positions = {
                width * -0.5f, height * 0.5f, 0.0f,  // Left Top
                width * -0.5f, height * -0.5f, 0.0f, // Left Bottom
                width * 0.5f, height * 0.5f, 0.0f,   // Right Top
                width * 0.5f, height * -0.5f, 0.0f   // Right Bottom
        };

        float[] colors = {
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

        float[] uvs = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        int[] triangles = {
                0, 1, 2,
                1, 3, 2
        };

        build(positions, colors, uvs, triangles);
    }

    public final long getNativePointer() {
        return nativePointer.get();
    }
}
