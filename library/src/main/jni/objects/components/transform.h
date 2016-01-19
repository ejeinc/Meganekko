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

#ifndef TRANSFORM_H_
#define TRANSFORM_H_

#include <memory>

#include "objects/Lazy.h"
#include "objects/components/Component.h"

#include "Kernel/OVR_Math.h"

using namespace OVR;

namespace mgn {
class Transform: public Component {
public:
    Transform();
    virtual ~Transform();

    const Vector3f& getPosition() const {
        return position;
    }

    void setPosition(const Vector3f &position) {
        this->position = position;
        invalidate(false);
    }

    void setPositionX(float x) {
        position.x = x;
        invalidate(false);
    }

    void setPositionY(float y) {
        position.y = y;
        invalidate(false);
    }

    void setPositionZ(float z) {
        position.z = z;
        invalidate(false);
    }

    const Quatf& getRotation() const {
        return rotation;
    }

    float getRotationYaw() const {
        return yaw(rotation);
    }

    float getRotationPitch() const {
        return pitch(rotation);
    }

    float getRotationRoll() const {
        return roll(rotation);
    }

    void setRotation(const Quatf &rotation) {
        this->rotation = rotation;
        invalidate(true);
    }

    const Vector3f& getScale() const {
        return scale;
    }

    void setScale(const Vector3f& scale) {
        this->scale = scale;
        invalidate(false);
    }

    void setScaleX(float x) {
        scale.x = x;
        invalidate(false);
    }

    void setScaleY(float y) {
        scale.y = y;
        invalidate(false);
    }

    void setScaleZ(float z) {
        scale.z = z;
        invalidate(false);
    }

    void invalidate(bool rotationUpdated);
    const Matrix4f & getModelMatrix();
    void translate(float x, float y, float z);
    void setRotationByAxis(float angle, float x, float y, float z);
    void rotate(float w, float x, float y, float z);
    void rotateByAxis(float angle, float x, float y, float z);
    void rotateByAxisWithPivot(float angle, float axis_x, float axis_y,
            float axis_z, float pivot_x, float pivot_y, float pivot_z);
    void rotateWithPivot(float w, float x, float y, float z, float pivot_x,
            float pivot_y, float pivot_z);
    void setModelMatrix(const Matrix4f & mat);

    static inline const Quatf angleAxis(float angle, float axis_x, float axis_y, float axis_z) {
        return Quatf(Vector3f(axis_x, axis_y, axis_z), DegreeToRad(angle));
    }

private:
    Transform(const Transform& transform);
    Transform(Transform&& transform);
    Transform& operator=(const Transform& transform);
    Transform& operator=(Transform&& transform);

    inline float sign(float value) {
        return value < 0 ? -1.0f : 1.0f;
    }
private:
    Vector3f position;
    Quatf rotation;
    Vector3f scale;

    Lazy<Matrix4f> modelMatrix;

    // from glm::yaw
    inline static float yaw(Quatf q) {
        return RadToDegree(asinf(-2.0f * (q.x * q.z - q.w * q.y)));
    }

    // from glm::pitch
    inline static float pitch(Quatf q) {
        return RadToDegree(atan2(2.0f * (q.y * q.z + q.w * q.x), q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z));
    }

    // from glm::roll
    inline static float roll(Quatf q) {
        return RadToDegree(atan2(2.0f * (q.x * q.y + q.w * q.z), q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z));
    }
};

}
#endif
