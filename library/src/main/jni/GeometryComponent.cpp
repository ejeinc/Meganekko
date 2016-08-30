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

void Java_org_meganekkovr_GeometryComponent_build(
    JNIEnv *env, jclass clazz, jlong nativePtr, jfloatArray jPositions,
    jfloatArray jColors, jfloatArray jUVs, jintArray jTriangles) {

  VertexAttribs attribs;

  // positions
  jsize jPositionsSize = env->GetArrayLength(jPositions);
  jfloat *jPositionsElements = env->GetFloatArrayElements(jPositions, 0);

  for (int i = 0; i < jPositionsSize; i += 3) {
    float x = jPositionsElements[i];
    float y = jPositionsElements[i + 1];
    float z = jPositionsElements[i + 2];
    attribs.position.PushBack(Vector3f(x, y, z));
    attribs.color.PushBack(Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
  }

  env->ReleaseFloatArrayElements(jPositions, jPositionsElements, 0);

  // colors
  jsize jColorsSize = env->GetArrayLength(jColors);
  jfloat *jColorsElements = env->GetFloatArrayElements(jColors, 0);

  for (int i = 0; i < jColorsSize; i += 4) {
    float r = jColorsElements[i];
    float g = jColorsElements[i + 1];
    float b = jColorsElements[i + 2];
    float a = jColorsElements[i + 3];
    attribs.color.PushBack(Vector4f(r, g, b, a));
  }

  env->ReleaseFloatArrayElements(jColors, jColorsElements, 0);

  // UVs
  jsize jUVsSize = env->GetArrayLength(jUVs);
  jfloat *jUVsElements = env->GetFloatArrayElements(jUVs, 0);

  for (int i = 0; i < jUVsSize; i += 2) {
    float x = jUVsElements[i];
    float y = jUVsElements[i + 1];
    attribs.uv0.PushBack(Vector2f(x, y));
  }

  env->ReleaseFloatArrayElements(jUVs, jUVsElements, 0);

  // triangles
  jsize jTrianglesSize = env->GetArrayLength(jTriangles);
  jint *jTrianglesElements = env->GetIntArrayElements(jTriangles, 0);

  Array<TriangleIndex> indices;
  indices.Resize(jTrianglesSize);

  for (int i = 0; i < jTrianglesSize; ++i) {
    indices[i] = jTrianglesElements[i];
  }

  env->ReleaseIntArrayElements(jTriangles, jTrianglesElements, 0);

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