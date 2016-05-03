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
#include "SceneObject.h"

#include "RenderData.h"
#include "Mesh.h"

namespace mgn {
    SceneObject::SceneObject() : HybridObject(),
        position(Vector3f()),
        scale(Vector3f(1, 1, 1)),
        rotation(Quatf()),
        render_data_(),
        parent_(),
        children_(),
        visible_(true),
        in_frustum_(false),
        query_currently_issued_(false),
        vis_count_(0),
        lod_min_range_(0),
        lod_max_range_(MAXFLOAT),
        using_lod_(false) {

    // Occlusion query setup
    queries_ = new GLuint[1];
    glGenQueries(1, queries_);
}

SceneObject::~SceneObject() {
    delete queries_;
}

void SceneObject::attachRenderData(SceneObject* self, RenderData* render_data) {
    if (render_data_) {
        detachRenderData();
    }
    SceneObject* owner_object(render_data->owner_object());
    if (owner_object) {
        owner_object->detachRenderData();
    }
    render_data_ = render_data;
    render_data->set_owner_object(self);
}

void SceneObject::detachRenderData() {
    if (render_data_) {
        render_data_->removeOwnerObject();
        render_data_ = NULL;
    }
}

void SceneObject::addChildObject(SceneObject* self, SceneObject* child) {
    for (SceneObject* parent = parent_; parent; parent = parent->parent_) {
        if (child == parent) {
            std::string error =
                    "SceneObject::addChildObject() : cycle of scene objects is not allowed.";
            throw error;
        }
    }
    children_.push_back(child);
    child->parent_ = self;
}

void SceneObject::removeChildObject(SceneObject* child) {
    if (child->parent_ == this) {
        children_.erase(std::remove(children_.begin(), children_.end(), child),
                children_.end());
        child->parent_ = NULL;
    }
}

int SceneObject::getChildrenCount() const {
    return children_.size();
}

SceneObject* SceneObject::getChildByIndex(int index) {
    if (index < children_.size()) {
        return children_[index];
    } else {
        std::string error = "SceneObject::getChildByIndex() : Out of index.";
        throw error;
    }
}

void SceneObject::set_visible(bool visibility = true) {

    //HACK
    //If checked every frame, queries may return
    //an inconsistent result when used with bounding boxes.

    //We need to make sure that the object's visibility status is consistent before
    //changing the status to avoid flickering artifacts.

    if (visibility == true)
        vis_count_++;
    else
        vis_count_--;

    if (vis_count_ > check_frames_) {
        visible_ = true;
        vis_count_ = 0;
    } else if (vis_count_ < (-1 * check_frames_)) {
        visible_ = false;
        vis_count_ = 0;
    }
}

bool SceneObject::isColliding(SceneObject *scene_object) {

    //Get the transformed bounding boxes in world coordinates and check if they intersect
    //Transformation is done by the getTransformedBoundingBoxInfo method in the Mesh class

    float this_object_bounding_box[6], check_object_bounding_box[6];

    OVR::Matrix4f this_object_model_matrix = this->render_data()->owner_object()->GetModelMatrix();
    this->render_data()->mesh()->getTransformedBoundingBoxInfo(&this_object_model_matrix, this_object_bounding_box);

    OVR::Matrix4f check_object_model_matrix = scene_object->render_data()->owner_object()->GetModelMatrix();
    scene_object->render_data()->mesh()->getTransformedBoundingBoxInfo(&check_object_model_matrix, check_object_bounding_box);

    bool result = (this_object_bounding_box[3] > check_object_bounding_box[0]
            && this_object_bounding_box[0] < check_object_bounding_box[3]
            && this_object_bounding_box[4] > check_object_bounding_box[1]
            && this_object_bounding_box[1] < check_object_bounding_box[4]
            && this_object_bounding_box[5] > check_object_bounding_box[2]
            && this_object_bounding_box[2] < check_object_bounding_box[5]);

    return result;
}

void SceneObject::SetPosition(const Vector3f& position) {
    this->position = position;
    Invalidate(false);
}

void SceneObject::SetScale(const Vector3f& scale) {
    this->scale = scale;
    Invalidate(false);
}

void SceneObject::SetRotation(const Quatf& rotation) {
    this->rotation = rotation;
    Invalidate(true);
}

const Matrix4f & SceneObject::GetModelMatrix() {
    
    if (modelMatrixInvalidated) {
        UpdateModelMatrix();
        modelMatrixInvalidated = false;
    }
        
    return modelMatrix;
}

void SceneObject::UpdateModelMatrix() {
    Matrix4f translationMatrix = Matrix4f::Translation(position);
    Matrix4f rotationMatrix = Matrix4f(rotation);
    Matrix4f scaleMatrix = Matrix4f::Scaling(scale);
    Matrix4f localMatrix = translationMatrix * rotationMatrix * scaleMatrix;

    if (parent() != 0) {
        Matrix4f matrix = parent()->GetModelMatrix() * localMatrix;
        this->modelMatrix = matrix;
    } else {
        this->modelMatrix = localMatrix;
    }
}

float inline sign(float a) {
    return a >= 0 ? 1.0f : -1.0f;
}

void SceneObject::SetModelMatrix(const Matrix4f & matrix) {

    float xs = sign(matrix.M[0][0] * matrix.M[1][0] * matrix.M[2][0] * matrix.M[3][0]);
    float ys = sign(matrix.M[0][1] * matrix.M[1][1] * matrix.M[2][1] * matrix.M[3][1]);
    float zs = sign(matrix.M[0][2] * matrix.M[1][2] * matrix.M[2][2] * matrix.M[3][2]);

    Vector3f newScale(
            xs * Vector3f(matrix.M[0][0], matrix.M[1][0], matrix.M[2][0]).Length(),
            ys * Vector3f(matrix.M[0][1], matrix.M[1][1], matrix.M[2][1]).Length(),
            zs * Vector3f(matrix.M[0][2], matrix.M[1][2], matrix.M[2][2]).Length());

    Matrix3f rotationMatrix(
            matrix.M[0][0] / newScale.x, matrix.M[0][1] / newScale.y, matrix.M[0][2] / newScale.z,
            matrix.M[1][0] / newScale.x, matrix.M[1][1] / newScale.y, matrix.M[1][2] / newScale.z,
            matrix.M[2][0] / newScale.x, matrix.M[2][1] / newScale.y, matrix.M[2][2] / newScale.z);

    position = matrix.GetTranslation();
    scale = newScale;
    rotation = Quatf(rotationMatrix);
}

void SceneObject::Invalidate(bool rotationUpdated) {
    if (!modelMatrixInvalidated) {
        modelMatrixInvalidated = true;
        std::vector<SceneObject*> objects = children();
        for (auto it = objects.begin(); it != objects.end(); ++it) {
            (*it)->Invalidate(false);
        }
    }
    
    if (rotationUpdated) {
        // scale rotation if needed to avoid overflow
        static const float threshold = sqrt(FLT_MAX) / 2.0f;
        static const float scale_factor = 0.5f / sqrt(FLT_MAX);
        if (rotation.w > threshold || rotation.x > threshold
                || rotation.y > threshold || rotation.z > threshold) {
            rotation.w *= scale_factor;
            rotation.x *= scale_factor;
            rotation.y *= scale_factor;
            rotation.z *= scale_factor;
        }
    }
}

}
