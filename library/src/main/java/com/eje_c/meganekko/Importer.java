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

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

/**
 * {@link Importer} provides methods for importing 3D models and making them
 * available through instances of {@link AssimpImporter}.
 * <p/>
 * Supports importing models from an application's resources (both
 * {@code assets} and {@code res/raw}) and from directories on the device's SD
 * card that the application has permission to read.
 */
class Importer {
    private Importer() {
    }

    /**
     * Imports a 3D model from the specified file in the application's
     * {@code asset} directory.
     *
     * @param context  Context to import file from.
     * @param filename Name of the file to import.
     * @return An instance of {@link AssimpImporter} or {@code null} if the
     * file does not exist (or cannot be read)
     */
    static AssimpImporter readFileFromAssets(Context context,
                                             String filename, EnumSet<ImportSettings> settings) {
        long nativeValue = readFileFromAssets(context.getAssets(), filename, ImportSettings.getAssimpImportFlags(settings));
        return nativeValue == 0 ? null : new AssimpImporter(nativeValue);
    }

    static AssimpImporter readFileFromResources(Context context, int resourceId, EnumSet<ImportSettings> settings) {
        return readFileFromResources(new AndroidResource(context, resourceId), settings);
    }

    static AssimpImporter readFileFromResources(AndroidResource resource, EnumSet<ImportSettings> settings) {
        try {
            byte[] bytes;
            InputStream stream = resource.getStream();
            try {
                bytes = new byte[stream.available()];
                stream.read(bytes);
            } finally {
                resource.closeStream();
            }
            String resourceFilename = resource.getResourceFilename();
            if (resourceFilename == null) {
                resourceFilename = ""; // Passing null causes JNI exception.
            }
            long nativeValue = readFromByteArray(bytes, resourceFilename, ImportSettings.getAssimpImportFlags(settings));
            return new AssimpImporter(nativeValue);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static native long readFileFromAssets(AssetManager assetManager, String filename, int settings);

    private static native long readFileFromSDCard(String filename, int settings);

    private static native long readFromByteArray(byte[] bytes, String filename, int settings);
}
