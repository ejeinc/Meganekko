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

#include "includes.h"
#include "Scene.h"

#include "SceneObject.h"
#include "RenderData.h"

namespace mgn {
Scene::Scene() : SceneObject(), clearEnabled(true), clearColor(0, 0, 0, 1) {}

Array<SceneObject*> Scene::GetWholeSceneObjects() {
    Array<SceneObject*> sceneObjects(GetChildren());
    for (int i = 0; i < sceneObjects.GetSize(); ++i) {
        Array<SceneObject*> children(sceneObjects[i]->GetChildren());
        for (auto it = children.Begin(); it != children.End(); ++it) {
            sceneObjects.PushBack(*it);
        }
    }

    return sceneObjects;
}

void Scene::SetCenterViewMatrix(const Matrix4f & m){
    centerViewM = m;
}

const Matrix4f & Scene::GetCenterViewMatrix() {
    return centerViewM;
}

const Matrix4f & Scene::GetCenterViewMatrix() const {
    return centerViewM;
}

IntersectRayBoundsResult Scene::IntersectRayBounds(SceneObject *target, bool axisInWorld) {

    Matrix4f worldToModelM = target->GetMatrixWorld().Inverted();
    Matrix4f invertedCenterViewM = centerViewM.Inverted();
    Vector3f inWorldCenterViewPos = invertedCenterViewM.GetTranslation();
    Quatf centerViewRot = Quatf(invertedCenterViewM);

    const Vector3f rayStart = worldToModelM.Transform(inWorldCenterViewPos);
    const Vector3f rayDir = worldToModelM.Transform(centerViewRot.Rotate(Vector3f(0.0f, 0.0f, -1.0f))) - rayStart;
    const Vector3f boundingBoxMins = target->GetRenderData()->GetMesh()->GetGeometry().localBounds.GetMins();
    const Vector3f boundingBoxMaxs = target->GetRenderData()->GetMesh()->GetGeometry().localBounds.GetMaxs();
    float t0 = 0.0f;
    float t1 = 0.0f;

    bool intersected = Intersect_RayBounds(rayStart, rayDir, boundingBoxMins, boundingBoxMaxs, t0, t1);

    IntersectRayBoundsResult result;
    result.intersected = intersected && t0 > 0;

    if (intersected) {
        result.first = rayStart + t0 * rayDir;
        result.second = rayStart + t1 * rayDir;

        if (axisInWorld) {
            result.first = target->GetMatrixWorld().Transform(result.first);
            result.second = target->GetMatrixWorld().Transform(result.second);
        }
    }

    return result;
}

void Scene::SetViewPosition(const Vector3f & pos) {
    viewPosition = pos;
}

const Vector3f & Scene::GetViewPosition() {
    return viewPosition;
}

const Vector3f & Scene::GetViewPosition() const {
    return viewPosition;
}

void Scene::GetFrameMatrices(const ovrHeadModelParms & headModelParms, const float fovDegreesX, const float fovDegreesY, ovrFrameMatrices & frameMatrices ) const {

    ovrMatrix4f centerViewMatrix = centerViewM;
    frameMatrices.CenterView = centerViewMatrix;

    for (int eye = 0; eye < 2; eye++) {
        frameMatrices.EyeView[eye]       = vrapi_GetEyeViewMatrix(&headModelParms, &centerViewMatrix, eye);
        frameMatrices.EyeProjection[eye] = ovrMatrix4f_CreateProjectionFov( fovDegreesX, fovDegreesY, 0.0f, 0.0f, VRAPI_ZNEAR, 0.0f );
    }
}

void Scene::GenerateFrameSurfaceList(const ovrFrameMatrices & frameMatrices, Array< ovrDrawSurface > & surfaceList ) {

    Array<SceneObject*> objects = GetWholeSceneObjects();
    Array<RenderData*> renderDataList;

    // Iterate over all SceneObjects and collect RenderData for rendering
    for (int i = 0; i < objects.GetSizeI(); i++) {
        SceneObject * object = objects[i];
        RenderData * renderData = object->GetRenderData();

        // Skip rendering
        if (renderData == nullptr
            || renderData->GetMaterial() == nullptr
            || renderData->GetMesh() == nullptr
            || !renderData->IsVisible()) continue;

        // Prepare for rendering
        renderData->SetModelMatrix(object->GetMatrixWorld());
        renderData->UpdateSurfaceDef();
        renderDataList.PushBack(renderData);
    }

    // Sort with renderingOrder
    Alg::QuickSort(renderDataList, RenderData::compareRenderData);

    for (int i = 0; i < renderDataList.GetSizeI(); i++) {
        RenderData * renderData = renderDataList[i];
        surfaceList.PushBack(ovrDrawSurface(renderData->GetModelMatrix(), &renderData->GetSurfaceDef()));
    }
}

void Scene::SetClearEnabled(bool clearEnabled) {
    this->clearEnabled = clearEnabled;
}

bool Scene::IsClearEnabled() { return clearEnabled; }

void Scene::SetClearColor(const Vector4f &clearColor) {
    this->clearColor = clearColor;
}

const Vector4f &Scene::GetClearColor() { return clearColor; }

const Vector4f &Scene::GetClearColor() const { return clearColor; }
}
