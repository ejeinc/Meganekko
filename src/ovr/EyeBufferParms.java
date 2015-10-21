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

public class EyeBufferParms {

	private final long mNativePtr;

	protected EyeBufferParms(long nativePtr) {
		this.mNativePtr = nativePtr;
	}

	long getNativePtr() {
		return mNativePtr;
	}

	public int getResolutionWidth() {
		return getResolutionWidth(mNativePtr);
	}

	public void setResolutionWidth(int resolutionWidth) {
		setResolutionWidth(mNativePtr, resolutionWidth);
	}

	public int getResolutionHeight() {
		return getResolutionHeight(mNativePtr);
	}

	public void setResolutionHeight(int resolutionHeight) {
		setResolutionWidth(mNativePtr, resolutionHeight);
	}

	public int getMultisamples() {
		return getMultisamples(mNativePtr);
	}

	public void setMultisamples(int multisamples) {
		setResolutionWidth(mNativePtr, multisamples);
	}

	public boolean getResolveDepth() {
		return getResolveDepth(mNativePtr);
	}

	public void setResolveDepth(boolean resolveDepth) {
		setResolveDepth(mNativePtr, resolveDepth);
	}

	private native int getResolutionWidth(long nativePtr);

	private native void setResolutionWidth(long nativePtr, int resolutionWidth);

	private native int getResolutionHeight(long nativePtr);

	private native void setResolutionHeight(long nativePtr, int resolutionHeight);

	private native int getMultisamples(long nativePtr);

	private native void setMultisamples(long nativePtr, int multisamples);

	private native boolean getResolveDepth(long nativePtr);

	private native void setResolveDepth(long nativePtr, boolean resolveDepth);
}
