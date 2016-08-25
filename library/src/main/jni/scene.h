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

#ifndef SCENE_H_
#define SCENE_H_

#include "SceneObject.h"

using namespace OVR;

namespace mgn {

struct IntersectRayBoundsResult {
    bool intersected;
    Vector3f first;
    Vector3f second;
};

class Scene: public SceneObject {
public:
    Scene();

    Array<SceneObject*> GetWholeSceneObjects();

    void SetCenterViewMatrix(const Matrix4f & m);

    const Matrix4f & GetCenterViewMatrix();

    const Matrix4f & GetCenterViewMatrix() const;

    IntersectRayBoundsResult IntersectRayBounds(SceneObject * target, bool axisInWorld);

    void SetViewPosition(const Vector3f & pos);

    const Vector3f & GetViewPosition();

    const Vector3f & GetViewPosition() const;

    // Populate frameMatrices with the view and projection matrices for the scene.
    void GetFrameMatrices(const ovrHeadModelParms & headModelParms,
                          const float fovDegreesX, const float fovDegreesY,
                          ovrFrameMatrices & frameMatrices) const;
    // Generates a sorted surface list for the scene (including emit surfaces).
    void GenerateFrameSurfaceList(const ovrFrameMatrices & matrices, Array<ovrDrawSurface> & surfaceList);

    void SetClearEnabled(bool clearEnabled);

    bool IsClearEnabled();

    void SetClearColor(const Vector4f &clearColor);

    const Vector4f &GetClearColor();

    const Vector4f &GetClearColor() const;

private:
    Scene(const Scene& scene);
    Scene(Scene&& scene);
    Scene& operator=(const Scene& scene);
    Scene& operator=(Scene&& scene);

private:

    Vector3f viewPosition;
    Matrix4f centerViewM;
    bool clearEnabled;
    Vector4f clearColor;

};

}
#endif
