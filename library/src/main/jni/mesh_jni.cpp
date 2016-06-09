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
#include "Mesh.h"

namespace mgn {
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_eje_1c_meganekko_Mesh_initNativeInstance(JNIEnv* env, jobject obj) {
    return reinterpret_cast<jlong>(new Mesh());
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_build(JNIEnv * env, jobject obj, jlong jmesh,
        jfloatArray jPositions, jfloatArray jColors, jfloatArray jUVs, jintArray jTriangles) {

    VertexAttribs attribs;

    // positions
    jsize jPositionsSize = env->GetArrayLength(jPositions);
    jfloat *jPositionsElements = env->GetFloatArrayElements(jPositions, 0);

    // for bounding box
    Vector3f mins;
    Vector3f maxs;

    for (int i = 0; i < jPositionsSize; i += 3) {
        float x = jPositionsElements[i];
        float y = jPositionsElements[i + 1];
        float z = jPositionsElements[i + 2];
        attribs.position.PushBack(Vector3f(x, y, z));
        attribs.color.PushBack(Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        // calc mins and maxs
        mins.x = Alg::Min(mins.x, x);
        mins.y = Alg::Min(mins.y, y);
        mins.z = Alg::Min(mins.z, z);

        maxs.x = Alg::Max(maxs.x, x);
        maxs.y = Alg::Max(maxs.y, y);
        maxs.z = Alg::Max(maxs.z, z);
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

    Array< TriangleIndex > indices;
    indices.Resize(jTrianglesSize);

    for (int i = 0; i < jTrianglesSize; ++i) {
        indices[i] = jTrianglesElements[i];
    }

    env->ReleaseIntArrayElements(jTriangles, jTrianglesElements, 0);

    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(GlGeometry(attribs, indices));
    mesh->SetBoundingBox(mins, maxs);
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildTesselatedQuad(JNIEnv * env, jobject obj, jlong jmesh, jint horizontal, jint vertical, jboolean twoSided) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildTesselatedQuad(horizontal, vertical, twoSided));
    mesh->SetBoundingBox(Vector3f(-1.0f, -1.0f, 0.0f), Vector3f(1.0f, 1.0f, 0.0f));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildFadedScreenMask(JNIEnv * env, jobject obj, jlong jmesh, jfloat xFraction, jfloat yFraction) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildFadedScreenMask(xFraction, yFraction));
    mesh->SetBoundingBox(Vector3f(-1.0f, -1.0f, 0.0f), Vector3f(1.0f, 1.0f, 0.0f));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildVignette(JNIEnv * env, jobject obj, jlong jmesh, jfloat xFraction, jfloat yFraction) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildVignette(xFraction, yFraction));
    mesh->SetBoundingBox(Vector3f(-1.0f, -1.0f, 0.0f), Vector3f(1.0f, 1.0f, 0.0f));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildTesselatedCylinder(JNIEnv * env, jobject obj, jlong jmesh,
        jfloat radius, jfloat height, jint horizontal, jint vertical, jfloat uScale, jfloat vScale) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildTesselatedCylinder(radius, height, horizontal, vertical, uScale, vScale));
    mesh->SetBoundingBox(Vector3f(-radius, -radius, -height), Vector3f(radius, radius, height)); // TODO Help! Could you calculate right value?
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildDome(JNIEnv * env, jobject obj, jlong jmesh, jfloat latRads, jfloat uScale, jfloat vScale) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildDome(latRads, uScale, vScale));
    mesh->SetBoundingBox(Vector3f(-100, -100, -100), Vector3f(100, 100, 100)); // TODO Help! Could you calculate right value?
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildGlobe(JNIEnv * env, jobject obj, jlong jmesh, jfloat uScale, jfloat vScale) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildGlobe(uScale, vScale));
    mesh->SetBoundingBox(Vector3f(-100, -100, -100), Vector3f(100, 100, 100)); // TODO Help! Could you calculate right value?
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildSpherePatch(JNIEnv * env, jobject obj, jlong jmesh, jfloat fov) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildSpherePatch(fov));
    mesh->SetBoundingBox(Vector3f(-100, -100, -100), Vector3f(100, 100, 100)); // TODO Help! Could you calculate right value?
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildCalibrationLines(JNIEnv * env, jobject obj, jlong jmesh, jint extraLines, jboolean fullGrid) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildCalibrationLines(extraLines, fullGrid));
    mesh->SetBoundingBox(Vector3f(-1.0f, -1.0f, -1.0f), Vector3f(1.0f, 1.0f, 1.0f));
}

JNIEXPORT void JNICALL
Java_com_eje_1c_meganekko_Mesh_buildUnitCubeLines(JNIEnv * env, jobject obj, jlong jmesh) {
    Mesh* mesh = reinterpret_cast<Mesh*>(jmesh);
    mesh->SetGeometry(BuildUnitCubeLines());
    mesh->SetBoundingBox(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(1.0f, 1.0f, 1.0f));
}

#ifdef __cplusplus 
} // extern C
#endif
} // namespace mgn
