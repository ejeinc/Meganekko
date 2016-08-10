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

#include "includes.h"
#include "Mesh.h"

namespace mgn {

void Mesh::GetTransformedBoundingBoxInfo(OVR::Matrix4f *Mat,
        float *transformed_bounding_box) {

    OVR::Matrix4f M(*Mat);
    float a, b;

    //Inspired by Graphics Gems - TransBox.c
    //Transform the AABB to the correct position in world space
    //Generate a new AABB from the non axis aligned bounding box

    transformed_bounding_box[0] = M.M[0][3];
    transformed_bounding_box[3] = M.M[0][3];

    transformed_bounding_box[1] = M.M[1][3];
    transformed_bounding_box[4] = M.M[1][3];

    transformed_bounding_box[2] = M.M[2][3];
    transformed_bounding_box[5] = M.M[2][3];

    for (int i = 0; i < 3; i++) {
        //x coord
        a = M.M[0][i] * geometry.localBounds.GetMins().x;
        b = M.M[0][i] * geometry.localBounds.GetMaxs().x;
        if (a < b) {
            transformed_bounding_box[0] += a;
            transformed_bounding_box[3] += b;
        } else {
            transformed_bounding_box[0] += b;
            transformed_bounding_box[3] += a;
        }

        //y coord
        a = M.M[1][i] * geometry.localBounds.GetMins().y;
        b = M.M[1][i] * geometry.localBounds.GetMaxs().y;
        if (a < b) {
            transformed_bounding_box[1] += a;
            transformed_bounding_box[4] += b;
        } else {
            transformed_bounding_box[1] += b;
            transformed_bounding_box[4] += a;
        }

        //z coord
        a = M.M[2][i] * geometry.localBounds.GetMins().z;
        b = M.M[2][i] * geometry.localBounds.GetMaxs().z;
        if (a < b) {
            transformed_bounding_box[2] += a;
            transformed_bounding_box[5] += b;
        } else {
            transformed_bounding_box[2] += b;
            transformed_bounding_box[5] += a;
        }
    }

    *Mat = M;
}

}
