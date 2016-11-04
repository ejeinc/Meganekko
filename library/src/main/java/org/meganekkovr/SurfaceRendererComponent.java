package org.meganekkovr;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

/**
 * This gives appearance to {@link Entity} for rendering.
 * Surface can be defined with {@link Drawable}, {@link View}, or {@link android.graphics.Bitmap}.
 * You can use {@link Canvas} to render custom drawings.
 * {@link #getSurfaceTexture()} or {@link #getSurface()} are usefull if you wish to render video
 * or camera images with {@link android.media.MediaPlayer} or {@link android.hardware.Camera}.
 */
public class SurfaceRendererComponent extends Component {

    private final NativePointer nativePointer;
    private CanvasRenderer canvasRenderer;
    private boolean continuousUpdate;
    private float opacity = 1.0f;
    private StereoMode stereoMode = StereoMode.NORMAL;
    public SurfaceRendererComponent() {
        nativePointer = NativePointer.getInstance(newInstance());
    }

    private static native SurfaceTexture getSurfaceTexture(long nativePtr);

    private static native Surface getSurface(long nativePtr);

    private static native void setEntityTexture(long entityPtr, long nativePtr);

    private static native void removeEntityTexture(long entityPtr, long nativePtr);

    private static native void setOpacity(long nativePtr, float opacity);

    private static native void setStereoMode(long nativePtr, int stereoMode);

    private static native void setUseChromaKey(long nativePtr, boolean useChromaKey);

    private static native boolean getUseChromaKey(long nativePtr);

    private static native void setChromaKeyColor(long nativePtr, float r, float g, float b);

    public static SurfaceRendererComponent from(View view) {

        view.measure(0, 0);
        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();
        view.layout(0, 0, width, height);

        SurfaceRendererComponent component = new SurfaceRendererComponent();
        component.setCanvasRenderer(ViewRenderer.from(view));

        return component;
    }

    public static SurfaceRendererComponent from(Drawable drawable) {

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        SurfaceRendererComponent component = new SurfaceRendererComponent();
        component.setCanvasRenderer(new DrawableRenderer(drawable));

        return component;
    }

    protected native long newInstance();

    @Override
    public void onAttach(Entity entity) {
        super.onAttach(entity);
        setEntityTexture(entity.getNativePointer(), nativePointer.get());
    }

    @Override
    public void onDetach(Entity entity) {
        super.onDetach(entity);
        removeEntityTexture(entity.getNativePointer(), nativePointer.get());
    }

    @Override
    public void update(FrameInput frame) {

        if (canvasRenderer != null) {
            if (canvasRenderer.isDirty()) {
                Surface surface = getSurface(nativePointer.get());
                Canvas canvas = surface.lockCanvas(null);

                // One time draw if return true
                if (canvasRenderer.render(canvas)) {
                    canvasRenderer.dirty = false;
                }

                surface.unlockCanvasAndPost(canvas);

                // One time update texture
                if (!continuousUpdate) getSurfaceTexture().updateTexImage();
            }
        }

        // Continuous update texture
        if (continuousUpdate) {
            getSurfaceTexture().updateTexImage();
        }

        super.update(frame);
    }

    public SurfaceTexture getSurfaceTexture() {
        return getSurfaceTexture(nativePointer.get());
    }

    public Surface getSurface() {
        return getSurface(nativePointer.get());
    }

    public CanvasRenderer getCanvasRenderer() {
        return canvasRenderer;
    }

    public void setCanvasRenderer(CanvasRenderer canvasRenderer) {
        this.canvasRenderer = canvasRenderer;

        if (canvasRenderer != null) {
            getSurfaceTexture().setDefaultBufferSize(canvasRenderer.width, canvasRenderer.height);
        }
    }

    /**
     * Call this to update internal {@link SurfaceTexture} on every frame update.
     * Typically used when rendering video with {@link android.media.MediaPlayer}.
     *
     * @param continuousUpdate {@code true} to update texture automatically on frame update. default is {@code false}.
     */
    public void setContinuousUpdate(boolean continuousUpdate) {
        this.continuousUpdate = continuousUpdate;
    }

    /**
     * Get actual opacity for rendering. This value is inherited from parent {@link Entity}.
     *
     * @return Actual opacity for rendering
     */
    public float getOpacity() {
        return opacity;
    }

    void setOpacity(float opacity) {

        if (opacity < 0) {
            opacity = 0;
        } else if (opacity > 1) {
            opacity = 1;
        }

        this.opacity = opacity;
        setOpacity(nativePointer.get(), opacity);
    }

    public StereoMode getStereoMode() {
        return stereoMode;
    }

    public void setStereoMode(StereoMode stereoMode) {
        this.stereoMode = stereoMode;
        setStereoMode(nativePointer.get(), stereoMode.ordinal());
    }

    public boolean getUseChromaKey() {
        return getUseChromaKey(nativePointer.get());
    }

    public void setUseChromaKey(boolean useChromaKey) {
        setUseChromaKey(nativePointer.get(), useChromaKey);
    }

    public void setChromaKeyColor(float r, float g, float b) {
        setChromaKeyColor(nativePointer.get(), r, g, b);
    }

    public enum StereoMode {
        NORMAL,      // 0
        TOP_BOTTOM,  // 1
        BOTTOM_TOP,  // 2
        LEFT_RIGHT,  // 3
        RIGHT_LEFT,  // 4
        TOP_ONLY,    // 5
        BOTTOM_ONLY, // 6
        LEFT_ONLY,   // 7
        RIGHT_ONLY   // 8
    }

    public static abstract class CanvasRenderer {
        public final int width;
        public final int height;
        private boolean dirty = true;

        public CanvasRenderer(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public boolean isDirty() {
            return dirty;
        }

        /**
         * @param canvas Canvas to draw.
         * @return {@code true} to end drawing. No more called in next update. If redrawing is required, call {@link #invalidate()}.
         * {@code false} to continues draw. This behavior can be modified overriding {@link #isDirty()}.
         */
        protected abstract boolean render(Canvas canvas);

        public void invalidate() {
            dirty = true;
        }
    }

    public static class DrawableRenderer extends CanvasRenderer {
        private final Drawable drawable;

        public DrawableRenderer(Drawable drawable) {
            super(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            this.drawable = drawable;
        }

        @Override
        protected boolean render(Canvas canvas) {
            drawable.draw(canvas);
            return true;
        }
    }

    public static class ViewRenderer extends CanvasRenderer {
        private final View view;

        protected ViewRenderer(View view, int width, int height) {
            super(width, height);
            this.view = view;
        }

        public static ViewRenderer from(View view) {
            view.measure(0, 0);
            final int width = view.getMeasuredWidth();
            final int height = view.getMeasuredHeight();
            view.layout(0, 0, width, height);
            return new ViewRenderer(view, width, height);
        }

        /**
         * Check dirty state of view recursively.
         *
         * @param view Checked View.
         * @return Returns true if at least one View is dirty in hierarchy.
         */
        private static boolean isDirty(View view) {

            if (view.isDirty()) return true;

            // Apply this method to all children of view if view is ViewGroup
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
                    if (isDirty(viewGroup.getChildAt(i))) return true;
                }
            }

            return false;
        }

        @Override
        public boolean isDirty() {
            return isDirty(view);
        }

        @Override
        protected boolean render(Canvas canvas) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            view.draw(canvas);
            return true;
        }

        public View getView() {
            return view;
        }
    }
}
