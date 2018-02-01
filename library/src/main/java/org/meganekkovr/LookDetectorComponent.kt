package org.meganekkovr

/**
 * This notifies whether user is looking at [Entity] or not.
 */
class LookDetectorComponent(private val lookListener: LookListener) : Component() {

    private val lookDetector = LookDetector.instance
    private var looking: Boolean = false

    override fun update(frame: FrameInput) {

        val isLookingNow = lookDetector.isLookingAt(entity)

        if (isLookingNow) {

            // Start looking
            if (!looking) {
                lookListener.onLookStart(entity, frame)
            }

            lookListener.onLooking(entity, frame)

        } else {

            // Stop looking
            if (looking) {
                lookListener.onLookEnd(entity, frame)
            }
        }

        this.looking = isLookingNow

        super.update(frame)
    }

    interface LookListener {

        /**
         * Called when user starts looking at target [Entity].
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        fun onLookStart(entity: Entity, frame: FrameInput)

        /**
         * Called when user stops looking at target [Entity].
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        fun onLookEnd(entity: Entity, frame: FrameInput)

        /**
         * Called when user is looking at target [Entity] in every frame update.
         *
         * @param entity Entity
         * @param frame  Frame information
         */
        fun onLooking(entity: Entity, frame: FrameInput)
    }
}
