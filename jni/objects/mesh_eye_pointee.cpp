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
 * Eye pointee made by a mesh.
 ***************************************************************************/

#include "mesh_eye_pointee.h"

#include <limits>

#include "objects/mesh.h"
#include "util/gvr_log.h"

namespace gvr {
MeshEyePointee::MeshEyePointee(Mesh* mesh) :
        EyePointee(), mesh_(mesh) {
}

MeshEyePointee::~MeshEyePointee() {
}

inline OVR::Vector3f toVec3(const OVR::Vector4f v){
    return OVR::Vector3f(v.x, v.y, v.z);
}

EyePointData MeshEyePointee::isPointed(const OVR::Matrix4f& mv_matrix, float ox,
        float oy, float oz, float dx, float dy, float dz) {
    OVR::Matrix4f inv_mv_matrix = mv_matrix.InvertedHomogeneousTransform();
    std::vector<OVR::Vector4f> relative_veritces;
    for (auto it = mesh_->vertices().begin(); it != mesh_->vertices().end(); ++it) {
        OVR::Vector4f mesh_vertex(*it);
        relative_veritces.push_back(mv_matrix.Transform(mesh_vertex));
    }

    EyePointData data;

    //http://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
    for (int i = 0; i < mesh_->triangles().size(); i += 3) {
        OVR::Vector3f O(ox, oy, oz);
        OVR::Vector3f D(dx, dy, dz);

        OVR::Vector3f V1 = toVec3(relative_veritces[mesh_->triangles()[i]]);
        OVR::Vector3f V2 = toVec3(relative_veritces[mesh_->triangles()[i + 1]]);
        OVR::Vector3f V3 = toVec3(relative_veritces[mesh_->triangles()[i + 2]]);

        OVR::Vector3f e1(V2 - V1);
        OVR::Vector3f e2(V3 - V1);

        OVR::Vector3f P = D.Cross(e2);

        float det = e1.Dot(P);

        const float EPSILON = 0.00001f;

        if (det > -EPSILON && det < EPSILON) {
            continue;
        }

        float inv_det = 1.0f / det;

        OVR::Vector3f T(O - V1);

        float u = T.Dot(P) * inv_det;

        if (u < 0.0f || u > 1.0f) {
            continue;
        }

        OVR::Vector3f Q = T.Cross(e1);

        float v = D.Dot(Q) * inv_det;

        if (v < 0.0f || (u + v) > 1.0f) {
            continue;
        }

        float t = e2.Dot(Q) * inv_det;

        if (t > EPSILON) {
            float distance = t;
            if (distance < data.distance()) {
                data.setDistance(distance);
                data.setHit(
                        inv_mv_matrix.Transform(
                                V1 * (1.0f - u - v) + V2 * u
                                        + V3 * v));
            }
        }
    }

    return data;
}

EyePointData MeshEyePointee::isPointed(const OVR::Matrix4f& mv_matrix) {
    return isPointed(mv_matrix, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f);
}

}
