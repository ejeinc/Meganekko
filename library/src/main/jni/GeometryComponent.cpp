/*
 * Copyright 2016 eje inc.
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
#include "GeometryComponent.h"
#include "util/convert.h"

namespace mgn {

GeometryComponent::GeometryComponent() {}

GeometryComponent::~GeometryComponent() { geometry.Free(); }

GlGeometry &GeometryComponent::GetGeometry() { return geometry; }

void GeometryComponent::SetGeometry(const GlGeometry &geo) {
  this->geometry = geo;
}
}

/*
 * JNI methods
 */

extern "C" {

jlong Java_org_meganekkovr_GeometryComponent_newInstance(JNIEnv *jni,
                                                         jobject thiz) {
  return reinterpret_cast<jlong>(new mgn::GeometryComponent());
}

void Java_org_meganekkovr_GeometryComponent_buildGlobe(JNIEnv *jni,
                                                       jclass clazz,
                                                       jlong nativePtr) {
  mgn::GeometryComponent *geo =
      reinterpret_cast<mgn::GeometryComponent *>(nativePtr);
  geo->SetGeometry(BuildGlobe());
}

void Java_org_meganekkovr_GeometryComponent_buildDome(JNIEnv *jni,
                                                      jclass clazz,
                                                      jlong nativePtr,
                                                      jfloat latRads) {
  mgn::GeometryComponent *geo =
      reinterpret_cast<mgn::GeometryComponent *>(nativePtr);
  geo->SetGeometry(BuildDome(latRads));
}

void Java_org_meganekkovr_GeometryComponent_buildSpherePatch(JNIEnv *jni,
                                                             jclass clazz,
                                                             jlong nativePtr,
                                                             jfloat fov) {
  mgn::GeometryComponent *geo =
      reinterpret_cast<mgn::GeometryComponent *>(nativePtr);
  geo->SetGeometry(BuildSpherePatch(fov));
}

void Java_org_meganekkovr_GeometryComponent_build(
    JNIEnv *env, jclass clazz, jlong nativePtr, jfloatArray jPositions,
    jfloatArray jColors, jfloatArray jUVs, jintArray jTriangles) {

  VertexAttribs attribs;

  attribs.position = mgn::floatArrayToVector3fArray(env, jPositions);
  attribs.color = mgn::floatArrayToVector4fArray(env, jColors);
  attribs.uv0 = mgn::floatArrayToVector2fArray(env, jUVs);

  Array<TriangleIndex> indices = mgn::intArrayToTriangleIndex(env, jTriangles);

  mgn::GeometryComponent *geometry =
      reinterpret_cast<mgn::GeometryComponent *>(nativePtr);
  geometry->SetGeometry(GlGeometry(attribs, indices));
}

void Java_org_meganekkovr_GeometryComponent_setEntityGeometry(JNIEnv *jni,
                                                              jclass clazz,
                                                              jlong entityPtr,
                                                              jlong nativePtr) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  mgn::GeometryComponent *geo =
      reinterpret_cast<mgn::GeometryComponent *>(nativePtr);

  ovrSurfaceDef *surfaceDef = entity->GetOrCreateSurfaceDef();
  surfaceDef->geo = geo->GetGeometry();
}

} // extern "C"