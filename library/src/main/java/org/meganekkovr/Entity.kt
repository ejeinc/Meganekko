package org.meganekkovr

import android.graphics.drawable.Drawable
import android.view.View
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.meganekkovr.animation.EntityAnimator

/**
 * Entity is a basic class in Meganekko. It is something on VR scene.
 * Entity has transform information such as [position][.setPosition],
 * [scale][.setScale], and [rotation][.setRotation].
 * It can have child Entities and [Component]s.
 * Entity which will be rendered has to have [GeometryComponent] and [SurfaceRendererComponent].
 */
open class Entity {
    private val _nativePointer = NativePointer.getInstance(newInstance())

    /**
     * For internal use only.
     *
     * @return native pointer value.
     */
    val nativePointer: Long
        get() {
            return _nativePointer.get()
        }

    private val components = mutableMapOf<Class<out Component>, Component>()

    /**
     * Get children of Entity.
     *
     * @return Children of Entity.
     */
    val children = mutableListOf<Entity>()

    /**
     * Get/Set Entity's local position.
     */
    var position = Vector3f()
        set(position) {
            field.set(position)
            localMatrixUpdateRequired = true
        }

    /**
     * Get/Set Entity's local scale.
     */
    var scale = Vector3f(1f, 1f, 1f)
        set(scale) {
            field.set(scale)
            localMatrixUpdateRequired = true
        }

    /**
     * Get/Set Entity's local rotation.
     */
    var rotation = Quaternionf()
        set(rotation) {
            field.set(rotation)
            localMatrixUpdateRequired = true
        }

    private val localMatrix = Matrix4f()

    /**
     * Get world model matrix.
     *
     * @return World model matrix.
     */
    val worldModelMatrix = Matrix4f()

    private val matrixValues = FloatArray(16)

    /**
     * This method is not valid until this is attached to [Scene].
     */
    // Propagate to children
    var app: MeganekkoApp? = null
        set(app) {
            field = app
            children.forEach { child -> child.app = app }
        }

    /**
     * Parent [Entity].
     */
    var parent: Entity? = null
        private set

    private var localMatrixUpdateRequired = true
    private var worldMatrixUpdateRequired = true

    var id: Int = 0

    /**
     * Get/Set opacity. Actual opacity used in rendering can get with [.getRenderingOpacity].
     */
    var opacity = 1.0f
        set(opacity) {
            // Prevent out of range
            val actualOpacity = if (opacity < 0) {
                0f
            } else if (opacity > 1) {
                1f
            } else {
                opacity
            }

            // Do nothing if previous value is same to new value
            if (this.opacity != actualOpacity) {
                field = actualOpacity
                updateOpacityRequired = true
            }
        }

    private var updateOpacityRequired: Boolean = false

    /**
     * Get/Set visibility of this [Entity]. Note that this will return **its own visibility**.
     * If set to `false`, its children also become not be rendered.
     * Default value is `true`.
     */
    var isVisible = true

    var isRenderable: Boolean = false
        private set // true if this has Geometry and Surface.

    /**
     * Get actual opacity used in rendering.
     * This value can be different with value returned from [.getOpacity].
     *
     * @return Rendering opacity
     */
    val renderingOpacity: Float
        get() = this.opacity * parentOpacity()

    /**
     * @return `true` if this entity and ancestors are all visible. Otherwise `false`.
     */
    // Check parent visibility
    val isShown: Boolean
        get() {

            if (!isVisible) return false

            return parent?.isShown ?: true
        }

    /**
     * Override this to create own native instance.
     * Native class must be derived from `mgn::Entity`.
     *
     * @return Native pointer value return from C++ `new`.
     */
    private external fun newInstance(): Long

    /**
     * Set string id.
     *
     * @param id ID
     */
    fun setId(id: String) {
        this.id = id.hashCode()
    }

    /**
     * Find [Entity] from children. Typically, id is `R.id.xxx` value.
     *
     * @param id ID
     * @return Found Entity or `null` if it has no matched Entity with id.
     */
    fun findById(id: Int): Entity? {

        if (this.id == id) return this

        children.forEach { child ->
            val found = child.findById(id)
            if (found != null) return found
        }

        return null
    }

