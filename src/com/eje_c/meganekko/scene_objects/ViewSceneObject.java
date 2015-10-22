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

            mView.measure(0, 0);
            mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());

            // if mesh is missing, assign quad mesh
            if (getRenderData().getMesh() == null) {
                getRenderData()
                        .setMesh(getVrContext().createQuad(mView.getMeasuredWidth() * 0.002f,
                                mView.getMeasuredHeight() * 0.002f));
            }

            setCanvasSize(mView.getMeasuredWidth(), mView.getMeasuredHeight());

            setOnDrawListener(ViewSceneObject.this);
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
    public void onDraw(CanvasSceneObject object, Canvas canvas, VrFrame vrFrame) {

        if (mView != null) {

            // Simulate pressing
            if (mSimulatePressing && getEyePointeeHolder() != null) {
                float distance = Picker.pickSceneObject(this, getVrContext().getActivity().getScene().getMainCamera());
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
            mView.draw(canvas);
        }
    }

    private final Runnable updateViewState = new Runnable() {
        @Override
        public void run() {

            if (mView == null)
                return;

            mView.setPressed(mLooking);
        }
    };
}
