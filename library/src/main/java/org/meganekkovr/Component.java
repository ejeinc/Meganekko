package org.meganekkovr;

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
    public void onAttach(Entity entity) {
    }

    /**
     * Called when this is detached from {@link Entity}.
     *
     * @param entity Detached Entity
     */
    public void onDetach(Entity entity) {
    }

    /**
     * Called on every frame update. About 60 times per second.
     *
     * @param frame Frame information.
     */
    public void update(FrameInput frame) {
    }

    /**
     * For internal use only.
     *
     * @param entity Attached Entity
     */
    void setEntity(Entity entity) {
        this.entity = entity;
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
     * @return {@code true} if attached.
     */
    public boolean isAttached() {
        return entity != null;
    }
}
