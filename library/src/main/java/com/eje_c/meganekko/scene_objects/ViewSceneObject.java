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

import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.VrFrame;
import com.eje_c.meganekko.scene_objects.CanvasSceneObject.OnDrawListener;

import ovr.JoyButton;

/**
 * A {@linkplain SceneObject scene object} that renders Android's standard {@code View}.
 */
public class ViewSceneObject extends CanvasSceneObject implements OnDrawListener {

    public static final float AUTO_SIZE_SCALE = 0.006f;

    private View mView;
    private boolean mLooking;
    private final Runnable updateViewState = new Runnable() {
        @Override
        public void run() {

            if (mView == null)
                return;

            mView.setPressed(mLooking);
            mView.invalidate();
        }
    };
    private boolean mSimulatePressing = true;
    private boolean mGenerateAutoSizedMesh = true;

    /**
     * Check dirty state of view recursively.
     *
     * @param view Checked View.
     * @return Returns true if at least one View is dirty in hierarchy.
     */
    private static boolean isDirty(View view) {

        if (view.isDirty()) return true;

        // Apply this method to all children of view if view is ViewGroup
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
                if (isDirty(viewGroup.getChildAt(i))) return true;
            }
        }

        return false;
    }

    /**
     * Set view.
     *
     * @param view View which is used as {@link Texture}.
     */
    public void setView(View view) {
        this.mView = view;

        if (mView != null) {

            updateLayout();
            setOnDrawListener(ViewSceneObject.this);
        } else {
            clearOnDrawListener();
        }
    }

    /**
     * Get view.
     *
     * @return View
     */
    public View getView() {
        return mView;
    }

    /**
     * Set view from XML resource.
     *
     * @param layoutRes Layout XML resource ID
     */
    public void setView(int layoutRes) {
        LayoutInflater layoutInflater = LayoutInflater.from(getVrContext().getContext());
        View view = layoutInflater.inflate(layoutRes, null);
        setView(view);
    }

    /**
     * Update view size and recreate quad mesh.
     * If you want to assign custom mesh, call {@linkplain #setGenerateAutoSizedMesh(boolean) setGenerateAutoSizedMesh(false)} first.
     */
    public void updateLayout() {
        View view = getView();
        view.measure(0, 0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        setCanvasSize(view.getMeasuredWidth(), view.getMeasuredHeight());

        // Assign quad mesh if needed
        if (mGenerateAutoSizedMesh || getRenderData().getMesh() == null) {
            setAutoSizedQuadMesh();
        }
    }

    /**
     * Set mesh to auto sized quad.
     */
    public void setAutoSizedQuadMesh() {
        float width = mView.getMeasuredWidth() * AUTO_SIZE_SCALE;
        float height = mView.getMeasuredHeight() * AUTO_SIZE_SCALE;
        Mesh quadMesh = getVrContext().createQuad(width, height);
        getRenderData().setMesh(quadMesh);
    }

    @Override
    public void onEvent(VrFrame vrFrame) {

        if (mView != null) {

            // Simulate pressing
            if (mSimulatePressing) {
                if (getVrContext().getActivity().isLookingAt(this)) {
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
            canvas.drawColor(0, Mode.CLEAR);
            mView.draw(canvas);
        }
    }

    @Override
    public boolean isDirty() {
        return mView != null && isDirty(mView);
    }

    /**
     * @return whether simulate pressing is enabled
     */
    public boolean isSimulatePressingEnabled() {
        return mSimulatePressing;
    }

    /**
     * Set whether this change automatically its View's pressed state. Default is {@code true}.
     *
     * @param enabled whether this change automatically its View's pressed state
     */
    public void setSimulatePressingEnabled(boolean enabled) {
        this.mSimulatePressing = enabled;
    }

    /**
     * @return whether mesh will be generated automatically
     */
    public boolean getGenerateAutoSizedMesh() {
        return mGenerateAutoSizedMesh;
    }

    /**
     * Set whether mesh is automatically generated from view. Default is {@code true}.
     *
     * @param generateAutoSizedMesh whether mesh is automatically generated from view
     */
    public void setGenerateAutoSizedMesh(boolean generateAutoSizedMesh) {
        this.mGenerateAutoSizedMesh = generateAutoSizedMesh;
    }
}
