package org.meganekkovr;

import android.support.annotation.NonNull;

import org.joml.Quaternionf;

/**
 * CameraComponent updates its {@link Entity}'s rotation to head tracking rotation in every frame.
 * Any children of its entity are fixed on viewport.
 */
public class CameraComponent extends Component {

    @Override
    public void update(@NonNull FrameInput frame) {

        // Update entity rotation to match to head tracking.
        Entity entity = getEntity();
        Quaternionf q = entity.getApp().getCenterViewRotation();
        entity.setRotation(q);

        super.update(frame);
    }
}
