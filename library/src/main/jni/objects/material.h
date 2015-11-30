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
 * Links textures and shaders.
 ***************************************************************************/

#ifndef MATERIAL_H_
#define MATERIAL_H_

#include <map>
#include <memory>
#include <string>

#include "objects/hybrid_object.h"
#include "objects/textures/texture.h"

#include "Kernel/OVR_Math.h"

namespace mgn {
class Color;

class Material: public HybridObject {
public:
    enum ShaderType {
        UNLIT_HORIZONTAL_STEREO_SHADER = 0,
        UNLIT_VERTICAL_STEREO_SHADER = 1,
        OES_SHADER = 2,
        OES_HORIZONTAL_STEREO_SHADER = 3,
        OES_VERTICAL_STEREO_SHADER = 4,
        CUBEMAP_SHADER = 5,
        CUBEMAP_REFLECTION_SHADER = 6,
        TEXTURE_SHADER = 7,
        ASSIMP_SHADER = 9,
        TEXTURE_SHADER_NOLIGHT = 100
    };

    explicit Material(ShaderType shader_type) :
            shader_type_(shader_type), textures_(), floats_(), vec2s_(), vec3s_(), vec4s_(), shader_feature_set_(
                    0) {
        switch (shader_type) {
        default:
            vec3s_["color"] = OVR::Vector3f(1.0f, 1.0f, 1.0f);
            floats_["opacity"] = 1.0f;
            break;
        }
    }

    ~Material() {
    }

    ShaderType shader_type() const {
        return shader_type_;
    }

    void set_shader_type(ShaderType shader_type) {
        shader_type_ = shader_type;
    }

    Texture* getTexture(std::string key) const {
        auto it = textures_.find(key);
        if (it != textures_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getTexture() : " + key
                    + " not found";
            throw error;
        }
    }

    void setTexture(std::string key, Texture* texture) {
        textures_[key] = texture;
    }

    float getFloat(std::string key) {
        auto it = floats_.find(key);
        if (it != floats_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getFloat() : " + key + " not found";
            throw error;
        }
    }
    void setFloat(std::string key, float value) {
        floats_[key] = value;
    }

    OVR::Vector2f getVec2(std::string key) {
        auto it = vec2s_.find(key);
        if (it != vec2s_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getVec2() : " + key + " not found";
            throw error;
        }
    }

    void setVec2(std::string key, OVR::Vector2f vector) {
        vec2s_[key] = vector;
    }

    OVR::Vector3f getVec3(std::string key) {
        auto it = vec3s_.find(key);
        if (it != vec3s_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getVec3() : " + key + " not found";
            throw error;
        }
    }

    void setVec3(std::string key, OVR::Vector3f vector) {
        vec3s_[key] = vector;
    }

    OVR::Vector4f getVec4(std::string key) {
        auto it = vec4s_.find(key);
        if (it != vec4s_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getVec4() : " + key + " not found";
            throw error;
        }
    }

    void setVec4(std::string key, OVR::Vector4f vector) {
        vec4s_[key] = vector;
    }

    OVR::Matrix4f getMat4(std::string key) {
        auto it = mat4s_.find(key);
        if (it != mat4s_.end()) {
            return it->second;
        } else {
            std::string error = "Material::getMat4() : " + key + " not found";
            throw error;
        }
    }

    void setMat4(std::string key, OVR::Matrix4f matrix) {
        mat4s_[key] = matrix;
    }

    int get_shader_feature_set() {
        return shader_feature_set_;
    }

    void set_shader_feature_set(int feature_set) {
        shader_feature_set_ = feature_set;
    }

private:
    Material(const Material& material);
    Material(Material&& material);
    Material& operator=(const Material& material);
    Material& operator=(Material&& material);

private:
    ShaderType shader_type_;
    std::map<std::string, Texture*> textures_;
    std::map<std::string, float> floats_;
    std::map<std::string, OVR::Vector2f> vec2s_;
    std::map<std::string, OVR::Vector3f> vec3s_;
    std::map<std::string, OVR::Vector4f> vec4s_;
    std::map<std::string, OVR::Matrix4f> mat4s_;
    unsigned int shader_feature_set_;
};
}
#endif
