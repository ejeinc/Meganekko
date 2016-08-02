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
#include "Mesh.h"
#include "util/GL.h"

namespace mgn {
class Mesh;

class RenderData: public Component {
public:
    enum Queue {
        Background = 1000, Geometry = 2000, Transparent = 3000, Overlay = 4000
    };

    RenderData();
    ~RenderData();

    Mesh* GetMesh() const {
        return mesh;
    }

    void SetMesh(Mesh* mesh) {
        this->mesh = mesh;
        surfaceDef.geo = mesh->GetGeometry();
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
        return surfaceDef.graphicsCommand.GpuState.polygonOffsetEnable;
    }

    void SetOffset(bool offset) {
        surfaceDef.graphicsCommand.GpuState.polygonOffsetEnable = offset;
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
        return surfaceDef.graphicsCommand.GpuState.depthEnable;
    }

    void SetDepthTest(bool depthTest) {
        surfaceDef.graphicsCommand.GpuState.depthEnable = depthTest;
    }

    bool GetAlphaBlend() const {
        return alphaBlend;
    }

    void SetAlphaBlend(bool alphaBlend) {
        this->alphaBlend = alphaBlend;
    }

    void SetCameraDistance(float distance) {
        this->cameraDistance = distance;
    }

    float GetCameraDistance() const {
        return cameraDistance;
    }

    void UpdateSurfaceDef();

    void SetOpacity(float opacity) {
        this->opacity = opacity;
    }

    float GetOpacity() {
        return opacity;
    }

    const ovrSurfaceDef & GetSurfaceDef() {
        return surfaceDef;
    }

    void SetModelMatrix(const Matrix4f & modelMatrix) {
        this->modelMatrix = modelMatrix;
    }

    const Matrix4f & GetModelMatrix() {
        return modelMatrix;
    }

    const Matrix4f & GetModelMatrix() const {
        return modelMatrix;
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
    float offsetFactor;
    float offsetUnits;
    bool alphaBlend;
    float cameraDistance;
    ovrSurfaceDef surfaceDef;
    float opacity;
    Matrix4f modelMatrix;
    Matrix4f programMatrices[2];
    GlTexture programTexture;
    
    Matrix4f normalM = Matrix4f::Identity();
    Matrix4f topM = Matrix4f(
            1, 0, 0, 0,
            0, 0.5f, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f bottomM = Matrix4f(
            1, 0, 0, 0,
            0, 0.5f, 0, 0.5f,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f leftM = Matrix4f(
            0.5f, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );
    Matrix4f rightM = Matrix4f(
            0.5f, 0, 0, 0.5f,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1 );

    const Matrix4f & TexmForVideo(const Material::StereoMode stereoMode, const int eye) const;
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
