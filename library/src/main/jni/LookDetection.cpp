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
#include "LookDetection.h"

using namespace OVR;

namespace mgn {

IntersectRayBoundsResult IntersectRayBounds(const Matrix4f &centerViewMatrix,
                                            const Matrix4f &targetWorldMatrix,
                                            const GlGeometry &targetGeometry,
                                            bool axisInWorld) {

  Matrix4f worldToModelM = targetWorldMatrix.Inverted();
  Matrix4f invertedCenterViewM = centerViewMatrix.Inverted();
  Vector3f inWorldCenterViewPos = invertedCenterViewM.GetTranslation();
  Quatf centerViewRot = Quatf(invertedCenterViewM);

  const Vector3f rayStart = worldToModelM.Transform(inWorldCenterViewPos);
  const Vector3f rayDir = worldToModelM.Transform(centerViewRot.Rotate(
                              Vector3f(0.0f, 0.0f, -1.0f))) -
                          rayStart;
  const Vector3f boundingBoxMins = targetGeometry.localBounds.GetMins();
  const Vector3f boundingBoxMaxs = targetGeometry.localBounds.GetMaxs();
  float t0 = 0.0f;
  float t1 = 0.0f;

  bool intersected = Intersect_RayBounds(rayStart, rayDir, boundingBoxMins,
                                         boundingBoxMaxs, t0, t1);

  IntersectRayBoundsResult result;
  result.intersected = intersected && t0 > 0;

  if (intersected) {
    result.first = rayStart + t0 * rayDir;
    result.second = rayStart + t1 * rayDir;

    if (axisInWorld) {
      result.first = targetWorldMatrix.Transform(result.first);
      result.second = targetWorldMatrix.Transform(result.second);
    }
  }

  return result;
}
}