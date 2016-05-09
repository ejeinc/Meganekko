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

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.TypedValue;

import com.eje_c.meganekko.utility.MarkingFileInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class to minimize overload fan-out.
 * <p/>
 * APIs that load resources can take a {@link AndroidResource} instead of
 * having overloads for {@code assets} files, {@code res/drawable} and
 * {@code res/raw} files, and plain old files.
 * <p/>
 * See the discussion of asset-relative filenames <i>vs.</i> {@code R.raw}
 * resource ids in the <a href="package-summary.html#assets">package
 * description</a>.
 */
public class AndroidResource {

    private final InputStream mStream;

    /*
     * Instance members
     */
    // Save parameters, for hashCode() and equals()
    private final String mFilePath;
    private final int mResourceId;
    private final String mAssetPath;
    private DebugStates mDebugState;
    // For hint to Assimp
    private String mResourceFilePath;

    /**
     * Open any file you have permission to read.
     *
     * @param path A Linux file path
     * @throws FileNotFoundException File doesn't exist, or can't be read.
     */
    public AndroidResource(String path) throws FileNotFoundException {
        mStream = new MarkingFileInputStream(path);
        mDebugState = DebugStates.OPEN;

        mFilePath = path;
        mResourceId = 0; // No R.whatever field will ever be 0
        mAssetPath = null;
        mResourceFilePath = null;
    }

    /**
     * Open any file you have permission to read.
     *
     * @param file A Java {@link File} object
     * @throws FileNotFoundException File doesn't exist, or can't be read.
     */
    public AndroidResource(File file) throws FileNotFoundException {
        this(file.getAbsolutePath());
    }

    /**
     * Open a {@code res/raw} or {@code res/drawable} bitmap file.
     *
     * @param context    An Android Context
     * @param resourceId A {@code R.raw} or {@code R.drawable} id
     */
    public AndroidResource(Context context, int resourceId) {
        Resources resources = context.getResources();
        mStream = resources.openRawResource(resourceId);
        mDebugState = DebugStates.OPEN;

        mFilePath = null;
        this.mResourceId = resourceId;
        mAssetPath = null;
        TypedValue value = new TypedValue();
        resources.getValue(resourceId, value, true);
        mResourceFilePath = value.string.toString();
    }

    /**
     * Open an {@code assets} file
     *
     * @param context               An Android Context
     * @param assetRelativeFilename A filename, relative to the {@code assets} directory. The file
     *                              can be in a sub-directory of the {@code assets} directory:
     *                              {@code "foo/bar.png"} will open the file {@code assets/foo/bar.png}
     * @throws IOException File does not exist or cannot be read
     */
    public AndroidResource(Context context, String assetRelativeFilename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        mStream = assets.open(assetRelativeFilename);
        mDebugState = DebugStates.OPEN;

        mFilePath = null;
        mResourceId = 0; // No R.whatever field will ever be 0
        mAssetPath = assetRelativeFilename;
        mResourceFilePath = null;
    }

    /**
     * Get the open stream.
     * <p/>
     * Changes the debug state (visible <i>via</i> {@link #toString()}) to
     * {@linkplain AndroidResource.DebugStates#READING READING}.
     *
     * @return An open {@link InputStream}.
     */
    public final InputStream getStream() {
        mDebugState = DebugStates.READING;
        return mStream;
    }

