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
 * JNI
 ***************************************************************************/

#include <jni.h>

#include "RenderData.h"
#include "Material.h"

namespace mgn {

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_RenderData_initNativeInstance(JNIEnv * env,
        jobject obj);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setMesh(JNIEnv * env,
        jobject obj, jlong jrender_data, jlong jmesh);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_addPass(JNIEnv* env,
        jobject obj, jlong jrender_data, jlong jrender_pass);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setLight(JNIEnv * env,
        jobject obj, jlong jrender_data, jlong jlight);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_enableLight(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_disableLight(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderMask(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderMask(JNIEnv * env,
        jobject obj, jlong jrender_data, jint render_mask);
JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderingOrder(
        JNIEnv * env, jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderingOrder(
        JNIEnv * env, jobject obj, jlong jrender_data, jint rendering_order);

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffset(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffset(JNIEnv * env,
        jobject obj, jlong jrender_data, jboolean offset);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetFactor(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetFactor(JNIEnv * env,
        jobject obj, jlong jrender_data, jfloat offset_factor);
JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetUnits(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetUnits(JNIEnv * env,
        jobject obj, jlong jrender_data, jfloat offset_units);
JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getDepthTest(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDepthTest(JNIEnv * env,
        jobject obj, jlong jrender_data, jboolean depth_test);
JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getAlphaBlend(JNIEnv * env,
        jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setAlphaBlend(JNIEnv * env,
        jobject obj, jlong jrender_data, jboolean alpha_blend);

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getDrawMode(
        JNIEnv * env, jobject obj, jlong jrender_data);

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDrawMode(
        JNIEnv * env, jobject obj, jlong jrender_data, jint draw_mode);
}
;

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_RenderData_initNativeInstance(JNIEnv * env,
    jobject obj) {
return reinterpret_cast<jlong>(new RenderData());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setMesh(JNIEnv * env,
    jobject obj, jlong jrender_data, jlong jmesh) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
render_data->set_mesh(mesh);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_addPass(JNIEnv* env,
        jobject obj, jlong jrender_data, jlong jrender_pass) {
    RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
    RenderPass* render_pass = reinterpret_cast<RenderPass*>(jrender_pass);
    render_data->add_pass(render_pass);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setLight(JNIEnv * env,
    jobject obj, jlong jrender_data, jlong jlight) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
Light* light = reinterpret_cast<Light*>(jlight);
render_data->set_light(light);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_enableLight(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->enable_light();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_disableLight(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->disable_light();
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderMask(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return render_data->render_mask();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderMask(JNIEnv * env,
    jobject obj, jlong jrender_data, jint render_mask) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_render_mask(render_mask);
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getRenderingOrder(
    JNIEnv * env, jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return render_data->rendering_order();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setRenderingOrder(
    JNIEnv * env, jobject obj, jlong jrender_data, jint rendering_order) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_rendering_order(rendering_order);
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffset(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return static_cast<jboolean>(render_data->offset());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffset(JNIEnv * env,
    jobject obj, jlong jrender_data, jboolean offset) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_offset(static_cast<bool>(offset));
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetFactor(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return render_data->offset_factor();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetFactor(JNIEnv * env,
    jobject obj, jlong jrender_data, jfloat offset_factor) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_offset_factor(offset_factor);
}

JNIEXPORT jfloat JNICALL
Java_com_eje_1c_meganekko_RenderData_getOffsetUnits(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return render_data->offset_units();
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setOffsetUnits(JNIEnv * env,
    jobject obj, jlong jrender_data, jfloat offset_units) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_offset_units(offset_units);
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getDepthTest(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return static_cast<jboolean>(render_data->depth_test());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDepthTest(JNIEnv * env,
    jobject obj, jlong jrender_data, jboolean depth_test) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_depth_test(static_cast<bool>(depth_test));
}

JNIEXPORT jboolean JNICALL
Java_com_eje_1c_meganekko_RenderData_getAlphaBlend(JNIEnv * env,
    jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return static_cast<jboolean>(render_data->alpha_blend());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setAlphaBlend(JNIEnv * env,
    jobject obj, jlong jrender_data, jboolean alpha_blend) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_alpha_blend(static_cast<bool>(alpha_blend));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_RenderData_setDrawMode(
    JNIEnv * env, jobject obj, jlong jrender_data, jint draw_mode) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
render_data->set_draw_mode(draw_mode);
}

JNIEXPORT jint JNICALL
Java_com_eje_1c_meganekko_RenderData_getDrawMode(
    JNIEnv * env, jobject obj, jlong jrender_data) {
RenderData* render_data = reinterpret_cast<RenderData*>(jrender_data);
return render_data->draw_mode();
}

}
