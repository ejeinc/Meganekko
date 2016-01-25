package com.eje_c.meganekko;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Meganekko {

    void setSceneFromXML(int xmlRes);

    Scene getScene();

    void setScene(Scene scene);

    void runOnGlThread(Runnable runnable);

    void runOnUiThread(Runnable runnable);

    void recenter();

    void animate(@NonNull final Animator anim, @Nullable final Runnable endCallback);

    void cancel(@NonNull final Animator anim, @Nullable final Runnable endCallback);

    Context getContext();

    VrFrame getVrFrame();

    boolean isLookingAt(SceneObject object);
}
