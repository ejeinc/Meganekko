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
 * The mesh for rendering.
 ***************************************************************************/
#include "includes.h"

#ifndef MESH_H_
#define MESH_H_

#include "HybridObject.h"
#include "Material.h"
#include "util/GL.h"

namespace mgn {
class Mesh: public HybridObject {
public:
    Mesh() :
            vertices_(), normals_(), tex_coords_(), triangles_(), float_vectors_(), vec2_vectors_(), vec3_vectors_(), vec4_vectors_(), vertexLoc_(
                    -1), normalLoc_(-1), texCoordLoc_(-1), have_bounding_box_(false), have_bounding_sphere_(false),
                     vaoID(0), triangle_vboID(0), vert_vboID(0), norm_vboID(0), tex_vboID(0), numTriangles_(0) {
    }

    ~Mesh() {
        CleanUp();
    }

    void CleanUp() {
        std::vector<OVR::Vector3f> vertices;
        vertices.swap(vertices_);
        std::vector<OVR::Vector3f> normals;
        normals.swap(normals_);
        std::vector<OVR::Vector2f> tex_coords;
        tex_coords.swap(tex_coords_);
        std::vector<unsigned short> triangles;
        triangles.swap(triangles_);

        DeleteVaos();
    }

    void DeleteVaos() {
        
        if (vaoID != 0) {
            gl_delete.queueVertexArray(vaoID);
            vaoID = 0;
        }

        if (triangle_vboID != 0) {
            gl_delete.queueBuffer(triangle_vboID);
            triangle_vboID = 0;
        }

        if (vert_vboID != 0) {
            gl_delete.queueBuffer(vert_vboID);
            vert_vboID = 0;
        }

        if (norm_vboID != 0) {
            gl_delete.queueBuffer(norm_vboID);
            norm_vboID = 0;
        }

        if (tex_vboID != 0) {
            gl_delete.queueBuffer(tex_vboID);
            tex_vboID = 0;
        }
        have_bounding_box_ = false;
        have_bounding_sphere_ = false;
    }

    std::vector<OVR::Vector3f>& GetVertices() {
        return vertices_;
    }

    const std::vector<OVR::Vector3f>& GetVertices() const {
        return vertices_;
    }

    void SetVertices(const std::vector<OVR::Vector3f>& vertices) {
        vertices_ = vertices;
        getBoundingSphereInfo(); // calculate bounding sphere
    }

    void SetVertices(std::vector<OVR::Vector3f>&& vertices) {
        vertices_ = std::move(vertices);
        getBoundingSphereInfo(); // calculate bounding sphere
    }

    std::vector<OVR::Vector3f>& GetNormals() {
        return normals_;
    }

    const std::vector<OVR::Vector3f>& GetNormals() const {
        return normals_;
    }

    void SetNormals(const std::vector<OVR::Vector3f>& normals) {
        normals_ = normals;
    }

    void SetNormals(std::vector<OVR::Vector3f>&& normals) {
        normals_ = std::move(normals);
    }

    std::vector<OVR::Vector2f>& GetTexCoords() {
        return tex_coords_;
    }

    const std::vector<OVR::Vector2f>& GetTexCoords() const {
        return tex_coords_;
    }

    void SetTexCoords(const std::vector<OVR::Vector2f>& tex_coords) {
        tex_coords_ = tex_coords;
        vao_dirty_ = true;
    }

    void SetTexCoords(std::vector<OVR::Vector2f>&& tex_coords) {
        tex_coords_ = std::move(tex_coords);
        vao_dirty_ = true;
    }

    std::vector<unsigned short>& GetTriangles() {
        return triangles_;
    }

    const std::vector<unsigned short>& GetTriangles() const {
        return triangles_;
    }

    void SetTriangles(const std::vector<unsigned short>& triangles) {
        triangles_ = triangles;
    }

    void SetTriangles(std::vector<unsigned short>&& triangles) {
        triangles_ = std::move(triangles);
    }

    std::vector<float>& GetFloatVector(std::string key) {
        auto it = float_vectors_.find(key);
        if (it != float_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getFloatVector() : " + key
                    + " not found";
            throw error;
        }
    }

    const std::vector<float>& GetFloatVector(std::string key) const {
        auto it = float_vectors_.find(key);
        if (it != float_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getFloatVector() : " + key
                    + " not found";
            throw error;
        }
    }

    void SetFloatVector(std::string key, const std::vector<float>& vector) {
        float_vectors_[key] = vector;
    }

    std::vector<OVR::Vector2f>& GetVec2Vector(std::string key) {
        auto it = vec2_vectors_.find(key);
        if (it != vec2_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec2Vector() : " + key + " not found";
            throw error;
        }
    }

    const std::vector<OVR::Vector2f>& GetVec2Vector(std::string key) const {
        auto it = vec2_vectors_.find(key);
        if (it != vec2_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec2Vector() : " + key + " not found";
            throw error;
        }
    }

