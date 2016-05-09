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
Java_com_eje_1c_meganekko_Mesh_buildQuad(JNIEnv * env, jobject obj, jlong jmesh, float width, float height) {

    Vector3f vertices[] = {
        Vector3f(width * -0.5f, height * 0.5f, 0.0f),
        Vector3f(width * -0.5f, height * -0.5f, 0.0f),
        Vector3f(width * 0.5f, height * 0.5f, 0.0f),
        Vector3f(width * 0.5f, height * -0.5f, 0.0f)
    };

    Vector2f uv[] = {
        Vector2f(0.0f, 0.0f),
        Vector2f(0.0f, 1.0f),
        Vector2f(1.0f, 0.0f),
        Vector2f(1.0f, 1.0f)
    };

    Vector4f color = Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    TriangleIndex triangles[] = {
        0, 1, 2, 1, 3, 2
    };

    VertexAttribs attribs;
    attribs.position.Resize(4);
    attribs.color.Resize(4);
    attribs.uv0.Resize(4);

    Vector3f mins;
    Vector3f maxs;
    
    for (int i = 0; i < 4; ++i) {
        attribs.position[i] = vertices[i];
        attribs.color[i] = color;
        attribs.uv0[i] = uv[i];
        
        mins.x = std::min(mins.x, vertices[i].x);
        mins.y = std::min(mins.y, vertices[i].y);
        mins.z = std::min(mins.z, vertices[i].z);
        
        maxs.x = std::max(maxs.x, vertices[i].x);
        maxs.y = std::max(maxs.y, vertices[i].y);
        maxs.z = std::max(maxs.z, vertices[i].z);
    }

    Array< TriangleIndex > indices;
    indices.Resize(6);

    for (int i = 0; i < 6; ++i) {
        indices[i] = triangles[i];
    }

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
