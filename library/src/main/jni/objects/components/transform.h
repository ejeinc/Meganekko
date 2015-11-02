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

#include "objects/lazy.h"
#include "objects/components/component.h"

#include "Kernel/OVR_Math.h"

namespace mgn {
class Transform: public Component {
public:
    Transform();
    virtual ~Transform();

    const OVR::Vector3f& position() const {
        return position_;
    }

    void set_position(const OVR::Vector3f& position) {
        position_ = position;
        invalidate(false);
    }

    void set_position_x(float x) {
        position_.x = x;
        invalidate(false);
    }

    void set_position_y(float y) {
        position_.y = y;
        invalidate(false);
    }

    void set_position_z(float z) {
        position_.z = z;
        invalidate(false);
    }

    const OVR::Quatf& rotation() const {
        return rotation_;
    }

    float rotation_yaw() const {
        return yaw(rotation_);
    }

    float rotation_pitch() const {
        return pitch(rotation_);
    }

    float rotation_roll() const {
        return roll(rotation_);
    }

    void set_rotation(const OVR::Quatf& roation) {
        rotation_ = roation;
        invalidate(true);
    }

    const OVR::Vector3f& scale() const {
        return scale_;
    }

    void set_scale(const OVR::Vector3f& scale) {
        scale_ = scale;
        invalidate(false);
    }

    void set_scale_x(float x) {
        scale_.x = x;
        invalidate(false);
    }

    void set_scale_y(float y) {
        scale_.y = y;
        invalidate(false);
    }

    void set_scale_z(float z) {
        scale_.z = z;
        invalidate(false);
    }

    void invalidate(bool rotationUpdated);
    const OVR::Matrix4f & getModelMatrix();
    void translate(float x, float y, float z);
    void setRotationByAxis(float angle, float x, float y, float z);
    void rotate(float w, float x, float y, float z);
    void rotateByAxis(float angle, float x, float y, float z);
    void rotateByAxisWithPivot(float angle, float axis_x, float axis_y,
            float axis_z, float pivot_x, float pivot_y, float pivot_z);
    void rotateWithPivot(float w, float x, float y, float z, float pivot_x,
            float pivot_y, float pivot_z);
    void setModelMatrix(const OVR::Matrix4f & mat);

    static inline const OVR::Quatf angleAxis(float angle, float axis_x, float axis_y, float axis_z) {
        return OVR::Quatf(OVR::Vector3f(axis_x, axis_y, axis_z), OVR::DegreeToRad(angle));
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
    OVR::Vector3f position_;
    OVR::Quatf rotation_;
    OVR::Vector3f scale_;

    Lazy<OVR::Matrix4f> model_matrix_;

    // from glm::yaw
    inline static float yaw(OVR::Quatf q) {
        return OVR::RadToDegree(asinf(-2.0f * (q.x * q.z - q.w * q.y)));
    }

    // from glm::pitch
    inline static float pitch(OVR::Quatf q) {
        return OVR::RadToDegree(atan2(2.0f * (q.y * q.z + q.w * q.x), q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z));
    }

    // from glm::roll
    inline static float roll(OVR::Quatf q) {
        return OVR::RadToDegree(atan2(2.0f * (q.x * q.y + q.w * q.z), q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z));
    }
};

}
#endif
