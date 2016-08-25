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

namespace mgn {

class Mesh: public HybridObject {
public:
    Mesh();

    ~Mesh();

    const GlGeometry & GetGeometry() const;

    const GlGeometry & GetGeometry();

    void SetGeometry(const GlGeometry & geometry);

    void GetTransformedBoundingBoxInfo(OVR::Matrix4f *M,
            float *transformed_bounding_box); //Get Bounding box info transformed by matrix

private:
    Mesh(const Mesh& mesh);
    Mesh(Mesh&& mesh);
    Mesh& operator=(const Mesh& mesh);
    Mesh& operator=(Mesh&& mesh);

private:
    GlGeometry geometry;
};
}
#endif