    void SetVec2Vector(std::string key, const std::vector<OVR::Vector2f>& vector) {
        vec2_vectors_[key] = vector;
    }

    std::vector<OVR::Vector3f>& GetVec3Vector(std::string key) {
        auto it = vec3_vectors_.find(key);
        if (it != vec3_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec3Vector() : " + key + " not found";
            throw error;
        }
    }

    const std::vector<OVR::Vector3f>& GetVec3Vector(std::string key) const {
        auto it = vec3_vectors_.find(key);
        if (it != vec3_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec3Vector() : " + key + " not found";
            throw error;
        }
    }

    void SetVec3Vector(std::string key, const std::vector<OVR::Vector3f>& vector) {
        vec3_vectors_[key] = vector;
    }

    std::vector<OVR::Vector4f>& GetVec4Vector(std::string key) {
        auto it = vec4_vectors_.find(key);
        if (it != vec4_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec4Vector() : " + key + " not found";
            throw error;
        }
    }

    const std::vector<OVR::Vector4f>& GetVec4Vector(std::string key) const {
        auto it = vec4_vectors_.find(key);
        if (it != vec4_vectors_.end()) {
            return it->second;
        } else {
            std::string error = "Mesh::getVec4Vector() : " + key + " not found";
            throw error;
        }
    }

    void SetVec4Vector(std::string key, const std::vector<OVR::Vector4f>& vector) {
        vec4_vectors_[key] = vector;
    }

    Mesh* getBoundingBox();
    const float* getBoundingBoxInfo(); // Xmin, Ymin, Zmin and Xmax, Ymax, Zmax
    void getTransformedBoundingBoxInfo(OVR::Matrix4f *M,
            float *transformed_bounding_box); //Get Bounding box info transformed by matrix
    const float *getBoundingSphereInfo(); // Get bounding sphere based on the bounding box

    // /////////////////////////////////////////////////
    //  code for vertex attribute location

    void SetVertexLoc(GLuint loc) {
        vertexLoc_ = loc;
    }

    const GLuint GetVertexLoc() const {
        return vertexLoc_;
    }

    void SetNormalLoc(GLuint loc) {
        normalLoc_ = loc;
    }

    const GLuint GetNormalLoc() const {
        return normalLoc_;
    }

    void SetTexCoordLoc(GLuint loc) {
        texCoordLoc_ = loc;
    }

    const GLuint GetTexCoordLoc() const {
        return texCoordLoc_;
    }

    void SetVertexAttribLocF(GLuint location, std::string key) {
        attribute_float_keys_[location] = key;
    }

    void SetVertexAttribLocV2(GLuint location, std::string key) {
        attribute_vec2_keys_[location] = key;
    }

    void SetVertexAttribLocV3(GLuint location, std::string key) {
        attribute_vec3_keys_[location] = key;
    }

    void SetVertexAttribLocV4(GLuint location, std::string key) {
        attribute_vec4_keys_[location] = key;
    }

    // generate VAO
    void GenerateVAO();

    const GLuint GetVAOId() const {
        return vaoID;
    }

    GLuint GetNumTriangles() {
        return numTriangles_;
    }

private:
    Mesh(const Mesh& mesh);
    Mesh(Mesh&& mesh);
    Mesh& operator=(const Mesh& mesh);
    Mesh& operator=(Mesh&& mesh);

private:
    std::vector<OVR::Vector3f> vertices_;
    std::vector<OVR::Vector3f> normals_;
    std::vector<OVR::Vector2f> tex_coords_;
    std::map<std::string, std::vector<float>> float_vectors_;
    std::map<std::string, std::vector<OVR::Vector2f>> vec2_vectors_;
    std::map<std::string, std::vector<OVR::Vector3f>> vec3_vectors_;
    std::map<std::string, std::vector<OVR::Vector4f>> vec4_vectors_;
    std::vector<unsigned short> triangles_;

    // add location slot map
    std::map<int, std::string> attribute_float_keys_;
    std::map<int, std::string> attribute_vec2_keys_;
    std::map<int, std::string> attribute_vec3_keys_;
    std::map<int, std::string> attribute_vec4_keys_;

    // add vertex array object and VBO
    GLuint vaoID;
    GLuint triangle_vboID;
    GLuint vert_vboID;
    GLuint norm_vboID;
    GLuint tex_vboID;

    // attribute locations
    GLuint vertexLoc_;
    GLuint normalLoc_;
    GLuint texCoordLoc_;

    // triangle information
    GLuint numTriangles_;

    // bounding box info
    bool have_bounding_box_;
    float bounding_box_info_[6];

    // bounding sphere info
    bool have_bounding_sphere_;
    float bounding_sphere_info_[4]; // [0-2] center x,y,z; [3] radius

    bool vao_dirty_;
};
}
#endif
