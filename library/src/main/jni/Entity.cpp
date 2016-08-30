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
#include "Entity.h"

namespace mgn {

Entity::Entity() : surfaceDef(nullptr) {}

Entity::~Entity() { delete surfaceDef; }

void Entity::SetWorldModelMatrix(const Matrix4f &m) { this->modelMatrix = m; }
const Matrix4f &Entity::GetWorldModelMatrix() { return modelMatrix; }

ovrSurfaceDef *Entity::GetOrCreateSurfaceDef() {

  if (surfaceDef == nullptr) {
    surfaceDef = new ovrSurfaceDef();

    // Enable alpha blending
    surfaceDef->graphicsCommand.GpuState.blendMode = GL_FUNC_ADD;
    surfaceDef->graphicsCommand.GpuState.blendSrc = GL_ONE;
    surfaceDef->graphicsCommand.GpuState.blendDst = GL_ONE_MINUS_SRC_ALPHA;
    surfaceDef->graphicsCommand.GpuState.blendEnable =
        ovrGpuState::BLEND_ENABLE;
  }

  return surfaceDef;
}

ovrSurfaceDef *Entity::GetSurfaceDef() { return surfaceDef; }
} // namespace mgn

/*
 * JNI methods
 */

extern "C" {

jlong Java_org_meganekkovr_Entity_newInstance(JNIEnv *jni, jobject thiz) {
  return reinterpret_cast<jlong>(new mgn::Entity());
}

void Java_org_meganekkovr_Entity_addSurfaceDef(JNIEnv *jni, jclass clazz,
                                               jlong entityPtr,
                                               jlong surfacesPtr) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);
  Array<ovrDrawSurface> *surfaces =
      reinterpret_cast<Array<ovrDrawSurface> *>(surfacesPtr);
  ovrSurfaceDef *surfaceDef = entity->GetSurfaceDef();

  if (surfaceDef) {
    // TODO
    surfaces->PushBack(
        ovrDrawSurface(entity->GetWorldModelMatrix(), surfaceDef));
  }
}

void Java_org_meganekkovr_Entity_setWorldModelMatrix(JNIEnv *jni, jclass clazz,
                                                     jlong entityPtr,
                                                     jfloatArray matrix) {

  mgn::Entity *entity = reinterpret_cast<mgn::Entity *>(entityPtr);

  jfloat *values = jni->GetFloatArrayElements(matrix, 0);

  Matrix4f m(values[0], values[4], values[8], values[12],  //
             values[1], values[5], values[9], values[13],  //
             values[2], values[6], values[10], values[14], //
             values[3], values[7], values[11], values[15]);
  entity->SetWorldModelMatrix(m);

  jni->ReleaseFloatArrayElements(matrix, values, 0);
}
} // extern "C"