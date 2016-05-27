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
 * Objects in a scene.
 ***************************************************************************/
#include "includes.h"

#ifndef SCENE_OBJECT_H_
#define SCENE_OBJECT_H_

#include "HybridObject.h"
#include "RenderData.h"
#include "util/GL.h"

using namespace OVR;

namespace mgn {
class Camera;

class SceneObject: public HybridObject {
public:
    SceneObject();
    ~SceneObject();

    void SetInFrustum(bool inFrustum = true) {
        this->inFrustum = inFrustum;
    }

    bool IsInFrustum() const {
        return inFrustum;
    }

    void SetVisible(bool visibility);

    bool IsVisible() const {
        return visible;
    }

    void SetQueryIssued(bool issued = true) {
        queryCurrentlyIssued = issued;
    }

    bool IsQueryIssued() {
        return queryCurrentlyIssued;
    }

    void AttachRenderData(SceneObject* self, RenderData* render_data);

    void DetachRenderData();

    RenderData* GetRenderData() const {
        return renderData;
    }

    SceneObject* GetParent() const {
        return parent;
    }

    const std::vector<SceneObject*>& GetChildren() const {
        return children;
    }

    void AddChildObject(SceneObject* self, SceneObject* child);

    void RemoveChildObject(SceneObject* child);

    int GetChildrenCount() const;

    SceneObject* GetChildByIndex(int index);

    GLuint * GetOcclusionArray() {
        return queries;
    }

    bool IsColliding(SceneObject* scene_object);

    void SetLODRange(float minRange, float maxRange) {
        lodMinRange = minRange * minRange;
        lodMaxRange = maxRange * maxRange;
        usingLod = true;
    }

    float GetLODMinRange() {
        return lodMinRange;
    }

    float GetLODMaxRange() {
        return lodMaxRange;
    }

    bool InLODRange(float distance_from_camera) {
        if(!usingLod) {
            return true;
        }
        if(distance_from_camera >= lodMinRange &&
           distance_from_camera < lodMaxRange) {
            return true;
        }
        return false;
    }
    
    const Vector3f & GetPosition() const {
        return position;
    }
    
    const Vector3f & GetPosition() {
        return position;
    }
    
    const Vector3f & GetScale() const {
        return scale;
    }
    
    const Vector3f & GetScale() {
        return scale;
    }
    
    const Quatf & GetRotation() const {
        return rotation;
    }
    
    const Quatf & GetRotation() {
        return rotation;
    }
    
    void SetPosition(const Vector3f& position);
    
    void SetScale(const Vector3f& scale);
    
    void SetRotation(const Quatf& rotation);
    
    const Matrix4f & GetMatrixWorld();

    const Matrix4f & GetMatrix() {
        return matrixLocal;
    }

    void SetMatrixLocal(const Matrix4f & matrix);
    
    void Invalidate(bool rotationUpdated);

    static bool CompareWithRenderingOrder(const SceneObject * left, const SceneObject * right) {
        if (left->renderData == nullptr || right->renderData == nullptr) {
            return true;
        } else {
            return left->renderData->GetRenderingOrder() < right->renderData->GetRenderingOrder();
        }
    }
private:
    SceneObject(const SceneObject& scene_object);
    SceneObject(SceneObject&& scene_object);
    SceneObject& operator=(const SceneObject& scene_object);
    SceneObject& operator=(SceneObject&& scene_object);

private:

    void UpdateMatrixWorld();
    void UpdateMatrixLocal();

    Vector3f position;
    Vector3f scale;
    Quatf    rotation;
    Matrix4f matrixLocal;
    Matrix4f matrixWorld;
    bool     matrixWorldNeedsUpdate = true;

    RenderData *              renderData;
    SceneObject *             parent;
    std::vector<SceneObject*> children;

    float lodMinRange;
    float lodMaxRange;
    bool  usingLod;

    //Flags to check for visibility of a node and
    //whether there are any pending occlusion queries on it
    const int checkFrames = 12;
    int       visCount;
    bool      visible;
    bool      inFrustum;
    bool      queryCurrentlyIssued;
    GLuint *  queries;
};

}
#endif