    /**
     * Find [Entity] from children.
     *
     * @param id ID
     * @return Found Entity or `null` if it has no matched Entity with id.
     */
    fun findById(id: String): Entity? {
        return findById(id.hashCode())
    }

    /**
     * Called at every frame update.
     *
     * @param frame Frame information
     */
    open fun update(frame: FrameInput) {

        // Notify to components
        components.values.forEach { it.update(frame) }

        // Notify to children
        children.forEach { it.update(frame) }

        // Update local model matrix if necessary.
        if (localMatrixUpdateRequired) {
            updateLocalMatrix()
            invalidateWorldModelMatrix()
            localMatrixUpdateRequired = false
        }

        // Update world model matrix if necessary.
        if (worldMatrixUpdateRequired) {
            updateWorldModelMatrix()
            worldMatrixUpdateRequired = false

            // Update native side values
            worldModelMatrix.get(matrixValues)
            setWorldModelMatrix(nativePointer, matrixValues)
        }

        // Update opacity if necessary.
        if (updateOpacityRequired) {
            updateOpacity()
            updateOpacityRequired = false
        }
    }

    /**
     * Update local matrix.
     */
    private fun updateLocalMatrix() {
        localMatrix.identity()
        localMatrix.translate(this.position)
        localMatrix.rotate(this.rotation)
        localMatrix.scale(this.scale)
    }

    /**
     * Update world model matrix.
     */
    private fun updateWorldModelMatrix() {

        // parentMatrix * worldModelMatrix
        val parent = parent
        if (parent != null) {
            val parentMatrix = parent.worldModelMatrix
            parentMatrix.mul(localMatrix, worldModelMatrix)
        } else {

            // worldModelMatrix = localMatrix
            worldModelMatrix.set(localMatrix)
        }
    }

    /**
     * Add [Component]. Note that only one Component can be added per class.
     *
     * @param component Component
     * @return `true` if Successfully added. Otherwise `false`.
     */
    fun add(component: Component): Boolean {
        val componentClass = component.javaClass
        if (!components.containsKey(componentClass)) {
            component.entity = this
            component.onAttach(this)
            components[componentClass] = component

            if (component.javaClass == GeometryComponent::class.java || component.javaClass == SurfaceRendererComponent::class.java) {
                this.isRenderable = hasComponent<GeometryComponent>() && hasComponent<SurfaceRendererComponent>()
            }

            return true
        }
        return false
    }

    /**
     * Remove [Component].
     *
     * @param component Component.
     * @return `true` if Successfully removed. Otherwise `false`.
     */
    fun remove(component: Component): Boolean {
        return remove(component.javaClass)
    }

    /**
     * Remove [Component] with associated class.
     *
     * @param clazz Class of Component.
     * @param <T>   Type
     * @return `true` if Successfully removed. Otherwise `false`.
     */
    fun <T : Component> remove(clazz: Class<T>): Boolean {
        if (components.containsKey(clazz)) {
            val component = components[clazz]
            component!!.onDetach(this)
            component.entity = null
            components.remove(clazz)

            if (clazz == SurfaceRendererComponent::class.java || clazz == GeometryComponent::class.java) {
                this.isRenderable = hasComponent<GeometryComponent>() && hasComponent<SurfaceRendererComponent>()
            }

            return true
        }
        return false
    }

    inline fun <reified T : Component> getComponent(): T? {
        return getComponent(T::class.java)
    }

    fun <T : Component> getComponent(clazz: Class<T>): T? {
        return components[clazz] as T?
    }

    inline fun <reified T : Component> hasComponent(): Boolean {
        return hasComponent(T::class.java)
    }

    fun hasComponent(clazz: Class<out Component>): Boolean {
        return components.containsKey(clazz)
    }

    /**
     * Add child [Entity].
     *
     * @param child Child entity.
     * @return `true` if Successfully added. Otherwise `false`.
     */
    fun add(child: Entity): Boolean {
        val added = children.add(child)
        if (added) {
            child.parent = this
            child.app = this.app
        }
        return added
    }

    /**
     * Remove child [Entity].
     *
     * @param child Child entity.
     * @return `true` if Successfully removed. Otherwise `false`.
     */
    fun remove(child: Entity): Boolean {
        val removed = children.remove(child)
        if (removed) {
            child.parent = null
        }
        return removed
    }

