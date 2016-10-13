package org.meganekkovr;

/**
 * This notifies whether user is looking at {@link Entity} or not.
 */
public class LookDetectorComponent extends Component {

    public interface LookListener {

        /**
         * Called when user starts looking at target {@link Entity}.
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        void onLookStart(Entity entity, FrameInput frame);

        /**
         * Called when user stops looking at target {@link Entity}.
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        void onLookEnd(Entity entity, FrameInput frame);

        /**
         * Called when user is looking at target {@link Entity} in every frame update.
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        void onLooking(Entity entity, FrameInput frame);
    }

    private final LookDetector lookDetector = LookDetector.getInstance();
    private final LookListener lookListener;
    private boolean looking;

    public LookDetectorComponent(LookListener lookListener) {
        this.lookListener = lookListener;
    }

    @Override
    public void update(FrameInput frame) {

        Entity entity = getEntity();
        boolean isLookingNow = lookDetector.isLookingAt(entity);

        if (isLookingNow) {

            // Start looking
            if (!looking) {
                lookListener.onLookStart(entity, frame);
            }

            lookListener.onLooking(entity, frame);

        } else {

            // Stop looking
            if (looking) {
                lookListener.onLookEnd(entity, frame);
            }
        }

        this.looking = isLookingNow;
        super.update(frame);
    }
}
