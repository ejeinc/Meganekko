package org.meganekkovr;

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
     */
    public boolean remove() {
        return entity != null && entity.remove(this);
    }
}
