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
#include "Mesh.h"

namespace mgn {
    Scene::Scene() : SceneObject(),
        frustumFlag(false),
        occlusionFlag(false),
        sceneObjectsChanged(true) {
    oesShader = new OESShader();
    backgroundColor = Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
}

Scene::~Scene() {
    delete oesShader;
}

std::vector<SceneObject*> Scene::GetWholeSceneObjects() {
    std::vector<SceneObject*> sceneObjects(GetChildren());
    for (int i = 0; i < sceneObjects.size(); ++i) {
        std::vector<SceneObject*> children(sceneObjects[i]->GetChildren());
        for (auto it = children.begin(); it != children.end(); ++it) {
            sceneObjects.push_back(*it);
        }
    }

    return sceneObjects;
}

void Scene::PrepareForRendering() {

    // Cache all SceneObjects in this scene to sceneObjects.
    if (sceneObjectsChanged) {
        sceneObjects = GetWholeSceneObjects();
        std::sort(sceneObjects.begin(), sceneObjects.end(), SceneObject::CompareWithRenderingOrder);
        sceneObjectsChanged = false;
    }
}

Matrix4f Scene::Render(const int eye) {
    const Matrix4f viewProjectionM = projectionM * viewM;

    // Prepare GL state
    glEnable (GL_DEPTH_TEST);
    glDepthFunc (GL_LEQUAL);
    glEnable (GL_CULL_FACE);
    glFrontFace (GL_CCW);
    glCullFace (GL_BACK);
    glEnable (GL_BLEND);
    glBlendEquation (GL_FUNC_ADD);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    glDisable (GL_POLYGON_OFFSET_FILL);

    glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    // Render all children
    for (auto it = sceneObjects.begin(); it != sceneObjects.end(); ++it) {

        SceneObject * sceneObject = *it;
        RenderData * renderData = sceneObject->GetRenderData();

        // No attached RenderData or invisible
        if (renderData == nullptr || !renderData->IsVisible()) continue;

        Mesh * mesh = renderData->GetMesh();
        if (mesh == nullptr) continue; // has no mesh

        Material* material = renderData->GetMaterial();
        if (material == nullptr) continue; // has no material

        if (renderData->GetOffset()) {
            glEnable (GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(renderData->GetOffsetFactor(), renderData->GetOffsetUnits());
        }

        if (!renderData->GetDepthTest()) {
            glDisable (GL_DEPTH_TEST);
        }

        if (!renderData->GetAlphaBlend()) {
            glDisable (GL_BLEND);
        }

        SetFaceCulling(material->GetSide());

        Matrix4f modelM = sceneObject->GetMatrixWorld();
        Matrix4f mvM(viewM * modelM);
        Matrix4f mvpM = projectionM * mvM;

        try {
            oesShader->Render(mvpM, mesh->GetGeometry(), material, eye);
        } catch (std::string error) {
            __android_log_print(ANDROID_LOG_ERROR, "mgn", "Error detected in Renderer::renderRenderData; error : %s", error.c_str());
        }

        // Restoring to Default.
        // TODO: There's a lot of redundant state changes. If on every render face culling is being set there's no need to
        // restore defaults. Possibly later we could add a OpenGL state wrapper to avoid redundant api calls.
        if (renderData->GetMaterial()->GetSide() != Material::FrontSide) {
            glEnable (GL_CULL_FACE);
            glCullFace (GL_BACK);
        }

        if (renderData->GetOffset()) {
            glDisable (GL_POLYGON_OFFSET_FILL);
        }

        if (!renderData->GetDepthTest()) {
            glEnable (GL_DEPTH_TEST);
        }

        if (!renderData->GetAlphaBlend()) {
            glEnable (GL_BLEND);
        }
    }

    return viewProjectionM;
}

void Scene::SetFaceCulling(int cullFace) {
    switch (cullFace) {
    case Material::BackSide:
        glEnable (GL_CULL_FACE);
        glCullFace (GL_FRONT);
        break;

    case Material::DoubleSide:
        glDisable(GL_CULL_FACE);
        break;

        // FrontSide as Default
    default:
        glEnable(GL_CULL_FACE);
        glCullFace (GL_BACK);
        break;
    }
}

IntersectRayBoundsResult Scene::IntersectRayBounds(SceneObject *target, bool axisInWorld) {

    Matrix4f worldToModelM = target->GetMatrixWorld().Inverted();
    Matrix4f invertedCenterViewM = centerViewM.Inverted();
    Vector3f inWorldCenterViewPos = invertedCenterViewM.GetTranslation();
    Quatf centerViewRot = Quatf(invertedCenterViewM);

    const Vector3f rayStart = worldToModelM.Transform(inWorldCenterViewPos);
    const Vector3f rayDir = worldToModelM.Transform(centerViewRot.Rotate(Vector3f(0.0f, 0.0f, -1.0f))) - rayStart;
    const BoundingBoxInfo boundingBoxInfo = target->GetRenderData()->GetMesh()->GetBoundingBoxInfo();
    float t0 = 0.0f;
    float t1 = 0.0f;

    bool intersected = Intersect_RayBounds(rayStart, rayDir, boundingBoxInfo.mins, boundingBoxInfo.maxs, t0, t1);

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

}