    /**
     * Remove this from scene graph.
     *
     * @return `true` if Successfully removed. Otherwise `false`.
     * @since 3.0.17
     */
    fun remove(): Boolean {
        return parent?.remove(this) ?: false
    }

    private fun invalidateWorldModelMatrix() {

        worldMatrixUpdateRequired = true

        // Propagate to children
        children.forEach(Entity::invalidateWorldModelMatrix)
    }

    /**
     * Set Entity's local position.
     *
     * @param x X component of position
     * @param y Y component of position
     * @param z Z component of position
     */
    fun setPosition(x: Float, y: Float, z: Float) {
        this.position.set(x, y, z)
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local position.
     *
     * @param x X component of position
     */
    fun setX(x: Float) {
        this.position.x = x
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local position.
     *
     * @param y Y component of position
     */
    fun setY(y: Float) {
        this.position.y = y
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local position.
     *
     * @param z Z component of position
     */
    fun setZ(z: Float) {
        this.position.z = z
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local scale.
     *
     * @param x X component of scale
     * @param y Y component of scale
     * @param z Z component of scale
     */
    fun setScale(x: Float, y: Float, z: Float) {
        this.scale.set(x, y, z)
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local scale.
     *
     * @param x X component of scale
     */
    fun setScaleX(x: Float) {
        this.scale.x = x
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local scale.
     *
     * @param y Y component of scale
     */
    fun setScaleY(y: Float) {
        this.scale.y = y
        localMatrixUpdateRequired = true
    }

    /**
     * Set Entity's local scale.
     *
     * @param z Z component of scale
     */
    fun setScaleZ(z: Float) {
        this.scale.z = z
        localMatrixUpdateRequired = true
    }

    /**
     * Get [View]. This works only for Entity which is created by [.from]
     * or has [SurfaceRendererComponent] which is
     * created by [SurfaceRendererComponent.from].
     *
     * @return View
     */
    fun view(): View? {
        val surfaceRendererComponent = getComponent<SurfaceRendererComponent>() ?: return null

        val canvasRenderer = surfaceRendererComponent.canvasRenderer ?: return null

        return (canvasRenderer as? SurfaceRendererComponent.ViewRenderer)?.view

    }

    /**
     * Short hand for `new EntityAnimator(entity)`.
     *
     * @return new EntityAnimator
     */
    fun animate(): EntityAnimator {
        return EntityAnimator(this)
    }

    private fun updateOpacity() {

        val surfaceRendererComponent = getComponent<SurfaceRendererComponent>()
        surfaceRendererComponent?.opacity = renderingOpacity

        children.forEach(Entity::updateOpacity)
    }

    private fun parentOpacity(): Float {
        return if (parent != null) parent!!.opacity * parent!!.parentOpacity() else 1.0f
    }

    /**
     * Returns the component of type in the [Entity] or any of its children using depth first search.
     *
     * @param type Component class
     * @return Found [Component]. If no one is found, null.
     */
    fun <T : Component> getComponentInChildren(type: Class<T>): T? {

        children.forEach { child ->
            val component = child.getComponentInChildren(type)
            if (component != null) {
                return component
            }
        }

        return getComponent(type)
    }

    /**
     * Returns the component of type in the [Entity] or any of its parents.
     * Recurses upwards until it finds a valid component. Returns null if no component found.
     *
     * @param type Component class
     * @return Found [Component]. If no one is found, null.
     */
    fun <T : Component> getComponentInParent(type: Class<T>): T? {

        return if (hasComponent(type)) {
            getComponent(type)
        } else {
            parent?.getComponentInParent(type)
        }
    }

    private external fun setWorldModelMatrix(nativePtr: Long, matrix: FloatArray)

    companion object {

        /**
         * Create Entity from [View]. New Entity has plane geometry.
         *
         * @param view View for surface.
         * @return new Entity
         */
        fun from(view: View): Entity {

            return Entity().apply {
                add(SurfaceRendererComponent.from(view))
                add(GeometryComponent.from(view))
            }
        }

        /**
         * Create Entity from [Drawable]. New Entity has plane geometry.
         *
         * @param drawable Drawable for surface.
         * @return new Entity
         */
        fun from(drawable: Drawable): Entity {

            return Entity().apply {
                add(SurfaceRendererComponent.from(drawable))
                add(GeometryComponent.from(drawable))
            }
        }
    }
}
