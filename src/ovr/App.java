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

public class App {
	private final long mAppPtr;

	public App(long appPtr) {
		this.mAppPtr = appPtr;
	}

	public void startSystemActivity(String command) {
		startSystemActivity(mAppPtr, command);
	}

	public void returnToHome() {
		returnToHome(mAppPtr);
	}

	// -----------------------------------------------------------------
	// system settings
	// -----------------------------------------------------------------

	public int getSystemBrightness() {
		return getSystemBrightness(mAppPtr);
	}

	public void setSystemBrightness(int brightness) {
		setSystemBrightness(mAppPtr, brightness);
	}

	public boolean getComfortMode() {
		return getComfortMode(mAppPtr);
	}

	public void setComfortMode(boolean enable) {
		setComfortMode(mAppPtr, enable);
	}

	public boolean getDoNotDisturbMode() {
		return getDoNotDisturbMode(mAppPtr);
	}

	public void setDoNotDisturbMode(boolean enable) {
		setDoNotDisturbMode(mAppPtr, enable);
	}

	// -----------------------------------------------------------------
	// accessors
	// -----------------------------------------------------------------

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

	private native void returnToHome(long appPtr);

	private native int getSystemBrightness(long appPtr);

	private native void setSystemBrightness(long appPtr, int brightness);

	private native boolean getComfortMode(long appPtr);

	private native void setComfortMode(long appPtr, boolean enable);

	private native boolean getDoNotDisturbMode(long appPtr);

	private native void setDoNotDisturbMode(long appPtr, boolean enable);

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
