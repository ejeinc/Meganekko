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
#include "util/GL.h"

using namespace OVR;

namespace mgn {
class Camera;
class RenderData;

class SceneObject: public HybridObject {
public:
    SceneObject();
    ~SceneObject();

    void SetInFrustum(bool in_frustum = true) {
        in_frustum_ = in_frustum;
    }

    bool IsInFrustum() const {
        return in_frustum_;
    }

    void SetVisible(bool visibility);
    bool IsVisible() const {
        return visible_;
    }

    void SetQueryIssued(bool issued = true) {
        query_currently_issued_ = issued;
    }

    bool IsQueryIssued() {
        return query_currently_issued_;
    }

    void AttachRenderData(SceneObject* self, RenderData* render_data);
    void DetachRenderData();

    RenderData* GetRenderData() const {
        return render_data_;
    }

    SceneObject* GetParent() const {
        return parent_;
    }

    const std::vector<SceneObject*>& GetChildren() const {
        return children_;
    }

    void AddChildObject(SceneObject* self, SceneObject* child);

    void RemoveChildObject(SceneObject* child);

    int GetChildrenCount() const;

    SceneObject* GetChildByIndex(int index);

    GLuint * GetOcclusionArray() {
        return queries_;
    }

    bool IsColliding(SceneObject* scene_object);

    void SetLODRange(float minRange, float maxRange) {
        lod_min_range_ = minRange * minRange;
        lod_max_range_ = maxRange * maxRange;
        using_lod_ = true;
    }

    float GetLODMinRange() {
        return lod_min_range_;
    }

    float GetLODMaxRange() {
        return lod_max_range_;
    }

    bool InLODRange(float distance_from_camera) {
        if(!using_lod_) {
            return true;
        }
        if(distance_from_camera >= lod_min_range_ &&
           distance_from_camera < lod_max_range_) {
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
    
    const Matrix4f & GetModelMatrix();
    
    void UpdateModelMatrix();
    
    void SetModelMatrix(const Matrix4f & matrix);
    
    void Invalidate(bool rotationUpdated);

private:
    SceneObject(const SceneObject& scene_object);
    SceneObject(SceneObject&& scene_object);
    SceneObject& operator=(const SceneObject& scene_object);
    SceneObject& operator=(SceneObject&& scene_object);

private:

    Vector3f position;
    Vector3f scale;
    Quatf rotation;
    Matrix4f modelMatrix;
    bool modelMatrixInvalidated = true;
    
    RenderData* render_data_;
    SceneObject* parent_;
    std::vector<SceneObject*> children_;
    float lod_min_range_;
    float lod_max_range_;
    bool using_lod_;

    //Flags to check for visibility of a node and
    //whether there are any pending occlusion queries on it
    const int check_frames_ = 12;
    int vis_count_;
    bool visible_;
    bool in_frustum_;
    bool query_currently_issued_;
    GLuint *queries_;
};

}
#endif
