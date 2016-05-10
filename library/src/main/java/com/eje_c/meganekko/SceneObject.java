/* 
 * Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.XmlRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.eje_c.meganekko.animation.PositionUpdateListener;
import com.eje_c.meganekko.animation.QuaternionEvaluator;
import com.eje_c.meganekko.animation.RotationUpdateListener;
import com.eje_c.meganekko.animation.ScaleUpdateListener;
import com.eje_c.meganekko.animation.VectorEvaluator;
import com.eje_c.meganekko.utility.Log;
import com.eje_c.meganekko.xml.XmlSceneObjectParser;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * One of the key Meganekko classes: a scene object.
 * <p/>
 * Every scene object has children. An invisible scene object can be used to
 * move a set of scene as a unit, preserving their relative geometry. Invisible
 * scene objects don't need any {@linkplain SceneObject#getRenderData() render
 * data.}
 * <p/>
 * <p/>
 * Visible scene objects must have render data
 * {@linkplain SceneObject#attachRenderData(RenderData) attached.} Each
 * {@link RenderData} has a {@link Mesh GL mesh} that defines its geometry, and
 * a {@link Material} that defines its surface.
 */
public class SceneObject extends HybridObject {
    // Temp array for get values from JNI.
    // You have to surround with
    // synchronized (sTempValuesForJni) { ... } block to prevent
    protected static final float[] sTempValuesForJni = new float[16];

    private static final String TAG = SceneObject.class.getSimpleName();
    private final List<SceneObject> mChildren = new ArrayList<>();
    private final Set<KeyEventListener> mKeyEventListeners = new HashSet<>();
    private int mId;
    private String mName;
    private RenderData mRenderData;
    private SceneObject mParent;
    private float mOpacity = 1.0f;
    private boolean mVisible = true;

    /**
     * Constructs an empty scene object with a default transform.
     */
    public SceneObject() {
    }

    /**
     * Constructs a scene object with an arbitrarily complex mesh.
     *
     * @param mesh a {@link Mesh}.
     */
    public SceneObject(Mesh mesh) {
        RenderData renderData = new RenderData();
        attachRenderData(renderData);
        renderData.setMesh(mesh);
    }

    /**
     * Create {@link SceneObject} from {@code View}.
     *
     * @param view
     * @return
     */
    public static SceneObject from(View view) {
        SceneObject sceneObject = new SceneObject();
        sceneObject.material(Material.from(view));
        sceneObject.updateViewLayout();
        sceneObject.mesh(Mesh.from(view));
        return sceneObject;
    }

