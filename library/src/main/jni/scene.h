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
 * Holds scene objects. Can be used by engines.
 ***************************************************************************/

#ifndef SCENE_H_
#define SCENE_H_

#include <memory>
#include <vector>


#include "HybridObject.h"
#include "Camera.h"
#include "Renderer.h"
#include "Kernel/OVR_Math.h"

using namespace OVR;

namespace mgn {
class SceneObject;

class Scene: public SceneObject {
public:
    Scene(JNIEnv * jni, jobject javaObject);
    virtual ~Scene();
    const Camera* main_camera() {
        return main_camera_;
    }
    void set_main_camera(Camera* camera) {
        main_camera_ = camera;
    }
    std::vector<SceneObject*> getWholeSceneObjects();

    void set_frustum_culling( bool frustum_flag){ frustum_flag_ = frustum_flag; }
    bool get_frustum_culling(){ return frustum_flag_; }

    void set_occlusion_culling( bool occlusion_flag){ occlusion_flag_ = occlusion_flag; }
    bool get_occlusion_culling(){ return occlusion_flag_; }

    void SetViewMatrix(const Matrix4f & m){
        viewM = m;
    }

    void SetProjectionMatrix(const Matrix4f & m){
        projectionM = m;
    }

    void PrepareForRendering();

    Matrix4f Render(const int eye);

    bool IsLookingAt(const SceneObject * target);

private:
    Scene(const Scene& scene);
    Scene(Scene&& scene);
    Scene& operator=(const Scene& scene);
    Scene& operator=(Scene&& scene);

private:
    Camera* main_camera_;
    OESShader* oesShader;

    Matrix4f viewM;
    Matrix4f projectionM;
    std::vector<SceneObject*> sceneObjects; // will be rendererd

    int dirtyFlag_;
    bool frustum_flag_;
    bool occlusion_flag_;
    bool statsInitialized = false;

};

}
#endif
