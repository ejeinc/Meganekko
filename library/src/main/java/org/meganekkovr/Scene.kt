package org.meganekkovr

open class Scene : Entity() {
    private var initialized: Boolean = false

    /**
     * Called before first rendering.
     */
    open fun init() {}

    /**
     * Called when this Scene is activated by [MeganekkoApp.setScene].
     * If you override this method, you must call `super.onStartRendering()`.
     */
    open fun onStartRendering() {
        if (!initialized) {
            init()
            initialized = true
        }
    }

    /**
     * Called when other Scene is activated by [MeganekkoApp.setScene].
     */
    open fun onStopRendering() {}

    open fun onKeyPressed(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }

    open fun onKeyDoubleTapped(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }

    open fun onKeyLongPressed(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }

    open fun onKeyDown(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }

    open fun onKeyUp(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }

    open fun onKeyMax(keyCode: Int, repeatCount: Int): Boolean {
        return false
    }
}
