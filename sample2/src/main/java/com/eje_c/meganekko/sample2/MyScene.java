package com.eje_c.meganekko.sample2;

import com.eje_c.meganekko.Frame;
import com.eje_c.meganekko.Scene;
import com.eje_c.meganekko.SceneObject;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ovr.JoyButton;

public class MyScene extends Scene {
    private final Random random = new Random();
    private List<SceneObject> objects = new ArrayList<>();

    @Override
    public void update(Frame frame) {

        // If single tap detected, spawn object
        if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
            SceneObject object = SceneObject.fromDrawable(getApp().getContext(), R.mipmap.ic_launcher);
            object.position(new Vector3f(random.nextFloat() * 5.0f - 2.5f, random.nextFloat() * 5.0f - 2.5f, random.nextFloat() * 5.0f - 2.5f));
            objects.add(object);
            addChildObject(object);
        }
        // If long tap detected, remove all spawned objects
        else if (JoyButton.contains(frame.getButtonPressed(), JoyButton.BUTTON_TOUCH_LONGPRESS)) {

            for (SceneObject object : objects) {
                removeChildObject(object);
            }
            objects.clear();
        }

        // Move spawned objects
        for (SceneObject object : objects) {
            object.position(object.position().sub(0, 0, 0.1f));
        }

        super.update(frame);
    }
}
