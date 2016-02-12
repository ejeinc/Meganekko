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

    static inline jobject ToJava(JNIEnv * jni, Vector3f vec) {
        const jclass clazz = jni->FindClass("org/joml/Vector3f");
        const jmethodID constructor = jni->GetMethodID(clazz, "<init>", "(FFF)V" );
        return jni->NewObject(clazz, constructor, vec.x, vec.y, vec.z);
    }

    static inline jobject ToJava(JNIEnv * jni, Quatf q) {
        const jclass clazz = jni->FindClass("org/joml/Quaternionf");
        const jmethodID constructor = jni->GetMethodID(clazz, "<init>", "(FFFF)V" );
        return jni->NewObject(clazz, constructor, q.x, q.y, q.z, q.w);
    }
}
#endif