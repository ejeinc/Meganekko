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
 * Contains data from the rotation sensor.
 ***************************************************************************/

#ifndef ROTATION_SENSOR_DATA_H_
#define ROTATION_SENSOR_DATA_H_

#include "Kernel/OVR_Math.h"

namespace mgn {

class RotationSensorData {
public:
    RotationSensorData() :
            time_stamp_(0), quaterion_(), gyro_() {
    }
    RotationSensorData(double time_stamp, float w, float x, float y, float z,
            float gyro_x, float gyro_y, float gyro_z) :
            time_stamp_(time_stamp), quaterion_(x, y, z, w), gyro_(gyro_x,
                    gyro_y, gyro_z) {
    }
    ~RotationSensorData() {
    }
    long long time_stamp() const {
        return time_stamp_;
    }
    const OVR::Quatf& quaternion() const {
        return quaterion_;
    }
    OVR::Quatf quaternion() {
        return quaterion_;
    }
    const OVR::Vector3f& gyro() const {
        return gyro_;
    }
    OVR::Vector3f gyro() {
        return gyro_;
    }
    RotationSensorData& operator=(RotationSensorData&& rotation_sensor_data) {
        time_stamp_ = rotation_sensor_data.time_stamp_;
        quaterion_ = std::move(rotation_sensor_data.quaterion_);
        gyro_ = std::move(rotation_sensor_data.gyro_);

        return *this;
    }

private:
    RotationSensorData(const RotationSensorData& rotation_sensor_data);
    RotationSensorData(RotationSensorData&& rotation_sensor_data);
    RotationSensorData& operator=(
            const RotationSensorData& rotation_sensor_data);

private:
    double time_stamp_;
    OVR::Quatf quaterion_;
    OVR::Vector3f gyro_;
};

}

#endif
