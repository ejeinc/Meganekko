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

/***************************************************************************
 * The mesh for rendering.
 ***************************************************************************/
#include "includes.h"

#ifndef MESH_H_
#define MESH_H_

#include "HybridObject.h"
#include "Material.h"
#include "util/GL.h"

namespace mgn {

struct BoundingBoxInfo {
    Vector3f mins;
    Vector3f maxs;
};

struct BoundingSphereInfo {
    Vector3f center;
    float radius;
};

class Mesh: public HybridObject {
public:
    Mesh() {
    }

    ~Mesh() {
        geometry.Free();
    }

    const GlGeometry & GetGeometry() const {
        return geometry;
    }

    const GlGeometry & GetGeometry() {
        return geometry;
    }

    void SetGeometry(const GlGeometry & geometry) {
        this->geometry.Free();
        this->geometry = geometry;
    }

    void SetBoundingBox(const Vector3f & mins, const Vector3f & maxs);

    const BoundingBoxInfo & getBoundingBoxInfo(); // Xmin, Ymin, Zmin and Xmax, Ymax, Zmax
    void getTransformedBoundingBoxInfo(OVR::Matrix4f *M,
            float *transformed_bounding_box); //Get Bounding box info transformed by matrix
    const BoundingSphereInfo & getBoundingSphereInfo(); // Get bounding sphere based on the bounding box

    // generate VAO
    void GenerateVAO();

private:
    Mesh(const Mesh& mesh);
    Mesh(Mesh&& mesh);
    Mesh& operator=(const Mesh& mesh);
    Mesh& operator=(Mesh&& mesh);

private:

    // bounding box info
    BoundingBoxInfo boundingBoxInfo;
    BoundingSphereInfo boundingSphereInfo;

    GlGeometry geometry;
};
}
#endif
