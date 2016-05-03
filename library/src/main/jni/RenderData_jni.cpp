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
#include "RenderData.h"
#include "Material.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_RenderData_initNativeInstance(JNIEnv * env, jobject obj) {
    return reinterpret_cast<jlong>(new RenderData());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setMesh(JNIEnv * env, jobject obj, jlong jrenderData, jlong jmesh) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    render_data->set_mesh(mesh);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setMaterial(JNIEnv * env, jobject obj, jlong jrenderData, jlong jmaterial) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    Material* material = reinterpret_cast<Material*>(jmaterial);
    render_data->SetMaterial(material);
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderMask(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return render_data->render_mask();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderMask(JNIEnv * env, jobject obj, jlong jrenderData, jint render_mask) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_render_mask(render_mask);
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderingOrder( JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return render_data->rendering_order();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderingOrder( JNIEnv * env, jobject obj, jlong jrenderData, jint renderingOrder) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_rendering_order(renderingOrder);
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffset(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return static_cast<jboolean>(render_data->offset());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffset(JNIEnv * env, jobject obj, jlong jrenderData, jboolean offset) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_offset(static_cast<bool>(offset));
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetFactor(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return render_data->offset_factor();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetFactor(JNIEnv * env, jobject obj, jlong jrenderData, jfloat offsetFactor) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_offset_factor(offsetFactor);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetUnits(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return render_data->offset_units();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetUnits(JNIEnv * env, jobject obj, jlong jrenderData, jfloat offsetUnits) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_offset_units(offsetUnits);
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getDepthTest(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return static_cast<jboolean>(render_data->depth_test());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDepthTest(JNIEnv * env, jobject obj, jlong jrenderData, jboolean depthTest) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_depth_test(static_cast<bool>(depthTest));
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getAlphaBlend(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return static_cast<jboolean>(render_data->alpha_blend());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setAlphaBlend(JNIEnv * env, jobject obj, jlong jrenderData, jboolean alpha_blend) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_alpha_blend(static_cast<bool>(alpha_blend));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDrawMode(JNIEnv * env, jobject obj, jlong jrenderData, jint draw_mode) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    render_data->set_draw_mode(draw_mode);
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getDrawMode(JNIEnv * env, jobject obj, jlong jrenderData) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrenderData);
    return render_data->draw_mode();
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
