package io.github.poeschl.pixelflutmaze.labyrinth

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import de.amr.graph.core.api.TraversalState
import de.amr.graph.grid.impl.GridFactory
import de.amr.graph.grid.impl.GridGraph
import de.amr.graph.grid.impl.Top4
import de.amr.maze.alg.traversal.GrowingTreeAlwaysRandom
import io.github.poeschl.pixelflutmaze.shared.Painter
import io.github.poeschl.pixelflutmaze.shared.PixelFlutInterface
import io.github.poeschl.pixelflutmaze.shared.Point
import io.github.poeschl.pixelflutmaze.shared.drawRect
import java.awt.Color
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::Args).run {
        println("Start drawing on $host:$port")
        LabyrinthDrawer(host, port, x, y, width, height).start()
    }
}

class LabyrinthDrawer(host: String, port: Int, xStart: Int, yStart: Int, width: Int, height: Int) : Painter() {
    companion object {

        private val MAZE_START = Point(0, 0)
    }

    private val drawInterface = PixelFlutInterface(host, port)
    private val daemonTimer = Timer(true)
    private val areaSize = Pair(width, height)
    private val areaOrigin = Point(xStart, yStart)
    private val maze = Maze(areaOrigin, areaSize)

    private var genTimer: TimerTask? = null

    override fun init() {
        generateMazePixels()
    }

    override fun render() {
        maze.draw(drawInterface)
    }

    override fun afterStop() {
        drawInterface.close()
        daemonTimer.cancel()
    }

    private fun generateMazePixels() {
        print("Update Maze...")
        val genMilli = measureTimeMillis {
            val mazeGrid = createNewMazeGrid(MAZE_START, maze.mazeCellSize)
            print("Redraw Maze...")
            maze.clear()
            drawRect(drawInterface, areaOrigin, areaSize, Color.BLACK)
            maze.updateMaze(mazeGrid.edges())
        }
        println("Maze updated in $genMilli ms")
    }

    private fun createNewMazeGrid(start: Point, size: Pair<Int, Int>): GridGraph<TraversalState, Int> {
        val grid = GridFactory.emptyGrid(
            size.first, size.second, Top4.get(),
            TraversalState.UNVISITED, 0
        )
        val mazeGen = GrowingTreeAlwaysRandom(grid)
        mazeGen.createMaze(start.x, start.y)
        return grid
    }

    private fun setTimerTime(delayMinutes: Long) {

        if (delayMinutes < 1) {
            genTimer?.cancel()
            println("Disabled timer")
        } else {
            genTimer?.cancel()
            val delayseconds = delayMinutes * 60 * 1000
            genTimer = daemonTimer.schedule(delayseconds, delayseconds) { generateMazePixels() }
            println("Set timer to a period of $delayMinutes minute")
        }
    }
}

class Args(parser: ArgParser) {
    val host by parser.storing("--host", help = "The host of the pixelflut server").default("localhost")
    val port by parser.storing("-p", "--port", help = "The port of the server") { toInt() }.default(1234)
    val x by parser.storing("-x", help = "The x start position") { toInt() }.default(0)
    val y by parser.storing("-y", help = "The y start position") { toInt() }.default(0)
    val width by parser.storing("--width", help = "The maze width") { toInt() }.default(500)
    val height by parser.storing("--height", help = "The maze height") { toInt() }.default(500)
}
