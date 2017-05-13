package org.meganekkovr.sample_component;

import android.support.annotation.NonNull;

import org.joml.Quaternionf;
import org.meganekkovr.Component;
import org.meganekkovr.FrameInput;

/**
 * This rotates Android robot. Call togglePause() to pause rotation. Call it again to resume.
 */
public class RotatorComponent extends Component {
    private static final float ROTATION_PER_SECOND = (float) Math.toRadians(180);
    private final Quaternionf rotation = new Quaternionf();
    private boolean paused;

    @Override
    public void update(@NonNull FrameInput frame) {

        if (!paused) {
            rotation.rotate(0, 0, ROTATION_PER_SECOND * frame.getDeltaSeconds());
            getEntity().setRotation(rotation);
        }

        super.update(frame);
    }

    public void togglePause() {
        paused = !paused;
    }
}
