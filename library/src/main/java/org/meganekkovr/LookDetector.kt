package org.meganekkovr

import org.joml.Vector3f

class LookDetector private constructor(private val appPtr: Long) {

    @JvmOverloads
    fun isLookingAt(entity: Entity, firstIntersect: FloatArray? = null, secondIntersect: FloatArray? = null, axisInWorld: Boolean = false): Boolean {

        if (!entity.isShown) {
            return false
        }

        val geometry = entity.getComponent<GeometryComponent>() ?: return false
        return isLookingAt(appPtr, entity.nativePointer, geometry.nativePointer, firstIntersect, secondIntersect, axisInWorld)
    }

    @JvmOverloads
    fun getLookingPoint(entity: Entity, axisInWorkd: Boolean = false): Vector3f? {
        val first = FloatArray(3)
        val looking = isLookingAt(entity, first, null, axisInWorkd)
        return if (looking) {
            Vector3f(first[0], first[1], first[2])
        } else {
            null
        }
    }

    private external fun isLookingAt(appPtr: Long, entityPtr: Long, geometryComponentPtr: Long, first: FloatArray?, second: FloatArray?, axisInWorld: Boolean): Boolean

    companion object {

        @JvmStatic
        lateinit var instance: LookDetector

        @Synchronized
        @JvmStatic
        internal fun init(appPtr: Long) {
            instance = LookDetector(appPtr)
        }
    }
}
