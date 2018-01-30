package org.meganekkovr;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.View;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.meganekkovr.animation.EntityAnimator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Entity is a basic class in Meganekko. It is something on VR scene.
 * Entity has transform information such as {@link #setPosition(float, float, float) position},
 * {@link #setScale(float, float, float) scale}, and {@link #setRotation(Quaternionf) rotation}.
 * It can have child Entities and {@link Component}s.
 * Entity which will be rendered has to have {@link GeometryComponent} and {@link SurfaceRendererComponent}.
 */
public class Entity {
    private final NativePointer nativePointer;
    private final Map<Class<? extends Component>, Component> components = new ArrayMap<>();
    private final List<Entity> children = new CopyOnWriteArrayList<>();
    private final Vector3f position = new Vector3f();
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Quaternionf rotation = new Quaternionf();
    private final Matrix4f localMatrix = new Matrix4f();
    private final Matrix4f worldModelMatrix = new Matrix4f();
    private final float[] matrixValues = new float[16];
    private MeganekkoApp app;
    private Entity parent;
    private boolean localMatrixUpdateRequired = true;
    private boolean worldMatrixUpdateRequired = true;
    private int id;
    private float opacity = 1.0f;
    private boolean updateOpacityRequired;
    private boolean visible = true;
    private boolean renderable; // true if this has Geometry and Surface.

    public Entity() {
        nativePointer = NativePointer.getInstance(newInstance());
    }

    private static native void setWorldModelMatrix(long nativePtr, float[] matrix);

    /**
     * Create Entity from {@link View}. New Entity has plane geometry.
     *
     * @param view View for surface.
     * @return new Entity
     */
    @NonNull
    public static Entity from(@NonNull View view) {

        final Entity entity = new Entity();
        entity.add(SurfaceRendererComponent.from(view));
        entity.add(GeometryComponent.from(view));

        return entity;
    }

    /**
     * Create Entity from {@link Drawable}. New Entity has plane geometry.
     *
     * @param drawable Drawable for surface.
     * @return new Entity
     */
    @NonNull
    public static Entity from(@NonNull Drawable drawable) {

        final Entity entity = new Entity();
        entity.add(SurfaceRendererComponent.from(drawable));
        entity.add(GeometryComponent.from(drawable));

        return entity;
    }

    /**
     * Override this to create own native instance.
     * Native class must be derived from {@code mgn::Entity}.
     *
     * @return Native pointer value return from C++ {@code new}.
     */
    protected native long newInstance();

    /**
     * This method is not valid until this is attached to {@link Scene}.
     *
     * @return MeganekkoApp.
     */
    public MeganekkoApp getApp() {
        return app;
    }

    /**
     * For internal use only.
     *
     * @param app
     */
    void setApp(@NonNull MeganekkoApp app) {
        this.app = app;

        // Propagate to children
        for (Entity child : children) {
            child.setApp(app);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Set string id.
     *
     * @param id ID
     */
    public void setId(@NonNull String id) {
        setId(id.hashCode());
    }

    /**
     * Find {@link Entity} from children. Typically, id is {@code R.id.xxx} value.
     *
     * @param id ID
     * @return Found Entity or {@code null} if it has no matched Entity with id.
     */
    @Nullable
    public Entity findById(int id) {

        if (this.id == id) return this;

        for (Entity child : children) {
            Entity found = child.findById(id);
            if (found != null) return found;
        }

        return null;
    }

    /**
     * Find {@link Entity} from children.
     *
     * @param id ID
     * @return Found Entity or {@code null} if it has no matched Entity with id.
     */
    public Entity findById(@NonNull String id) {
        return findById(id.hashCode());
    }

    /**
     * Called at every frame update.
     *
     * @param frame Frame information
     */
    public void update(@NonNull FrameInput frame) {

        // Notify to components
        for (Component component : components.values()) {
            component.update(frame);
        }

        // Notify to children
        for (Entity child : children) {
            child.update(frame);
        }

        // Update local model matrix if necessary.
        if (localMatrixUpdateRequired) {
            updateLocalMatrix();
            invalidateWorldModelMatrix();
            localMatrixUpdateRequired = false;
        }

        // Update world model matrix if necessary.
        if (worldMatrixUpdateRequired) {
            updateWorldModelMatrix();
            worldMatrixUpdateRequired = false;

            // Update native side values
            worldModelMatrix.get(matrixValues);
            setWorldModelMatrix(nativePointer.get(), matrixValues);
        }

        // Update opacity if necessary.
        if (updateOpacityRequired) {
            updateOpacity();
            updateOpacityRequired = false;
        }
    }

    /**
     * Update local matrix.
     */
    public void updateLocalMatrix() {
        localMatrix.identity();
        localMatrix.translate(position);
        localMatrix.rotate(rotation);
        localMatrix.scale(scale);
    }

    /**
     * Update world model matrix.
     */
    public void updateWorldModelMatrix() {

        // parentMatrix * worldModelMatrix
        Entity parent = getParent();
        if (parent != null) {
            Matrix4f parentMatrix = parent.getWorldModelMatrix();
            parentMatrix.mul(localMatrix, worldModelMatrix);
        } else {

            // worldModelMatrix = localMatrix
            worldModelMatrix.set(localMatrix);
        }
    }

    /**
     * Get world model matrix.
     *
     * @return World model matrix.
     */
    @NonNull
    public Matrix4f getWorldModelMatrix() {
        return worldModelMatrix;
    }

    /**
     * Add {@link Component}. Note that only one Component can be added per class.
     *
     * @param component Component
     * @return {@code true} if Successfully added. Otherwise {@code false}.
     */
    public boolean add(@NonNull Component component) {
        final Class<? extends Component> componentClass = component.getClass();
        if (!components.containsKey(componentClass)) {
            component.setEntity(this);
            component.onAttach(this);
            components.put(componentClass, component);

            if (component.getClass().equals(GeometryComponent.class) || component.getClass().equals(SurfaceRendererComponent.class)) {
                this.renderable = hasComponent(GeometryComponent.class) && hasComponent(SurfaceRendererComponent.class);
            }

            return true;
        }
        return false;
    }

    /**
     * Remove {@link Component}.
     *
     * @param component Component.
     * @return {@code true} if Successfully removed. Otherwise {@code false}.
     */
    public boolean remove(@NonNull Component component) {
        return remove(component.getClass());
    }

    /**
     * Remove {@link Component} with associated class.
     *
     * @param clazz Class of Component.
     * @param <T>   Type
     * @return {@code true} if Successfully removed. Otherwise {@code false}.
     */
    public <T extends Component> boolean remove(@NonNull Class<T> clazz) {
        if (components.containsKey(clazz)) {
            Component component = components.get(clazz);
            component.onDetach(this);
            component.setEntity(null);
            components.remove(clazz);

            if (clazz.equals(SurfaceRendererComponent.class) || clazz.equals(GeometryComponent.class)) {
                this.renderable = hasComponent(GeometryComponent.class) && hasComponent(SurfaceRendererComponent.class);
            }

            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(@NonNull Class<T> clazz) {
        return (T) components.get(clazz);
    }

    public boolean hasComponent(@NonNull Class<? extends Component> clazz) {
        return components.containsKey(clazz);
    }

    /**
     * Add child {@link Entity}.
     *
     * @param child Child entity.
     * @return {@code true} if Successfully added. Otherwise {@code false}.
     */
    public boolean add(@NonNull Entity child) {
        final boolean added = children.add(child);
        if (added) {
            child.parent = this;
            child.setApp(app);
        }
        return added;
    }

    /**
     * Remove child {@link Entity}.
     *
     * @param child Child entity.
     * @return {@code true} if Successfully removed. Otherwise {@code false}.
     */
    public boolean remove(@NonNull Entity child) {
        final boolean removed = children.remove(child);
        if (removed) {
            child.parent = null;
        }
        return removed;
    }

    /**
     * Remove this from scene graph.
     *
     * @return {@code true} if Successfully removed. Otherwise {@code false}.
     * @since 3.0.17
     */
    public boolean remove() {
        return parent != null && parent.remove(this);
    }

    /**
     * Get children of Entity. Returned {@link List} can not be modified.
     *
     * @return Children of Entity.
     */
    @NonNull
    public List<Entity> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Get parent {@link Entity}.
     *
     * @return Parent entity.
     */
    @Nullable
    public Entity getParent() {
        return parent;
    }

    /**
     * For internal use only.
     *
     * @return native pointer value.
     */
    public long getNativePointer() {
        return nativePointer.get();
    }

    private void invalidateWorldModelMatrix() {

        worldMatrixUpdateRequired = true;

        // Propagate to children
        for (Entity child : children) {
            child.invalidateWorldModelMatrix();
        }
    }

    /**
     * Set Entity's local position.
     *
     * @param x X component of position
     * @param y Y component of position
     * @param z Z component of position
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local position.
     *
     * @param x X component of position
     */
    public void setX(float x) {
        this.position.x = x;
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local position.
     *
     * @param y Y component of position
     */
    public void setY(float y) {
        this.position.y = y;
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local position.
     *
     * @param z Z component of position
     */
    public void setZ(float z) {
        this.position.z = z;
        localMatrixUpdateRequired = true;
    }

    /**
     * Get Entity's local position.
     *
     * @return position
     */
    @NonNull
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Set Entity's local position.
     *
     * @param position position
     */
    public void setPosition(@NonNull Vector3f position) {
        this.position.set(position);
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local scale.
     *
     * @param x X component of scale
     * @param y Y component of scale
     * @param z Z component of scale
     */
    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local scale.
     *
     * @param x X component of scale
     */
    public void setScaleX(float x) {
        this.scale.x = x;
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local scale.
     *
     * @param y Y component of scale
     */
    public void setScaleY(float y) {
        this.scale.y = y;
        localMatrixUpdateRequired = true;
    }

    /**
     * Set Entity's local scale.
     *
     * @param z Z component of scale
     */
    public void setScaleZ(float z) {
        this.scale.z = z;
        localMatrixUpdateRequired = true;
    }

    /**
     * Get Entity's local scale.
     *
     * @return scale
     */
    @NonNull
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Set Entity's local scale.
     *
     * @param scale scale
     */
    public void setScale(@NonNull Vector3f scale) {
        this.scale.set(scale);
        localMatrixUpdateRequired = true;
    }

    /**
     * Get Entity's local rotation.
     *
     * @return rotation
     */
    @NonNull
    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Set rotation, as a quaternion. Sets the transform's current rotation in quaternion terms.
     *
     * @param rotation rotation
     */
    public void setRotation(@NonNull Quaternionf rotation) {
        this.rotation.set(rotation);
        localMatrixUpdateRequired = true;
    }

    /**
     * Get {@link View}. This works only for Entity which is created by {@link #from(View)}
     * or has {@link SurfaceRendererComponent} which is
     * created by {@link SurfaceRendererComponent#from(View)}.
     *
     * @return View
     */
    @Nullable
    public View view() {
        SurfaceRendererComponent surfaceRendererComponent = getComponent(SurfaceRendererComponent.class);
        if (surfaceRendererComponent == null) {
            return null;
        }

        SurfaceRendererComponent.CanvasRenderer canvasRenderer = surfaceRendererComponent.getCanvasRenderer();
        if (canvasRenderer == null) {
            return null;
        }

        if (canvasRenderer instanceof SurfaceRendererComponent.ViewRenderer) {
            return ((SurfaceRendererComponent.ViewRenderer) canvasRenderer).getView();
        }

        return null;
    }

    /**
     * Short hand for {@code new EntityAnimator(entity)}.
     *
     * @return new EntityAnimator
     */
    @NonNull
    public EntityAnimator animate() {
        return new EntityAnimator(this);
    }

    private void updateOpacity() {

        SurfaceRendererComponent surfaceRendererComponent = getComponent(SurfaceRendererComponent.class);
        if (surfaceRendererComponent != null) {
            surfaceRendererComponent.setOpacity(getRenderingOpacity());
        }

        for (Entity child : children) {
            child.updateOpacity();
        }
    }

    private float parentOpacity() {
        return parent != null ? parent.getOpacity() * parent.parentOpacity() : 1.0f;
    }

    /**
     * Get actual opacity used in rendering.
     * This value can be different with value returned from {@link #getOpacity()}.
     *
     * @return Rendering opacity
     */
    public float getRenderingOpacity() {
        return opacity * parentOpacity();
    }

    /**
     * Get opacity set with {@link #setOpacity(float)}.
     * Actual opacity used in rendering can get with {@link #getRenderingOpacity()}.
     *
     * @return opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Set Entity's opacity.
     *
     * @param opacity Float in the range of 0.0 - 1.0 indicating how transparent the Entity is.
     */
    public void setOpacity(float opacity) {

        // Prevent out of range
        if (opacity < 0) {
            opacity = 0;
        } else if (opacity > 1) {
            opacity = 1;
        }

        // Do nothing if previous value is same to new value
        if (this.opacity == opacity) return;

        this.opacity = opacity;
        updateOpacityRequired = true;
    }

    /**
     * Get visibility of this {@link Entity}. Note that this will return <b>its own visibility</b>.
     *
     * @return visibility
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set visibility. If set to {@code false}, its children also become not be rendered.
     * Default value is {@code true}.
     *
     * @param visible visibility
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return {@code true} if this entity and ancestors are all visible. Otherwise {@code false}.
     */
    public boolean isShown() {

        if (!visible) return false;

        // Check parent visibility
        Entity parent = getParent();
        if (parent == null) return true;

        return parent.isShown();
    }

    public boolean isRenderable() {
        return renderable;
    }

    /**
     * Returns the component of type in the {@link Entity} or any of its children using depth first search.
     *
     * @param type Component class
     * @return Found {@link Component}. If no one is found, null.
     */
    public <T extends Component> T getComponentInChildren(@NonNull Class<T> type) {

        for (Entity child : children) {
            T component = child.getComponentInChildren(type);
            if (component != null) {
                return component;
            }
        }

        return getComponent(type);
    }

    /**
     * Returns the component of type in the {@link Entity} or any of its parents.
     * Recurses upwards until it finds a valid component. Returns null if no component found.
     *
     * @param type Component class
     * @return Found {@link Component}. If no one is found, null.
     */
    public <T extends Component> T getComponentInParent(@NonNull Class<T> type) {

        if (hasComponent(type)) {
            return getComponent(type);
        }

        return parent != null ? parent.getComponentInParent(type) : null;
    }
}
