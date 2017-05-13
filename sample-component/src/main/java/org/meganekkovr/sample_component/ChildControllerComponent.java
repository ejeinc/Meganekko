package org.meganekkovr.sample_component;

import android.support.annotation.NonNull;

import org.meganekkovr.Component;
import org.meganekkovr.FrameInput;
import org.meganekkovr.JoyButton;

public class ChildControllerComponent extends Component {

    @Override
    public void update(@NonNull FrameInput frame) {

        // Detect double tap event
        if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_DOUBLE)) {

            // Get component from child
            RotatorComponent c1 = getComponentInChildren(RotatorComponent.class);
            c1.togglePause();
        }

        super.update(frame);
    }
}
