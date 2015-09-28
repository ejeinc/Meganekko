/* Copyright 2015 Samsung Electronics Co., LTD
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

package com.eje_c.meganekko;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.eje_c.meganekko.utility.Threads;

import android.util.LongSparseArray;

/**
 * Holds any number of {@linkplain EyePointee 'eye pointees,'} which are
 * things the eye is pointing at.
 * 
 * Ray casting is computationally expensive. Rather than probing the entire
 * scene graph, Meganekko requires you to mark parts of the scene as "pickable" by
 * adding their meshes (or, more cheaply if less precisely, their
 * {@linkplain Mesh#getBoundingBox() bounding box}) to a
 * {@link EyePointeeHolder};
 * {@linkplain SceneObject#attachEyePointeeHolder(EyePointeeHolder)
 * attaching} that holder to a {@linkplain SceneObject scene object}; and
 * setting the holder's {@linkplain #setEnable(boolean) enabled flag.}
 * 
 * <p>
 * When you call one of the {@linkplain Picker#pickScene(GVRScene)
 * pickScene() overloads}, you get an array of {@linkplain EyePointeeHolder
 * eye pointee holders}. You can then call {@link #getOwnerObject()} to get the
 * scene object that a holder is attached to.
 */
public class EyePointeeHolder extends Component {

    // private static final String TAG = Log.tag(GVREyePointeeHolder.class);

    private static final LongSparseArray<WeakReference<EyePointeeHolder>> sEyePointeeHolders = new LongSparseArray<WeakReference<EyePointeeHolder>>();

    private final List<EyePointee> pointees = new ArrayList<EyePointee>();

    static EyePointeeHolder lookup(VrContext vrContext, long nativePointer) {
        WeakReference<EyePointeeHolder> weakReference = sEyePointeeHolders
                .get(nativePointer);
        return weakReference == null ? null : weakReference.get();
    }

    /**
     * Constructor
     * 
     * @param vrContext
     *            Current {@link VrContext}
     */
    public EyePointeeHolder(VrContext vrContext) {
        this(vrContext, NativeEyePointeeHolder.ctor());
    }

    private EyePointeeHolder(VrContext vrContext, long nativePointer) {
        super(vrContext, nativePointer, sCleanup);
        registerNativePointer(nativePointer);
    }

    /**
     * Special constructor, for descendants that need to 'unregister' instances.
     * 
     * @param vrContext
     *            The current Meganekko context
     * @param nativePointer
     *            The native pointer, returned by the native constructor
     * @param cleanupHandlers
     *            Cleanup handler(s).
     * 
     *            <p>
     *            {@link EyePointeeHolder} uses a
     *            {@link HybridObject.CleanupHandlerListManager} to manage
     *            the cleanup lists: if this parameter is a
     *            {@code private static} class constant, there will be only one
     *            {@code List} per class. Descendants that supply a {@code List}
     *            and <em>also</em> have descendants that supply a {@code List}
     *            should use a {@link CleanupHandlerListManager} of their own,
     *            in the same way that this class does.
     */
    protected EyePointeeHolder(VrContext vrContext, long nativePointer,
            List<NativeCleanupHandler> descendantsCleanupHandlerList) {
        super(vrContext, nativePointer, sConcatenations
                .getUniqueConcatenation(descendantsCleanupHandlerList));
        registerNativePointer(nativePointer);
    }

    private void registerNativePointer(long nativePointer) {
        sEyePointeeHolders.put(nativePointer,
                new WeakReference<EyePointeeHolder>(this));
    }

    private final static List<NativeCleanupHandler> sCleanup;
    private final static CleanupHandlerListManager sConcatenations;
    static {
        sCleanup = new ArrayList<NativeCleanupHandler>(1);
        sCleanup.add(new NativeCleanupHandler() {

            @Override
            public void nativeCleanup(long nativePointer) {
                sEyePointeeHolders.remove(nativePointer);
            }
        });

        sConcatenations = new CleanupHandlerListManager(sCleanup);
    }

    public SceneObject getOwnerObject() {
        return super.getOwnerObject();
    }

    /**
     * Is this holder enabled?
     * 
     * If this holder is disabled, then picking will <b>not</b> occur against
     * its {@link EyePointee}s.
     * 
     * @return true if enabled, false otherwise.
     */
    public boolean getEnable() {
        return NativeEyePointeeHolder.getEnable(getNative());
    }

    /**
     * Enable or disable this holder.
     * 
     * If this holder is disabled, then picking will <b>not</b> occur against
     * its {@link EyePointee}s.
     * 
     * @param enable
     *            whether this holder should be enabled.
     */
    public void setEnable(boolean enable) {
        NativeEyePointeeHolder.setEnable(getNative(), enable);
    }

    /**
     * Get the x, y, z of the point of where the hit occurred in model space
     * 
     * @return Three floats representing the x, y, z hit point.
     * 
     */
    public float[] getHit() {
        return NativeEyePointeeHolder.getHit(getNative());
    }

    /**
     * Add a {@link EyePointee} to this holder
     * 
     * @param eyePointee
     *            The {@link EyePointee} to add
     * 
     */
    public void addPointee(EyePointee eyePointee) {
        pointees.add(eyePointee);
        NativeEyePointeeHolder.addPointee(getNative(), eyePointee.getNative());
    }

    /**
     * Add a Future {@link EyePointee} to this holder
     * 
     * @param eyePointee
     *            A Future {@link EyePointee}, probably from
     *            {@link RenderData#getMeshEyePointee()}
     */
    public void addPointee(final Future<EyePointee> eyePointee) {
        // The Future<GVREyePointee> may well actually be a FutureWrapper, not a
        // 'real' Future
        if (eyePointee.isDone()) {
            addFutureEyePointee(eyePointee);
        } else {
            Threads.spawn(new Runnable() {

                @Override
                public void run() {
                    addFutureEyePointee(eyePointee);
                }
            });
        }
    }

    private void addFutureEyePointee(Future<EyePointee> eyePointee) {
        try {
            addPointee(eyePointee.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a {@link EyePointee} from this holder.
     * 
     * No exception is thrown if the eye pointee is not held by this holder.
     * 
     * @param eyePointee
     *            The {@link EyePointee} to remove
     * 
     */
    public void removePointee(EyePointee eyePointee) {
        pointees.remove(eyePointee);
        NativeEyePointeeHolder.removePointee(getNative(),
                eyePointee.getNative());
    }
}

class NativeEyePointeeHolder {
    static native long ctor();

    static native boolean getEnable(long eyePointeeHolder);

    static native void setEnable(long eyePointeeHolder, boolean enable);

    static native float[] getHit(long eyePointeeHolder);

    static native void addPointee(long eyePointeeHolder, long eyePointee);

    static native void removePointee(long eyePointeeHolder, long eyePointee);
}
