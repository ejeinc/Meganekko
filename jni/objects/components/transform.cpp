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
 * Containing data about how to position an object.
 ***************************************************************************/

#include "transform.h"

#include "objects/scene_object.h"

namespace gvr {
Transform::Transform() :
        Component(), position_(OVR::Vector3f(0.0f, 0.0f, 0.0f)), rotation_(
                OVR::Quatf()), scale_(
                OVR::Vector3f(1.0f, 1.0f, 1.0f)), model_matrix_(
                Lazy<OVR::Matrix4f>(OVR::Matrix4f())) {
}

Transform::~Transform() {
}

void Transform::invalidate(bool rotationUpdated) {
    if (model_matrix_.isValid()) {
        model_matrix_.invalidate();
        std::vector<SceneObject*> children(owner_object()->children());
        for (auto it = children.begin(); it != children.end(); ++it) {
            (*it)->transform()->invalidate(false);
        }
    }
    if (rotationUpdated) {
        // scale rotation_ if needed to avoid overflow
        static const float threshold = sqrt(FLT_MAX) / 2.0f;
        static const float scale_factor = 0.5f / sqrt(FLT_MAX);
        if (rotation_.w > threshold || rotation_.x > threshold
                || rotation_.y > threshold || rotation_.z > threshold) {
            rotation_.w *= scale_factor;
            rotation_.x *= scale_factor;
            rotation_.y *= scale_factor;
            rotation_.z *= scale_factor;
        }
    }
}

const OVR::Matrix4f & Transform::getModelMatrix() {
    if (!model_matrix_.isValid()) {
        OVR::Matrix4f translation_matrix = OVR::Matrix4f::Translation(position_);
        OVR::Matrix4f rotation_matrix = OVR::Matrix4f(rotation_);
        OVR::Matrix4f scale_matrix = OVR::Matrix4f::Scaling(scale_);
        OVR::Matrix4f trs_matrix = translation_matrix * rotation_matrix * scale_matrix;

        if (owner_object()->parent() != 0) {
            OVR::Matrix4f model_matrix =
                    owner_object()->parent()->transform()->getModelMatrix()
                            * trs_matrix;
            model_matrix_.validate(model_matrix);
        } else {
            model_matrix_.validate(trs_matrix);
        }
    }

    return model_matrix_.element();
}

void Transform::setModelMatrix(const OVR::Matrix4f & matrix) {

    float xs = sign(matrix.M[0][0] * matrix.M[1][0] * matrix.M[2][0] * matrix.M[3][0]);
    float ys = sign(matrix.M[0][1] * matrix.M[1][1] * matrix.M[2][1] * matrix.M[3][1]);
    float zs = sign(matrix.M[0][2] * matrix.M[1][2] * matrix.M[2][2] * matrix.M[3][2]);

    OVR::Vector3f new_scale(
            xs * OVR::Vector3f(matrix.M[0][0], matrix.M[1][0], matrix.M[2][0]).Length(),
            ys * OVR::Vector3f(matrix.M[0][1], matrix.M[1][1], matrix.M[2][1]).Length(),
            zs * OVR::Vector3f(matrix.M[0][2], matrix.M[1][2], matrix.M[2][2]).Length());

    OVR::Matrix3f rotation_mat(
            matrix.M[0][0] / new_scale.x, matrix.M[0][1] / new_scale.y, matrix.M[0][2] / new_scale.z,
            matrix.M[1][0] / new_scale.x, matrix.M[1][1] / new_scale.y, matrix.M[1][2] / new_scale.z,
            matrix.M[2][0] / new_scale.x, matrix.M[2][1] / new_scale.y, matrix.M[2][2] / new_scale.z);

    position_ = matrix.GetTranslation();
    scale_ = new_scale;
    rotation_ = OVR::Quatf(rotation_mat);
    invalidate(true);
}

void Transform::translate(float x, float y, float z) {
    position_ += OVR::Vector3f(x, y, z);
    invalidate(false);
}

void Transform::setRotationByAxis(float angle, float x, float y, float z) {
    rotation_ = angleAxis(angle, x, y, z);
    invalidate(true);
}

void Transform::rotate(float w, float x, float y, float z) {
    rotation_ = OVR::Quatf(x, y, z, w) * rotation_;
    invalidate(true);
}

void Transform::rotateByAxis(float angle, float x, float y, float z) {
    rotation_ = angleAxis(angle, x, y, z) * rotation_;
    invalidate(true);
}

void Transform::rotateByAxisWithPivot(float angle, float axis_x, float axis_y,
        float axis_z, float pivot_x, float pivot_y, float pivot_z) {
    OVR::Quatf axis_rotation = angleAxis(angle, axis_x, axis_y, axis_z);
    rotation_ = axis_rotation * rotation_;
    OVR::Vector3f pivot(pivot_x, pivot_y, pivot_z);
    OVR::Vector3f relative_position = position_ - pivot;
    relative_position = axis_rotation.Rotate(relative_position);
    position_ = relative_position + pivot;
    invalidate(true);
}

void Transform::rotateWithPivot(float w, float x, float y, float z,
        float pivot_x, float pivot_y, float pivot_z) {
    OVR::Quatf rot(x, y, z, w);
    rotation_ = rot * rotation_;
    OVR::Vector3f pivot(pivot_x, pivot_y, pivot_z);
    OVR::Vector3f relative_position = position_ - pivot;
    relative_position = rot.Rotate(relative_position);
    position_ = relative_position + pivot;
    invalidate(true);
}

}
