package io.github.poeschl.pixelflutmaze.shared

abstract class Painter {

    private var runningRender = true

    fun start() {
        init()

        while (runningRender) {
            render()
        }

        afterStop()
    }

    abstract fun init()

    abstract fun render()

    abstract fun afterStop()
}
