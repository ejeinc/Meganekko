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

#include "Scene.h"

#include "SceneObject.h"
#include "RenderData.h"
#include "Kernel/OVR_Geometry.h"

namespace mgn {
Scene::Scene() :
        SceneObject(), main_camera_(), frustum_flag_(
                false), dirtyFlag_(0), occlusion_flag_(false) {
    oesShader = new OESShader();
}

Scene::~Scene() {
    delete  oesShader;
}

std::vector<SceneObject*> Scene::getWholeSceneObjects() {
    std::vector<SceneObject*> scene_objects(children());
    for (int i = 0; i < scene_objects.size(); ++i) {
        std::vector<SceneObject*> children(scene_objects[i]->children());
        for (auto it = children.begin(); it != children.end(); ++it) {
            scene_objects.push_back(*it);
        }
    }

    return scene_objects;
}

void Scene::PrepareForRendering() {
    sceneObjects = getWholeSceneObjects();
}

Matrix4f Scene::Render(const int eye) {
    const Matrix4f viewProjectionM = projectionM * viewM;
    Renderer::RenderEyeView(this, sceneObjects, oesShader, viewM, projectionM, viewProjectionM, eye);
    return viewProjectionM;
}

bool Scene::IsLookingAt(const SceneObject *target) {

    Matrix4f worldToModelM = target->transform()->getModelMatrix().InvertedHomogeneousTransform();
    Matrix4f invertedCenterViewM = centerViewM.InvertedHomogeneousTransform();
    Vector3f inWorldCenterViewPos = invertedCenterViewM.GetTranslation();
    Quatf centerViewRot = Quatf(invertedCenterViewM);

    const Vector3f rayStart = worldToModelM.Transform(inWorldCenterViewPos);
    const Vector3f rayDir = centerViewRot.Rotate(Vector3f(0.0f, 0.0f, -1.0f));
    const float* boundingBoxInfo = target->render_data()->mesh()->getBoundingBoxInfo();
    const Vector3f mins(boundingBoxInfo[0], boundingBoxInfo[1], boundingBoxInfo[2]);
    const Vector3f maxs(boundingBoxInfo[3], boundingBoxInfo[4], boundingBoxInfo[5]);
    float t0 = 0.0f;
    float t1 = 0.0f;

    return Intersect_RayBounds(rayStart, rayDir, mins, maxs, t0, t1);
}

}