    /**
     * Create {@link SceneObject} from layout XML.
     *
     * @param context
     * @param layoutRes
     * @return
     */
    public static SceneObject fromLayout(Context context, @LayoutRes int layoutRes) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null);
        return from(view);
    }

    /**
     * Create {@link SceneObject} from {@code Drawable}.
     *
     * @param drawable
     * @return
     */
    public static SceneObject from(Drawable drawable) {
        SceneObject sceneObject = new SceneObject();
        sceneObject.mesh(Mesh.from(drawable));
        sceneObject.material(Material.from(drawable));
        return sceneObject;
    }

    /**
     * Create {@link SceneObject} from drawable XML.
     *
     * @param context
     * @param drawableRes
     * @return
     */
    public static SceneObject fromDrawable(Context context, @DrawableRes int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        return from(drawable);
    }

    /**
     * Create {@link SceneObject} from XML.
     *
     * @param context
     * @param xmlRes
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static SceneObject fromXML(Context context, @XmlRes int xmlRes) throws IOException, XmlPullParserException {
        XmlSceneObjectParser parser = new XmlSceneObjectParser(context);
        return parser.parse(context.getResources().getXml(xmlRes));
    }

    private static native void attachRenderData(long sceneObject, long renderData);

    private static native void detachRenderData(long sceneObject);

    private static native void addChildObject(long sceneObject, long child);

    private static native void removeChildObject(long sceneObject, long child);

    private static native boolean isColliding(long sceneObject, long otherObject);

    private static native void setLODRange(long sceneObject, float minRange, float maxRange);

    private static native float getLODMinRange(long sceneObject);

    private static native float getLODMaxRange(long sceneObject);

    private static native void setPosition(long sceneObject, float x, float y, float z);

    private static native void setScale(long sceneObject, float x, float y, float z);

    private static native void setRotation(long sceneObject, float x, float y, float z, float w);

    private static native void getPosition(long sceneObject, float[] val);

    private static native void getScale(long sceneObject, float[] val);

    private static native void getRotation(long sceneObject, float[] val);

    private static native void setModelMatrix(long sceneObject,
                                              float m11, float m12, float m13, float m14,
                                              float m21, float m22, float m23, float m24,
                                              float m31, float m32, float m33, float m34,
                                              float m41, float m42, float m43, float m44);

    private static native void getModelMatrix(long sceneObject, float[] val);

    @Override
    protected native long initNativeInstance();

    /**
     * Get the (optional) ID of the object.
     *
     * @return The ID of the object. If no name has been assigned, return 0.
     */
    public int getId() {
        return mId;
    }

    /**
     * Set the (optional) ID of the object.
     * <p/>
     * Scene object IDs are not needed: they are only for the application's
     * convenience.
     *
     * @param id ID of the object.
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Get the (optional) name of the object.
     *
     * @return The name of the object. If no name has been assigned, the
     * returned string will be empty.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set the (optional) name of the object.
     * <p/>
     * Scene object names are not needed: they are only for the application's
     * convenience.
     *
     * @param name Name of the object.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Attach {@linkplain RenderData rendering data} to the object.
     * <p/>
     * If other rendering data is currently attached, it is replaced with the
     * new data. {@link RenderData} contains the GL mesh, the texture, the
     * shader id, and various shader constants.
     *
     * @param renderData New rendering data.
     */
    public void attachRenderData(RenderData renderData) {
        mRenderData = renderData;
        renderData.setOwnerObject(this);
        attachRenderData(getNative(), renderData.getNative());
    }

    /**
     * Detach the object's current {@linkplain RenderData rendering data}.
     * <p/>
     * An object with no {@link RenderData} is not visible.
     */
    public void detachRenderData() {
        if (mRenderData != null) {
            mRenderData.setOwnerObject(null);
        }
        mRenderData = null;
        detachRenderData(getNative());
    }

    /**
     * Get the current {@link RenderData}.
     *
     * @return The current {@link RenderData rendering data}. If no rendering
     * data is currently attached to the object, returns {@code null}.
     */
    public RenderData getRenderData() {
        return mRenderData;
    }

    /**
     * Get the {@linkplain SceneObject parent object.}
     * <p/>
     * If the object has been {@link #addChildObject(SceneObject) added as a
     * child} to another {@link SceneObject}, returns that object. Otherwise,
     * returns {@code null}.
     *
     * @return The parent {@link SceneObject} or {@code null}.
     */
    public SceneObject getParent() {
        return mParent;
    }

    /**
     * Add {@code child} as a child of this object.
     *
     * @param child {@link SceneObject Object} to add as a child of this object.
     */
    public void addChildObject(SceneObject child) {
        mChildren.add(child);
        child.mParent = this;
        child.updateOpacity();
        addChildObject(getNative(), child.getNative());
    }

    /**
     * Remove {@code child} as a child of this object.
     *
     * @param child {@link SceneObject Object} to remove as a child of this
     *              object.
     */
    public void removeChildObject(SceneObject child) {
        mChildren.remove(child);
        child.mParent = null;
        removeChildObject(getNative(), child.getNative());
    }

    /**
     * Check if {@code otherObject} is colliding with this object.
     *
     * @param otherObject {@link SceneObject Object} to check for collision with this
     *                    object.
     * @return {@code true) if objects collide, {@code false} otherwise
     */
    public boolean isColliding(SceneObject otherObject) {
        return isColliding(getNative(), otherObject.getNative());
    }

    /**
     * Sets the range of distances from the camera where this object will be
     * shown.
     *
     * @param minRange The closest distance to the camera in which this object should
     *                 be shown. This should be a positive number between 0 and
     *                 Float.MAX_VALUE.
     * @param maxRange The farthest distance to the camera in which this object
     *                 should be shown. This should be a positive number between 0
     *                 and Float.MAX_VALUE.
     */
    public void setLODRange(float minRange, float maxRange) {
        if (minRange < 0 || maxRange < 0) {
            throw new IllegalArgumentException(
                    "minRange and maxRange must be between 0 and Float.MAX_VALUE");
        }
        if (minRange > maxRange) {
            throw new IllegalArgumentException(
                    "minRange should not be greater than maxRange");
        }
        setLODRange(getNative(), minRange, maxRange);
    }

    /**
     * Get the minimum distance from the camera in which to show this object.
     *
     * @return the minimum distance from the camera in which to show this
     * object. Default value is 0.
     */
    public float getLODMinRange() {
        return getLODMinRange(getNative());
    }

    /**
     * Get the maximum distance from the camera in which to show this object.
     *
     * @return the maximum distance from the camera in which to show this
     * object. Default value is Float.MAX_VALUE.
     */
    public float getLODMaxRange() {
        return getLODMaxRange(getNative());
    }

    /**
     * Get the number of child objects.
     *
     * @return Number of {@link SceneObject objects} added as children of this
     * object.
     */
    public int getChildrenCount() {
        return mChildren.size();
    }

    /**
     * Get the child object at {@code index}.
     *
     * @param index Position of the child to get.
     * @return {@link SceneObject Child object}.
     * @throws {@link java.lang.IndexOutOfBoundsException} if there is no child at
     *                that position.
     */
    public SceneObject getChildByIndex(int index) {
        return mChildren.get(index);
    }

    /**
     * Get all the children, in a single list.
     *
     * @return An un-modifiable list of this object's children.
     */
    public List<SceneObject> getChildren() {
        return Collections.unmodifiableList(mChildren);
    }

    /**
     * Get visibility set by {@link SceneObject#setVisible(boolean) setVisible()}.
     *
     * @return Visibility of this object.
     */
    public boolean isVisible() {
        return mVisible;
    }

    /**
     * Set visibility of this object. This affects also all children of this
     * object.
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.mVisible = visible;
        updateVisibility();
    }

    /**
     * Get if object is shown to scene.
     *
     * @return Visibility of this object. Even if isVisible() == true, it can
     * return false if this parent.isVisible() == false.
     */
    public boolean isShown() {

        if (!mVisible)
            return mVisible;

        if (getParent() == null)
            return mVisible;

        return mVisible && getParent().isShown();
    }

    private void updateVisibility() {
        RenderData renderData = getRenderData();
        if (renderData != null) {
            boolean visible = isShown();
            renderData.setVisible(visible);
        }

        for (SceneObject child : mChildren) {
            child.updateVisibility();
        }
    }

    /**
     * Get opacity set by {@link SceneObject#setOpacity(float) setOpacity()}.
     *
     * @return opacity
     */
    public float getOpacity() {
        return mOpacity;
    }

    /**
     * Set opacity of this object. This affects also all children of this
     * object.
     *
     * @param opacity
     */
    public void setOpacity(float opacity) {
        this.mOpacity = opacity;
        updateOpacity();
    }

    private float getInternalOpacity() {
        float parentOpacity = getParent() != null ? getParent().getInternalOpacity() : 1.0f;
        return mOpacity * parentOpacity;
    }

    private void updateOpacity() {
        RenderData renderData = getRenderData();
        if (renderData != null) {
            Material material = renderData.getMaterial();
            if (material != null) {
                float opacity = getInternalOpacity();
                material.setOpacity(opacity);
            }
        }

        for (SceneObject child : mChildren) {
            child.updateOpacity();
        }
    }

    public SceneObject findObjectById(int id) {
        if (mId == id) {
            return this;
        }

        for (SceneObject child : mChildren) {
            SceneObject result = child.findObjectById(id);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public SceneObject findObjectByName(String name) {
        if (name.equals(getName())) {
            return this;
        }

        for (SceneObject child : mChildren) {
            SceneObject result = child.findObjectByName(name);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public void update(Frame frame) {

        if (mRenderData != null) {
            Material material = mRenderData.getMaterial();

            if (material != null) {
                material.update(frame);
            }
        }

        for (SceneObject child : mChildren) {
            child.update(frame);
        }
    }

    /**
     * Get {@link Scene}. If this object is not in scene, return null.
     * This method uses recursive call. So you should not call it in render loop.
     *
     * @return
     */
    public Scene getScene() {

        if (this instanceof Scene) {
            return (Scene) this;
        }

        SceneObject parent = getParent();
        if (parent == null) {
            return null;
        }

        return parent.getScene();
    }

    public boolean onKeyShortPress(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyShortPress(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyShortPress(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean onKeyDoubleTap(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyDoubleTap(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyDoubleTap(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean onKeyLongPress(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyLongPress(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyLongPress(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean onKeyDown(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyDown(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyDown(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean onKeyUp(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyUp(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyUp(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean onKeyMax(int keyCode, int repeatCount) {

        for (KeyEventListener l : mKeyEventListeners) {
            if (l.onKeyMax(keyCode, repeatCount)) {
                return true;
            }
        }

        for (SceneObject child : mChildren) {
            if (child.onKeyMax(keyCode, repeatCount)) {
                return true;
            }
        }

        return false;
    }

    public boolean addKeyEventListener(KeyEventListener listener) {
        return mKeyEventListeners.add(listener);
    }

    public boolean removeKeyEventListener(KeyEventListener listener) {
        return mKeyEventListeners.remove(listener);
    }

    /*
     * Simple APIs
     */

    public void position(Vector3f position) {
        setPosition(getNative(), position.x, position.y, position.z);
    }

    public Vector3f position() {
        synchronized (sTempValuesForJni) {
            getPosition(getNative(), sTempValuesForJni);
            return new Vector3f(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2]);
        }
    }

    public void scale(Vector3f scale) {
        setScale(getNative(), scale.x, scale.y, scale.z);
    }

    public Vector3f scale() {
        synchronized (sTempValuesForJni) {
            getScale(getNative(), sTempValuesForJni);
            return new Vector3f(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2]);
        }
    }

    public void rotation(Quaternionf rotation) {
        setRotation(getNative(), rotation.x, rotation.y, rotation.z, rotation.w);
    }

    public Quaternionf rotation() {
        synchronized (sTempValuesForJni) {
            getRotation(getNative(), sTempValuesForJni);
            return new Quaternionf(sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2], sTempValuesForJni[3]);
        }
    }

    public void modelMatrix(Matrix4f m) {
        setModelMatrix(getNative(),
                m.m00, m.m01, m.m02, m.m03,
                m.m10, m.m11, m.m12, m.m13,
                m.m20, m.m21, m.m22, m.m23,
                m.m30, m.m31, m.m32, m.m33
        );
    }

    public Matrix4f modelMatrix() {
        synchronized (sTempValuesForJni) {
            getModelMatrix(getNative(), sTempValuesForJni);
            return new Matrix4f(
                    sTempValuesForJni[0], sTempValuesForJni[1], sTempValuesForJni[2], sTempValuesForJni[3],
                    sTempValuesForJni[4], sTempValuesForJni[5], sTempValuesForJni[6], sTempValuesForJni[7],
                    sTempValuesForJni[8], sTempValuesForJni[9], sTempValuesForJni[10], sTempValuesForJni[11],
                    sTempValuesForJni[12], sTempValuesForJni[13], sTempValuesForJni[14], sTempValuesForJni[15]);
        }
    }

    public Material material() {
        return mRenderData != null ? getRenderData().getMaterial() : null;
    }

    public void material(Material material) {

        // Ensure to have RenderData
        if (mRenderData == null) {
            attachRenderData(new RenderData());
        }

        mRenderData.setMaterial(material);
    }

    public Mesh mesh() {
        return mRenderData != null ? getRenderData().getMesh() : null;
    }

    public void mesh(Mesh mesh) {

        // Ensure to have RenderData
        if (mRenderData == null) {
            attachRenderData(new RenderData());
        }

        mRenderData.setMesh(mesh);
    }

    /**
     * Get attached view.
     *
     * @return
     */
    public View view() {

        Material material = material();
        if (material == null) {
            Log.d(TAG, "Material is not attached");
            return null;
        }

        Texture.CanvasRenderer renderer = material.texture().getRenderer();
        if (renderer == null) {
            Log.d(TAG, "Texture renderer is not attached");
            return null;
        } else if (!(renderer instanceof Texture.ViewRenderer)) {
            Log.d(TAG, "Texture renderer is not an instance of ViewRenderer");
            return null;
        }

        return ((Texture.ViewRenderer) renderer).getView();
    }

    /**
     * Attach view as Texture.
     *
     * @param view
     */
    public void view(View view) {

        // Ensure SceneObject has material
        Material material = material();
        if (material == null) {
            attachRenderData(new RenderData());
            material = material();
        }

        material.texture().set(view);
    }

    public void updateViewLayout() {
        updateViewLayout(false);
    }

    /**
     * Call this when you update {@code View} size after rendered.
     *
     * @param updateMeshToView Update mesh to new View size.
     */
    public void updateViewLayout(boolean updateMeshToView) {
        View view = view();
        if (view == null) return;

        view.measure(0, 0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        if (updateMeshToView) {
            mesh(Mesh.from(view));
        }
    }

    public SceneObjectAnimator animate() {
        return new SceneObjectAnimator();
    }

    public class SceneObjectAnimator {
        private Runnable callback;
        private List<Animator> animators = new ArrayList<>();
        private long duration = -1;
        private TimeInterpolator interpolator;
        private boolean sequential;
        private long delay;
        private AnimatorSet animator;

        // For sequential animation
        private Vector3f lastPos, lastScale;
        private Quaternionf lastRotation;
        private float lastOpacity = getOpacity();

        public SceneObjectAnimator moveTo(Vector3f position) {
            Vector3f fromPos = lastPos != null ? lastPos : position();
            ValueAnimator animator = ValueAnimator.ofObject(new VectorEvaluator(), fromPos, position);
            animator.addUpdateListener(new PositionUpdateListener(SceneObject.this));
            animators.add(animator);
            lastPos = position;
            return this;
        }

        public SceneObjectAnimator moveBy(Vector3f translation) {
            Vector3f toPosition = new Vector3f();
            position().add(translation, toPosition);
            return moveTo(toPosition);
        }

        public SceneObjectAnimator scaleTo(Vector3f scale) {
            Vector3f fromScale = lastScale != null ? lastScale : scale();
            ValueAnimator animator = ValueAnimator.ofObject(new VectorEvaluator(), fromScale, scale);
            animator.addUpdateListener(new ScaleUpdateListener(SceneObject.this));
            animators.add(animator);
            lastScale = scale;
            return this;
        }

        public SceneObjectAnimator scaleBy(Vector3f scale) {
            Vector3f toScale = new Vector3f();
            scale().mul(scale, toScale);
            return scaleTo(toScale);
        }

        public SceneObjectAnimator rotateTo(Quaternionf rotation) {
            Quaternionf fromRotation = lastRotation != null ? lastRotation : rotation();
            ValueAnimator animator = ValueAnimator.ofObject(new QuaternionEvaluator(), fromRotation, rotation);
            animator.addUpdateListener(new RotationUpdateListener(SceneObject.this));
            animators.add(animator);
            lastRotation = rotation;
            return this;
        }

        public SceneObjectAnimator rotateTo(float x, float y, float z) {
            Quaternionf q = new Quaternionf();
            q.rotate(x, y, z);
            return rotateTo(q);
        }

        public SceneObjectAnimator rotateBy(Quaternionf rotate) {
            Quaternionf toRotation = new Quaternionf();
            rotation().mul(rotate, toRotation);
            return rotateTo(toRotation);
        }

        public SceneObjectAnimator rotateBy(float x, float y, float z) {
            Quaternionf q = new Quaternionf();
            q.rotate(x, y, z);
            return rotateBy(q);
        }

        public SceneObjectAnimator opacity(float opacity) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(SceneObject.this, "opacity", lastOpacity, opacity);
            animators.add(animator);
            lastOpacity = opacity;
            return this;
        }

        public SceneObjectAnimator onEnd(Runnable callback) {
            this.callback = callback;
            return this;
        }

        public SceneObjectAnimator interpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public SceneObjectAnimator duration(long duration) {
            this.duration = duration;
            return this;
        }

        public SceneObjectAnimator sequential(boolean sequential) {
            this.sequential = sequential;
            return this;
        }

        public SceneObjectAnimator delay(long delay) {
            this.delay = delay;
            return this;
        }

        public SceneObjectAnimator start(MeganekkoApp app) {

            if (animator == null) {
                setupAnimator();
            }

            app.animate(animator, callback);
            return this;
        }

        public SceneObjectAnimator setupAnimator() {
            this.animator = new AnimatorSet();

            if (sequential) {
                animator.playSequentially(animators);
            } else {
                animator.playTogether(animators);
            }

            if (duration >= 0) {
                animator.setDuration(duration);
            }

            if (interpolator != null) {
                animator.setInterpolator(interpolator);
            }

            if (delay > 0) {
                animator.setStartDelay(delay);
            }

            return this;
        }

        /**
         * Get {@code Animator} of this.
         * Until {@link #start(MeganekkoApp)} or {@link #setupAnimator()} was called,
         * this method always return null.
         *
         * @return
         */
        public Animator getAnimator() {
            return animator;
        }
    }
}
