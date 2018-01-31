package org.meganekkovr

/**
 * CameraComponent updates its [Entity]'s rotation to head tracking rotation in every frame.
 * Any children of its entity are fixed on viewport.
 */
class CameraComponent : Component() {

    override fun update(frame: FrameInput) {

        // Update entity rotation to match to head tracking.
        entity?.rotation = HeadTransform.instance.quaternion
        entity?.position = HeadTransform.instance.position

        super.update(frame)
    }
}
