/* 
 * Copyright 2015 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.scene_objects;

import com.eje_c.meganekko.Picker;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrContext;
import com.eje_c.meganekko.VrFrame;
import com.eje_c.meganekko.scene_objects.CanvasSceneObject.OnDrawListener;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import ovr.JoyButton;

/**
 * A {@linkplain SceneObject scene object} that renders Android's standard
 * {@code View}. Currently {@code ViewGroup}s are not supported. You can only
 * use {@code Button}, {@code TextView},{@code ImageView} or other {@code View}s
 * which renders its contents.
 */
public class ViewSceneObject extends CanvasSceneObject implements OnDrawListener {

    private View mView;
    private boolean mLooking;
    private boolean mSimulatePressing = true;

    public ViewSceneObject(VrContext vrContext) {
        super(vrContext);
    }

    /**
     * Set view.
     * 
     * @param view
     */
    public void setView(View view) {
        this.mView = view;

        if (mView != null) {
            getVrContext().getActivity().runOnUiThread(addContentViewAndStartRendering);
        } else {
            clearOnDrawListener();
        }
    }

    /**
     * Set view from XML resource.
     * 
     * @param xmlRes
     */
    public void setView(int xmlRes) {
        LayoutInflater layoutInflater = LayoutInflater.from(getVrContext().getContext());
        View view = layoutInflater.inflate(xmlRes, null);
        setView(view);
    }

    /**
     * Get view.
     * 
     * @return
     */
    public View getView() {
        return mView;
    }

    @Override
    public void onEvent(VrFrame vrFrame) {

        if (mView != null) {

            // Update texture size
            setCanvasSize(mView.getWidth(), mView.getHeight());

            // Simulate pressing
            if (mSimulatePressing && getEyePointeeHolder() != null) {
                float distance = Picker.pickSceneObject(this, getVrContext().getMainScene().getMainCamera());
                if (distance < Float.POSITIVE_INFINITY) {
                    if (!mLooking) {
                        mLooking = true;
                        getVrContext().getActivity().runOnUiThread(updateViewState);
                    }
                } else {
                    if (mLooking) {
                        mLooking = false;
                        getVrContext().getActivity().runOnUiThread(updateViewState);
                    }
                }
            }

            // Simulate click
            if (mLooking) {
                if (JoyButton.contains(vrFrame.getButtonPressed(), JoyButton.BUTTON_TOUCH_SINGLE)) {
                    mView.callOnClick();
                } else if (JoyButton.contains(vrFrame.getButtonPressed(), JoyButton.BUTTON_TOUCH_LONGPRESS)) {
                    mView.performLongClick();
                }
            }
        }

        super.onEvent(vrFrame);
    }

    @Override
    public void onDraw(CanvasSceneObject object, Canvas canvas, VrFrame vrFrame) {
        if (mView != null) {
            mView.draw(canvas);
        }
    }

    private final Runnable addContentViewAndStartRendering = new Runnable() {
        @Override
        public void run() {

            if (mView == null)
                return;

            getVrContext().getActivity().addContentView(mView,
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mView.setVisibility(View.INVISIBLE);

            setOnDrawListener(ViewSceneObject.this);
        }
    };

    private final Runnable updateViewState = new Runnable() {
        @Override
        public void run() {

            if (mView == null)
                return;

            mView.setPressed(mLooking);
        }
    };
}
