#ifndef CONVERT_H_
#define CONVERT_H_

#include "jni.h"
#include "Kernel/OVR_Math.h"

using namespace OVR;

namespace mgn {

    /**
     * Convert float container values such as Vector3f or Matrix4f to Java float[].
     */
    template<typename T>
    static inline jfloatArray ToFloatArray(JNIEnv *jni, T value) {

        jsize size = sizeof(T) / sizeof(jfloat);
        jfloatArray array = jni->NewFloatArray(size);
        jni->SetFloatArrayRegion(array, 0, size, reinterpret_cast<jfloat *>(&value));

        return array;
    }

}
#endif