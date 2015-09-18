/***************************************************************************
 * installable external renderer
 ***************************************************************************/

#include "external_renderer_shader.h"

#include "objects/material.h"
#include "objects/mesh.h"
#include "objects/components/render_data.h"
#include "objects/textures/texture.h"
#include "objects/textures/external_renderer_texture.h"
#include "util/gvr_gl.h"
#include "util/gvr_log.h"

static GVRF_ExternalRenderer externalRenderer = NULL;

void GVRF_installExternalRenderer(GVRF_ExternalRenderer fct) {
    externalRenderer = fct;
}

namespace mgn {

void ExternalRendererShader::render(const OVR::Matrix4f& mvp_matrix, RenderData* render_data) {
    if (externalRenderer == NULL) {
        LOGE("External renderer not installed");
        return;
    }

    Texture* texture = render_data->pass(0)->material()->getTexture("main_texture");
    if (texture->getTarget() != ExternalRendererTexture::TARGET) {
        LOGE("External renderer only takes external renderer textures");
        return;
    }

    externalRenderer(reinterpret_cast<ExternalRendererTexture*>(texture)->getData(),
                     render_data->mesh()->getBoundingBoxInfo(), 6,
                     mvp_matrix.M[0], 16);

    checkGlError("ExternalRendererShader::render");
}

}

