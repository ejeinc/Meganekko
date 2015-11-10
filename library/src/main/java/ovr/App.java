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

package ovr;

/**
 * Native OVR::App wrapper. See VrAppFramework/Include/App.h for more details.
 */
public class App {
    private final long mAppPtr;

    public App(long appPtr) {
        this.mAppPtr = appPtr;
    }

    public void startSystemActivity(String command) {
        startSystemActivity(mAppPtr, command);
    }

    // -----------------------------------------------------------------
    // accessors
    // -----------------------------------------------------------------

    public EyeBufferParms getEyeBufferParms() {
        return new EyeBufferParms(getEyeBufferParms(mAppPtr));
    }

    public void setEyeBufferParms(EyeBufferParms eyeBufferParms) {
        setEyeBufferParms(mAppPtr, eyeBufferParms.getNativePtr());
    }

    public int getCpuLevel() {
        return getCpuLevel(mAppPtr);
    }

    public void setCpuLevel(int cpuLevel) {
        setCpuLevel(mAppPtr, cpuLevel);
    }

    public int getGpuLevel() {
        return getGpuLevel(mAppPtr);
    }

    public void setGpuLevel(int gpuLevel) {
        setGpuLevel(mAppPtr, gpuLevel);
    }

    public int getMinimumVsyncs() {
        return getMinimumVsyncs(mAppPtr);
    }

    public void setMinimumVsyncs(int mininumVsyncs) {
        setMinimumVsyncs(mAppPtr, mininumVsyncs);
    }

    // -----------------------------------------------------------------
    // debugging
    // -----------------------------------------------------------------

    public boolean getShowFPS() {
        return getShowFPS(mAppPtr);
    }

    public void setShowFPS(boolean show) {
        setShowFPS(mAppPtr, show);
    }

    public void showInfoText(float duration, String text) {
        showInfoText(mAppPtr, duration, text);
    }

    private native void startSystemActivity(long appPtr, String command);

    private native long getEyeBufferParms(long appPtr);

    private native long setEyeBufferParms(long appPtr, long nativeEyeBufferParms);

    private native int getCpuLevel(long appPtr);

    private native void setCpuLevel(long appPtr, int cpuLevel);

    private native int getGpuLevel(long appPtr);

    private native void setGpuLevel(long appPtr, int gpuLevel);

    private native int getMinimumVsyncs(long appPtr);

    private native void setMinimumVsyncs(long appPtr, int mininumVsyncs);

    private native boolean getShowFPS(long appPtr);

    private native void setShowFPS(long appPtr, boolean show);

    private native void showInfoText(long appPtr, float duration, String fmt);
}
