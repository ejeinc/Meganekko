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

#include "includes.h"

/***************************************************************************
 * Containing data about how to render an object.
 ***************************************************************************/

#ifndef RENDER_DATA_H_
#define RENDER_DATA_H_

#include "Component.h"
#include "Material.h"
#include "util/GL.h"

namespace mgn {
class Mesh;

class RenderData: public Component {
public:
    enum Queue {
        Background = 1000, Geometry = 2000, Transparent = 3000, Overlay = 4000
    };

    RenderData() : Component(),
        material_(nullptr),
        mesh_(0),
        visible(true),
        rendering_order_(DEFAULT_RENDERING_ORDER),
        offset_(false),
        offset_factor_(0.0f),
        offset_units_(0.0f),
        depth_test_(true),
        alpha_blend_(true),
        draw_mode_(GL_TRIANGLES) {
    }

    ~RenderData() {
    }

    Mesh* GetMesh() const {
        return mesh_;
    }

    void SetMesh(Mesh* mesh) {
        mesh_ = mesh;
    }

    void SetMaterial(Material* material) {
        this->material_ = material;
    }
    
    Material* GetMaterial() const {
        return material_;
    }
    
    Material* GetMaterial() {
        return material_;
    }

    bool IsVisible() const {
        return visible;
    }

    bool IsVisible() {
        return visible;
    }

    void SetVisible(bool visible) {
        this->visible = visible;
    }

    int GetRenderingOrder() const {
        return rendering_order_;
    }

    void SetRenderingOrder(int rendering_order) {
        rendering_order_ = rendering_order;
    }

    bool GetOffset() const {
        return offset_;
    }

    void SetOffset(bool offset) {
        offset_ = offset;
    }

    float GetOffsetFactor() const {
        return offset_factor_;
    }

    void SetOffsetFactor(float offset_factor) {
        offset_factor_ = offset_factor;
    }

    float GetOffsetUnits() const {
        return offset_units_;
    }

    void SetOffsetUnits(float offset_units) {
        offset_units_ = offset_units;
    }

    bool GetDepthTest() const {
        return depth_test_;
    }

    void SetDepthTest(bool depth_test) {
        depth_test_ = depth_test;
    }

    bool GetAlphaBlend() const {
        return alpha_blend_;
    }

    void SetAlphaBlend(bool alpha_blend) {
        alpha_blend_ = alpha_blend;
    }

    GLenum GetDrawMode() const {
        return draw_mode_;
    }

    void SetCameraDistance(float distance) {
        camera_distance_ = distance;
    }

    float GetCameraDistance() const {
        return camera_distance_;
    }

    void SetDrawMode(GLenum draw_mode) {
        draw_mode_ = draw_mode;
    }

private:
    RenderData(const RenderData& renderData);
    RenderData(RenderData&& renderData);
    RenderData& operator=(const RenderData& renderData);
    RenderData& operator=(RenderData&& renderData);

private:
    static const int DEFAULT_RENDERING_ORDER = Geometry;
    Mesh* mesh_;
    Material * material_;
    bool visible;
    int rendering_order_;
    bool offset_;
    float offset_factor_;
    float offset_units_;
    bool depth_test_;
    bool alpha_blend_;
    GLenum draw_mode_;
    float camera_distance_;
};

inline bool compareRenderData(RenderData* i, RenderData* j) {
    // if it is a transparent object, sort by camera distance.
    if(i->GetRenderingOrder() == j->GetRenderingOrder() &&
       i->GetRenderingOrder() >= RenderData::Transparent &&
       i->GetRenderingOrder() < RenderData::Overlay) {
        return i->GetCameraDistance() > j->GetCameraDistance();
    }

    return i->GetRenderingOrder() < j->GetRenderingOrder();
}

inline bool compareRenderDataWithFrustumCulling(RenderData* i, RenderData* j) {
    // if either i or j is a transparent object or an overlay object
    if (i->GetRenderingOrder() >= RenderData::Transparent
            || j->GetRenderingOrder() >= RenderData::Transparent) {
        if (i->GetRenderingOrder() == j->GetRenderingOrder()) {
            // if both are either transparent or both are overlays
            // place them in reverse camera order from back to front
            return i->GetCameraDistance() < j->GetCameraDistance();
        } else {
            // if one of them is a transparent or an overlay draw by rendering order
            return i->GetRenderingOrder() < j->GetRenderingOrder();
        }
    }

    // if both are neither transparent nor overlays, place them in camera order front to back
    return i->GetCameraDistance() > j->GetCameraDistance();
}

}
#endif
