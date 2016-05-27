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
        material(nullptr),
        mesh(nullptr),
        visible(true),
        renderingOrder(DEFAULT_RENDERING_ORDER),
        offset(false),
        offsetFactor(0.0f),
        offsetUnits(0.0f),
        depthTest(true),
        alphaBlend(true),
        drawMode(GL_TRIANGLES) {
    }

    ~RenderData() {
    }

    Mesh* GetMesh() const {
        return mesh;
    }

    void SetMesh(Mesh* mesh) {
        this->mesh = mesh;
    }

    void SetMaterial(Material* material) {
        this->material = material;
    }
    
    Material* GetMaterial() const {
        return material;
    }
    
    Material* GetMaterial() {
        return material;
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
        return renderingOrder;
    }

    void SetRenderingOrder(int renderingOrder) {
        this->renderingOrder = renderingOrder;
    }

    bool GetOffset() const {
        return offset;
    }

    void SetOffset(bool offset) {
        this->offset = offset;
    }

    float GetOffsetFactor() const {
        return offsetFactor;
    }

    void SetOffsetFactor(float offsetFactor) {
        this->offsetFactor = offsetFactor;
    }

    float GetOffsetUnits() const {
        return offsetUnits;
    }

    void SetOffsetUnits(float offsetUnits) {
        this->offsetUnits = offsetUnits;
    }

    bool GetDepthTest() const {
        return depthTest;
    }

    void SetDepthTest(bool depthTest) {
        this->depthTest = depthTest;
    }

    bool GetAlphaBlend() const {
        return alphaBlend;
    }

    void SetAlphaBlend(bool alphaBlend) {
        this->alphaBlend = alphaBlend;
    }

    GLenum GetDrawMode() const {
        return drawMode;
    }

    void SetCameraDistance(float distance) {
        this->cameraDistance = distance;
    }

    float GetCameraDistance() const {
        return cameraDistance;
    }

    void SetDrawMode(GLenum draw_mode) {
        this->drawMode = draw_mode;
    }

private:
    RenderData(const RenderData& renderData);
    RenderData(RenderData&& renderData);
    RenderData& operator=(const RenderData& renderData);
    RenderData& operator=(RenderData&& renderData);

private:
    static const int DEFAULT_RENDERING_ORDER = Geometry;
    Mesh* mesh;
    Material * material;
    bool visible;
    int renderingOrder;
    bool offset;
    float offsetFactor;
    float offsetUnits;
    bool depthTest;
    bool alphaBlend;
    GLenum drawMode;
    float cameraDistance;
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
