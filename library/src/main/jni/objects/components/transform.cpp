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

namespace mgn {
Transform::Transform() :
        Component(), position(Vector3f(0.0f, 0.0f, 0.0f)), rotation(
                Quatf()), scale(
                Vector3f(1.0f, 1.0f, 1.0f)), modelMatrix(
                Lazy<Matrix4f>(Matrix4f())) {
}

Transform::~Transform() {
}

void Transform::invalidate(bool rotationUpdated) {
    if (modelMatrix.isValid()) {
        modelMatrix.invalidate();
        std::vector<SceneObject*> children(owner_object()->children());
        for (auto it = children.begin(); it != children.end(); ++it) {
            (*it)->transform()->invalidate(false);
        }
    }
    if (rotationUpdated) {
        // scale rotation if needed to avoid overflow
        static const float threshold = sqrt(FLT_MAX) / 2.0f;
        static const float scale_factor = 0.5f / sqrt(FLT_MAX);
        if (rotation.w > threshold || rotation.x > threshold
                || rotation.y > threshold || rotation.z > threshold) {
            rotation.w *= scale_factor;
            rotation.x *= scale_factor;
            rotation.y *= scale_factor;
            rotation.z *= scale_factor;
        }
    }
}

const Matrix4f & Transform::getModelMatrix() {
    if (!modelMatrix.isValid()) {
        Matrix4f translationMatrix = Matrix4f::Translation(position);
        Matrix4f rotationMatrix = Matrix4f(rotation);
        Matrix4f scaleMatrix = Matrix4f::Scaling(scale);
        Matrix4f localMatrix = translationMatrix * rotationMatrix * scaleMatrix;

        if (owner_object()->parent() != 0) {
            Matrix4f matrix = owner_object()->parent()->transform()->getModelMatrix() * localMatrix;
            this->modelMatrix.validate(matrix);
        } else {
            this->modelMatrix.validate(localMatrix);
        }
    }

    return modelMatrix.element();
}

void Transform::setModelMatrix(const Matrix4f & matrix) {

    float xs = sign(matrix.M[0][0] * matrix.M[1][0] * matrix.M[2][0] * matrix.M[3][0]);
    float ys = sign(matrix.M[0][1] * matrix.M[1][1] * matrix.M[2][1] * matrix.M[3][1]);
    float zs = sign(matrix.M[0][2] * matrix.M[1][2] * matrix.M[2][2] * matrix.M[3][2]);

    Vector3f newScale(
            xs * Vector3f(matrix.M[0][0], matrix.M[1][0], matrix.M[2][0]).Length(),
            ys * Vector3f(matrix.M[0][1], matrix.M[1][1], matrix.M[2][1]).Length(),
            zs * Vector3f(matrix.M[0][2], matrix.M[1][2], matrix.M[2][2]).Length());

    Matrix3f rotationMatrix(
            matrix.M[0][0] / newScale.x, matrix.M[0][1] / newScale.y, matrix.M[0][2] / newScale.z,
            matrix.M[1][0] / newScale.x, matrix.M[1][1] / newScale.y, matrix.M[1][2] / newScale.z,
            matrix.M[2][0] / newScale.x, matrix.M[2][1] / newScale.y, matrix.M[2][2] / newScale.z);

    position = matrix.GetTranslation();
    scale = newScale;
    rotation = Quatf(rotationMatrix);
    invalidate(true);
}

void Transform::translate(float x, float y, float z) {
    position += Vector3f(x, y, z);
    invalidate(false);
}

void Transform::setRotationByAxis(float angle, float x, float y, float z) {
    rotation = angleAxis(angle, x, y, z);
    invalidate(true);
}

void Transform::rotate(float w, float x, float y, float z) {
    rotation = Quatf(x, y, z, w) * rotation;
    invalidate(true);
}

void Transform::rotateByAxis(float angle, float x, float y, float z) {
    rotation = angleAxis(angle, x, y, z) * rotation;
    invalidate(true);
}

void Transform::rotateByAxisWithPivot(float angle, float axis_x, float axis_y,
        float axis_z, float pivot_x, float pivot_y, float pivot_z) {
    Quatf axis_rotation = angleAxis(angle, axis_x, axis_y, axis_z);
    rotation = axis_rotation * rotation;
    Vector3f pivot(pivot_x, pivot_y, pivot_z);
    Vector3f relative_position = position - pivot;
    relative_position = axis_rotation.Rotate(relative_position);
    position = relative_position + pivot;
    invalidate(true);
}

void Transform::rotateWithPivot(float w, float x, float y, float z,
        float pivot_x, float pivot_y, float pivot_z) {
    Quatf rot(x, y, z, w);
    rotation = rot * rotation;
    Vector3f pivot(pivot_x, pivot_y, pivot_z);
    Vector3f relative_position = position - pivot;
    relative_position = rot.Rotate(relative_position);
    position = relative_position + pivot;
    invalidate(true);
}

}
