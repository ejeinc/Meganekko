package org.meganekkovr;

import android.support.annotation.NonNull;

/**
 * CameraComponent updates its {@link Entity}'s rotation to head tracking rotation in every frame.
 * Any children of its entity are fixed on viewport.
 */
public class CameraComponent extends Component {

    @Override
    public void update(@NonNull FrameInput frame) {

        // Update entity rotation to match to head tracking.
        Entity entity = getEntity();
        entity.setRotation(HeadTransform.getInstance().getQuaternion());
        entity.setPosition(HeadTransform.getInstance().getPosition());

        super.update(frame);
    }
}
