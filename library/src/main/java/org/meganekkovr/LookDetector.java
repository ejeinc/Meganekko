package org.meganekkovr;

public class LookDetector {

    private static LookDetector instance;
    private final long appPtr;

    private LookDetector(long appPtr) {
        this.appPtr = appPtr;
    }

    public synchronized static void init(long appPtr) {
        if (instance != null)
            throw new IllegalStateException("init was called twice!");

        instance = new LookDetector(appPtr);
    }

    public static LookDetector getInstance() {
        return instance;
    }

    private static native boolean isLookingAt(long appPtr, long entityPtr, long geometryComponentPtr);

    public boolean isLookingAt(Entity entity) {

        if (!entity.isShown() || !entity.hasComponent(GeometryComponent.class)) {
            return false;
        }

        GeometryComponent geometry = entity.getComponent(GeometryComponent.class);
        return isLookingAt(appPtr, entity.getNativePointer(), geometry.getNativePointer());
    }
}
