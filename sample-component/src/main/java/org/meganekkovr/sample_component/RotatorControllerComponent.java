package org.meganekkovr.sample_component;

import android.support.annotation.NonNull;

import org.meganekkovr.Component;
import org.meganekkovr.FrameInput;
import org.meganekkovr.JoyButton;

public class RotatorControllerComponent extends Component {

    @Override
    public void update(@NonNull FrameInput frame) {

        // Detect single tap event
        if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {

            // Get component
            RotatorComponent c1 = getComponent(RotatorComponent.class);
            c1.togglePause();
        }

        super.update(frame);
    }
}