    /**
     * Close the open stream.
     * <p/>
     * It's OK to call code that closes the stream for you - the only point of
     * this API is to update the debug state (visible <i>via</i>
     * {@link #toString()}) to
     * {@linkplain AndroidResource.DebugStates#CLOSED CLOSED}.
     */
    public final void closeStream() {
        try {
            mDebugState = DebugStates.CLOSED;
            mStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * TODO Should we somehow expose the CLOSED state? Return null or throw an
     * exception from getStream()? Or is it enough for the calling code to fail,
     * reading a closed stream?
     */

    /**
     * Save the stream position, for later use with {@link #reset()}.
     * <p/>
     * All {@link AndroidResource} streams support
     * {@link InputStream#mark(int) mark()} and {@link InputStream#reset()
     * reset().} Calling {@link #mark()} right after construction will allow you
     * to read the header then {@linkplain #reset() rewind the stream} if you
     * can't handle the file format.
     */
    public void mark() {
        mStream.mark(Integer.MAX_VALUE);
    }

    /**
     * Restore the stream position, to the point set by a previous
     * {@link #mark() mark().}
     * <p/>
     * Please note that calling {@link #reset()} generally 'consumes' the
     * {@link #mark()} - <em>do not</em> call
     * <p/>
     * <pre>
     * mark();
     * reset();
     * reset();
     * </pre>
     */
    public void reset() {
        try {
            mStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the filename of the resource with extension.
     *
     * @return Filename of the AndroidResource. May return null if the
     * resource is not associated with any file
     */
    public String getResourceFilename() {
        if (mFilePath != null) {
            return mFilePath.substring(mFilePath.lastIndexOf(File.separator) + 1);
        } else if (mResourceId != 0) {
            if (mResourceFilePath != null) {
                return mResourceFilePath.substring(mResourceFilePath
                        .lastIndexOf(File.separator) + 1);
            }
        } else if (mAssetPath != null) {
            return mAssetPath
                    .substring(mAssetPath.lastIndexOf(File.separator) + 1);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mAssetPath == null) ? 0 : mAssetPath.hashCode());
        result = prime * result + ((mFilePath == null) ? 0 : mFilePath.hashCode());
        result = prime * result + mResourceId;
        return result;
    }

    /*
     * Auto-generated hashCode() and equals(), for container support &c.
     * 
     * These check only the private 'parameter capture' fields - not the
     * InputStream.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AndroidResource other = (AndroidResource) obj;
        if (mAssetPath == null) {
            if (other.mAssetPath != null) {
                return false;
            }
        } else if (!mAssetPath.equals(other.mAssetPath)) {
            return false;
        }
        if (mFilePath == null) {
            if (other.mFilePath != null) {
                return false;
            }
        } else if (!mFilePath.equals(other.mFilePath)) {
            return false;
        }
        return mResourceId == other.mResourceId;
    }

    /**
     * For debugging: shows which file the instance describes, and shows the
     * OPEN / READING / CLOSED state of the input stream.
     */
    @Override
    public String toString() {
        return String.format("%s{filePath=%s; resourceId=%x; assetPath=%s}", mDebugState, mFilePath, mResourceId, mAssetPath);
    }

    /*
     * toString(), for debugging.
     */

    private enum DebugStates {
        OPEN, READING, CLOSED
    }

    /*
     * Generic callback interfaces, for asynchronous APIs that take a {@link
     * AndroidResource} parameter
     */

    /**
     * Callback interface for asynchronous resource loading.
     * <p/>
     * None of the asynchronous resource [textures, and meshes] loading methods
     * that take a {@link AndroidResource} parameter return a value. You must
     * supply a copy of this interface to get results.
     * <p/>
     * While you will often create a callback for each load request, the APIs do
     * each include the {@link AndroidResource} that you are loading. This
     * lets you use the same callback implementation with multiple resources.
     */
    public interface Callback<T extends HybridObject> {
        /**
         * Resource load succeeded.
         *
         * @param resource        A new Meganekko resource
         * @param androidResource The description of the resource that was loaded successfully
         */
        void loaded(T resource, AndroidResource androidResource);

        /**
         * Resource load failed.
         *
         * @param t               Error information
         * @param androidResource The description of the resource that could not be loaded
         */
        void failed(Throwable t, AndroidResource androidResource);
    }

    /**
     * Callback interface for cancelable resource loading.
     * <p/>
     * Loading uncompressed textures (Android {@linkplain Bitmap bitmaps}) can
     * take hundreds of milliseconds and megabytes of memory; loading even
     * moderately complex meshes can be even slower. Meganekko uses a throttling
     * system to manage system load, and an priority system to give you some
     * control over the throttling. This means that slow resource loads can take
     * enough time that you don't actually need the resource by the time the
     * system gets to it. The {@link #stillWanted(AndroidResource)} method
     * lets you cancel unneeded loads.
     */
    public interface CancelableCallback<T extends HybridObject> extends Callback<T> {
        /**
         * Do you still want this resource?
         * <p/>
         * If the throttler has a thread available, your request will be run
         * right away; this method will not be called. If not, it is enqueued;
         * this method will be called (at least once) before starting to load
         * the resource. If you no longer need it, returning {@code false} can
         * save non-trivial amounts of time and memory.
         *
         * @param androidResource The description of the resource that is about to be loaded
         * @return {@code true} to continue loading; {@code false} to abort.
         * (Returning {@code false} will not call
         * {@link #failed(Throwable, AndroidResource) failed().})
         */
        boolean stillWanted(AndroidResource androidResource);
    }

    /*
     * Specialized callback interfaces, to make use a bit smaller and clearer.
     */

    /**
     * Callback for asynchronous mesh loads
     */
    public interface MeshCallback extends CancelableCallback<Mesh> {
    }
}
