package org.meganekkovr

import android.content.Context

/**
 * Component is a basic class in Meganekko. It attaches some actions or behaviors to [Entity].
 */
abstract class Component {

    /**
     * Get attached [Entity].
     *
     * @return Entity which is attached to this Component.
     */
    var entity: Entity? = null
        internal set

    /**
     * @return `true` if attached.
     */
    val isAttached: Boolean
        get() = entity != null

    /**
     * Get Android context.
     *
     * @return Android context
     */
    val context: Context
        get() = entity!!.app!!.context

    /**
     * Get [MeganekkoApp].
     *
     * @return Android context
     */
    val app: MeganekkoApp
        get() = entity!!.app!!

    /**
     * Called when this is attached to [Entity].
     *
     * @param entity Entity
     */
    open fun onAttach(entity: Entity) {}

    /**
     * Called when this is detached from [Entity].
     *
     * @param entity Detached Entity
     */
    open fun onDetach(entity: Entity) {}

    /**
     * Called on every frame update. About 60 times per second.
     *
     * @param frame Frame information.
     */
    open fun update(frame: FrameInput) {}

    /**
     * Remove this from [Entity].
     *
     * @return `true` if Successfully removed. Otherwise `false`.
     * @since 3.0.17
     */
    @Deprecated("Use {@link Entity#remove(Component)} or {@link #removeSelf()}.")
    fun remove(): Boolean {
        return entity != null && entity!!.remove(this)
    }

    /**
     * Remove this from [Entity].
     * Use this if you want to remove myself from [.update].
     *
     * @since 3.0.23
     */
    protected fun removeSelf() {
        if (entity != null) {
            entity!!.app!!.runOnGlThread(Runnable { entity!!.remove(this@Component) })
        }
    }

    inline fun <reified T : Component> getComponent(): T? {
        return getComponent(T::class.java)
    }

    /**
     * Returns the component of type in the [Entity].
     *
     * @param type Component class
     * @return Found [Component].
     */
    fun <T : Component> getComponent(type: Class<T>): T? {
        return entity!!.getComponent(type)
    }

    inline fun <reified T : Component> getComponentInChildren(): T? {
        return getComponentInChildren(T::class.java)
    }

    /**
     * Returns the component of type in the [Entity] or any of its children using depth first search.
     *
     * @param type Component class
     * @return Found [Component]. If no one is found, null.
     */
    fun <T : Component> getComponentInChildren(type: Class<T>): T? {
        return entity!!.getComponentInChildren(type)
    }

    inline fun <reified T : Component> getComponentInParent(): T? {
        return getComponentInParent(T::class.java)
    }

    /**
     * Returns the component of type in the [Entity] or any of its parents.
     * Recurses upwards until it finds a valid component. Returns null if no component found.
     *
     * @param type Component class
     * @return Found [Component]. If no one is found, null.
     */
    fun <T : Component> getComponentInParent(type: Class<T>): T? {
        return entity!!.getComponentInParent(type)
    }
}
