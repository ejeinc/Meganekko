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

using namespace OVR;

namespace mgn {

class SceneObject: public HybridObject {
public:
    SceneObject();

    void SetVisible(bool visibility);

    bool IsVisible() const {
        return visible;
    }

    void AttachRenderData(RenderData* render_data);

    void DetachRenderData();

    RenderData* GetRenderData() const {
        return renderData;
    }

    SceneObject* GetParent() const {
        return parent;
    }

    const Array<SceneObject*>& GetChildren() const {
        return children;
    }

    void AddChildObject(SceneObject* child);

    void RemoveChildAt(int index);

    int GetChildrenCount() const;

    SceneObject* GetChildByIndex(int index);

    bool IsColliding(SceneObject* scene_object);

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
    Array<SceneObject*> children;

    bool      visible;
};

}
#endif
