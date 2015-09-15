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

/**
 * An actual eye pointee.
 * 
 * A {@link EyePointee} is something that is being pointed at by a picking
 * ray. {@linkplain EyePointee Eye pointees} are held by
 * {@linkplain EyePointeeHolder eye pointee holders,} which are attached to
 * {@link SceneObject scene objects.} The {@link Picker} will return a
 * {@code GVREyePointeeHolder[]}: you use
 * {@link EyePointeeHolder#getOwnerObject()} to retrieve the scene object.
 * 
 * <p>
 * A MeshEyePointee holds the {@link Mesh} that the picking ray will be
 * tested against.
 */
public class MeshEyePointee extends EyePointee {
    private Mesh mMesh;

    /**
     * Base constructor.
     * 
     * When the mesh is complicated, it will be cheaper - though less accurate -
     * to use {@link Mesh#getBoundingBox()} instead of the raw mesh.
     * 
     * @param gvrContext
     *            The {@link GLContext} used by the app.
     * 
     * @param mesh
     *            The {@link Mesh} that the picking ray will test against.
     */
    public MeshEyePointee(GLContext gvrContext, Mesh mesh) {
        super(gvrContext, NativeMeshEyePointee.ctor(mesh.getNative()));
        mMesh = mesh;
    }

    /**
     * Simple constructor.
     * 
     * When the mesh is complicated, it will be cheaper - though less accurate -
     * to use {@link Mesh#getBoundingBox()} instead of the raw mesh.
     * 
     * @param mesh
     *            The {@link Mesh} that the picking ray will test against.
     */
    public MeshEyePointee(Mesh mesh) {
        this(mesh.getGVRContext(), mesh);
    }

    /**
     * Constructor that can use the mesh's bounding box.
     * 
     * When the mesh is complicated, it will be cheaper - though less accurate -
     * to use {@link Mesh#getBoundingBox()} instead of the raw mesh.
     * 
     * @param mesh
     *            The {@link Mesh} that the picking ray will test against.
     * @param useBoundingBox
     *            When {@code true}, will use {@link Mesh#getBoundingBox()
     *            mesh.getBoundingBox()}; when {@code false} will use
     *            {@code mesh} directly.
     */
    /*
     * TODO How much accuracy do we lose with bounding boxes?
     * 
     * Would it make sense for the useBoundingBox parameter to be a tri-state
     * enum: mesh, box, box-then-mesh?
     */
    public MeshEyePointee(Mesh mesh, boolean useBoundingBox) {
        this(mesh.getGVRContext(), useBoundingBox ? mesh.getBoundingBox()
                : mesh);
    }

    /**
     * Retrieve the mesh that is held by this GVRMeshEyePointee
     * 
     * @return the {@link Mesh}
     * 
     */
    public Mesh getMesh() {
        return mMesh;
    }

    /**
     * Set the mesh to be tested against.
     * 
     * @param mesh
     *            The {@link Mesh} that the picking ray will test against.
     * 
     */
    public void setMesh(Mesh mesh) {
        mMesh = mesh;
        NativeMeshEyePointee.setMesh(getNative(), mesh.getNative());
    }
}

class NativeMeshEyePointee {
    static native long ctor(long mesh);

    static native void setMesh(long meshEyePointee, long mesh);
}
