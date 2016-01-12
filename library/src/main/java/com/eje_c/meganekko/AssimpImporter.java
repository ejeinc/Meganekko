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

import com.eje_c.meganekko.jassimp.AiMaterial;
import com.eje_c.meganekko.jassimp.AiScene;

/**
 * Provides access to the {@link Mesh meshes} contained in 3D models that
 * have been imported with {@link Importer}.
 */
class AssimpImporter extends HybridObject {

    AssimpImporter(VrContext vrContext, long ptr) {
        super(vrContext, ptr);
    }

    /**
     * @return The number of meshes contained in the imported 3D model.
     */
    int getNumberOfMeshes() {
        return getNumberOfMeshes(getNative());
    }

    /**
     * Retrieves a specific mesh from the imported 3D model.
     *
     * @param index Index of the mesh to get
     * @return The mesh, encapsulated as a {@link Mesh}.
     */
    Mesh getMesh(int index) {
        return new Mesh(getVrContext(), getMesh(
                getNative(), index));
    }

    /**
     * Retrieves the complete scene from the imported 3D model.
     *
     * @return The scene, encapsulated as a {@link AiScene}, which is a
     * component of the Jassimp integration.
     */
    AiScene getAssimpScene() {
        return getAssimpScene(getNative());
    }

    /**
     * Retrieves the particular mesh for the given node.
     *
     * @return The mesh, encapsulated as a {@link Mesh}.
     */
    Mesh getNodeMesh(String nodeName, int meshIndex) {
        return new Mesh(getVrContext(), getNodeMesh(
                getNative(), nodeName, meshIndex));
    }

    /**
     * Retrieves the material for the mesh of the given node..
     *
     * @return The material, encapsulated as a {@link AiMaterial}.
     */
    AiMaterial getMeshMaterial(String nodeName, int meshIndex) {
        return getMeshMaterial(getNative(), nodeName, meshIndex);
    }

    private static native int getNumberOfMeshes(long assimpImporter);

    private static native long getMesh(long assimpImporter, int index);

    private static native AiScene getAssimpScene(long assimpImporter);

    private static native long getNodeMesh(long assimpImporter, String nodeName, int meshIndex);

    private static native AiMaterial getMeshMaterial(long assimpImporter, String nodeName, int meshIndex);
}
