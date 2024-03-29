package xyz.poeschl.pixelmaze

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import de.amr.graph.core.api.TraversalState
import de.amr.graph.grid.impl.Grid4Topology
import de.amr.graph.grid.impl.GridFactory
import de.amr.graph.grid.impl.GridGraph
import de.amr.maze.alg.traversal.GrowingTreeAlwaysRandom
import mu.KotlinLogging
import xyz.poeschl.kixelflut.Painter
import xyz.poeschl.kixelflut.Pixelflut
import xyz.poeschl.kixelflut.Point
import xyz.poeschl.kixelflut.createRectPixels
import java.awt.Color
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) = mainBody {
  ArgParser(args).parseInto(::Args).run {
    println("Start drawing on $host:$port")
    LabyrinthDrawer(host, port, x, y, width, height, timer, blanking, cellSize, drawMarker, centerTarget, randomHoles).start()
  }
}

class LabyrinthDrawer(
  host: String,
  port: Int,
  xStart: Int,
  yStart: Int,
  width: Int,
  height: Int,
  private val timer: Long,
  private val blanking: Boolean,
  cellSize: Int,
  drawMarker: Boolean,
  centerTarget: Boolean,
  randomHoles: Boolean
) : Painter() {
  companion object {
    private val LOGGER = KotlinLogging.logger { }
    private val MAZE_START = Point(0, 0)
  }

  private val drawInterface = Pixelflut(host, port)
  private val daemonTimer = Timer(true)
  private val size = Pair(width, height)
  private val origin = Point(xStart, yStart)
  private val maze = Maze(origin, size, cellSize, drawMarker, centerTarget, randomHoles)

  private var genTimer: TimerTask? = null

  override fun init() {
    generateMazePixels()
    if (timer > 0) {
      setTimerTime(timer)
    }
  }

  override fun render() {
    drawInterface.drawPixels(maze.mazeSet)
  }

  override fun afterStop() {
    drawInterface.close()
    daemonTimer.cancel()
  }

  private fun generateMazePixels() {
    LOGGER.info { "Update Maze..." }
    val genMilli = measureTimeMillis {
      val mazeGrid = createNewMazeGrid()
      LOGGER.info { "Redraw Maze..." }
      maze.clear()
      if (blanking) {
        val pixels = createRectPixels(origin, size, Color.BLACK)
        drawInterface.drawPixels(pixels)
      }
      maze.updateMaze(mazeGrid.edges())
    }
    LOGGER.info { "Maze updated in $genMilli ms" }

  }

  private fun createNewMazeGrid(): GridGraph<TraversalState, Int> {
    val grid = GridFactory.emptyGrid(
      maze.widthInCells, maze.heightInCells, Grid4Topology.get(),
      TraversalState.UNVISITED, 0
    )
    val mazeGen = GrowingTreeAlwaysRandom(grid)
    mazeGen.createMaze(MAZE_START.x, MAZE_START.y)
    return grid
  }

  private fun setTimerTime(delaySeconds: Long) {

    if (delaySeconds < 1) {
      genTimer?.cancel()
      LOGGER.info { "Disabled timer" }
    } else {
      genTimer?.cancel()
      val millis = delaySeconds * 1000
      genTimer = daemonTimer.schedule(millis, millis) { generateMazePixels() }
      LOGGER.info { "Set timer to a period of $delaySeconds seconds" }
    }
  }
}

class Args(parser: ArgParser) {
  val host by parser.storing("--host", help = "The host of the pixelflut server").default("localhost")
  val port by parser.storing("-p", "--port", help = "The port of the server") { toInt() }.default(1234)
  val x by parser.storing("-x", help = "The x start position") { toInt().coerceAtLeast(0) }.default(0)
  val y by parser.storing("-y", help = "The y start position") { toInt().coerceAtLeast(0) }.default(0)
  val width by parser.storing("--width", help = "The maze width in pixel") { toInt().coerceAtLeast(1) }.default(500)
  val height by parser.storing("--height", help = "The maze height in pixel") { toInt().coerceAtLeast(1) }.default(500)
  val timer by parser.storing(
    "-t", "--timer",
    help = "Enable the regen of the maze after the value specified in seconds"
  ) { toLong() }.default(-1)
  val blanking by parser.flagging("--blank", help = "Enables blanking before redraw").default(false)
  val cellSize by parser.storing("-c", "--cellsize", help = "The size inside a maze cell") { toInt() }.default(8)
  val drawMarker by parser.flagging("--draw-marker", help = "Draw start and target point in the maze.").default(false)
  val centerTarget by parser.flagging("--center-target", help = "Positions the target point in the center").default(false)
  val randomHoles by parser.flagging("--random-holes", "--horns-of-jericho", help = "Break some random holes into the maze")
    .default(false)
}
