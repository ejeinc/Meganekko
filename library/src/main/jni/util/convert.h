
#include "includes.h"

#ifndef CONVERT_H_
#define CONVERT_H_

using namespace OVR;

namespace mgn {
    
    /**
     * Fill Java float array with float container value from 0 index.
     * T value argument will be a float container such as Vector3f, Quatf or Matrix4f.
     * This method does not check array length. Use carefully.
     */
    template<typename T>
    static inline void FillElementsUnSafe(JNIEnv * jni, jfloatArray array, T value) {
        jsize size = sizeof(T) / sizeof(jfloat);
        jni->SetFloatArrayRegion(array, 0, size, reinterpret_cast<jfloat*>(&value));
    }
}
#endif