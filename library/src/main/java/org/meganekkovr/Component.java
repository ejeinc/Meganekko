package org.meganekkovr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Component is a basic class in Meganekko. It attaches some actions or behaviors to {@link Entity}.
 */
public abstract class Component {

    private Entity entity;

    /**
     * Called when this is attached to {@link Entity}.
     *
     * @param entity Entity
     */
    public void onAttach(@NonNull Entity entity) {
    }

    /**
     * Called when this is detached from {@link Entity}.
     *
     * @param entity Detached Entity
     */
    public void onDetach(@NonNull Entity entity) {
    }

    /**
     * Called on every frame update. About 60 times per second.
     *
     * @param frame Frame information.
     */
    public void update(@NonNull FrameInput frame) {
    }

    /**
     * Get attached {@link Entity}.
     *
     * @return Entity which is attached to this Component.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * For internal use only.
     *
     * @param entity Attached Entity
     */
    void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    /**
     * @return {@code true} if attached.
     */
    public boolean isAttached() {
        return entity != null;
    }

    /**
     * Remove this from {@link Entity}.
     *
     * @return {@code true} if Successfully removed. Otherwise {@code false}.
     * @since 3.0.17
     * @deprecated Use {@link Entity#remove(Component)} or {@link #removeSelf()}.
     */
    public boolean remove() {
        return entity != null && entity.remove(this);
    }

    /**
     * Remove this from {@link Entity}.
     * Use this if you want to remove myself from {@link #update(FrameInput)}.
     *
     * @since 3.0.23
     */
    protected void removeSelf() {
        if (entity != null) {
            entity.getApp().runOnGlThread(new Runnable() {
                @Override
                public void run() {
                    entity.remove(Component.this);
                }
            });
        }
    }

    /**
     * Get Android context.
     *
     * @return Android context
     */
    public Context getContext() {
        return entity.getApp().getContext();
    }

    /**
     * Get {@link MeganekkoApp}.
     *
     * @return Android context
     */
    public MeganekkoApp getApp() {
        return entity.getApp();
    }

    /**
     * Returns the component of type in the {@link Entity}.
     *
     * @param type Component class
     * @return Found {@link Component}.
     */
    public <T extends Component> T getComponent(Class<T> type) {
        return entity.getComponent(type);
    }

    /**
     * Returns the component of type in the {@link Entity} or any of its children using depth first search.
     *
     * @param type Component class
     * @return Found {@link Component}. If no one is found, null.
     */
    public <T extends Component> T getComponentInChildren(Class<T> type) {
        return entity.getComponentInChildren(type);
    }

    /**
     * Returns the component of type in the {@link Entity} or any of its parents.
     * Recurses upwards until it finds a valid component. Returns null if no component found.
     *
     * @param type Component class
     * @return Found {@link Component}. If no one is found, null.
     */
    public <T extends Component> T getComponentInParent(@NonNull Class<T> type) {
        return entity.getComponentInParent(type);
    }
}
