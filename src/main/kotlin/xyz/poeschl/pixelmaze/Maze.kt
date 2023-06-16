package xyz.poeschl.pixelmaze

import de.amr.graph.core.api.Edge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import xyz.poeschl.kixelflut.Pixel
import xyz.poeschl.kixelflut.Point
import xyz.poeschl.kixelflut.createHorizontalPixels
import xyz.poeschl.kixelflut.createVerticalPixels
import java.awt.Color
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Maze(
  private val origin: Point,
  private val mazeSize: Pair<Int, Int>,
  private val pathSize: Int,
  private val drawMarker: Boolean = false,
  private val centeredTarget: Boolean = false,
  private val randomHoles: Boolean = false
) {

  companion object {
    private val LOGGER = KotlinLogging.logger { }
    private val VISUAL_LOGGER = KotlinLogging.logger("visualDebug")
    private val DEBUG = VISUAL_LOGGER.isDebugEnabled
    private const val BORDER_WIDTH = 1
    private val WALL_COLOR = Color.WHITE
    private val START_COLOR = Color.CYAN
    private val TARGET_COLOR = Color.MAGENTA
    private const val HOLE_RANDOM_POINTS_PERCENTAGE = 0.1
  }

  private val cellSize = pathSize + BORDER_WIDTH * 2
  val widthInCells = mazeSize.first / cellSize
  val heightInCells = mazeSize.second / cellSize

  private var shadowMatrix = PixelMatrix(mazeSize.first, mazeSize.second)
  var mazeSet = setOf<Pixel>()

  init {
    if (drawMarker) {
      LOGGER.info { "Drawing start and target marker" }
      if (centeredTarget) {
        LOGGER.info { "Draw target at the center" }
      }
    }
  }

  fun updateMaze(edges: Stream<Edge>) {
    createGrid()
    edges.parallel().forEach { createEdges(it) }
    if (drawMarker) {
      createMarkers()
    }
    if (randomHoles) {
      generateRandomHoles()
    }
    mazeSet = shadowMatrix.getPixelSet().map { Pixel(it.point.plus(origin), it.color) }.toSet()
    shadowMatrix = PixelMatrix(mazeSize.first, mazeSize.second)
  }

  fun clear() {
    mazeSet = setOf()
  }

  private fun createGrid() {
    runBlocking {
      launch {
        IntStream.rangeClosed(0, widthInCells)
          .mapToObj { x ->
            createVerticalPixels(
              Point(x * cellSize, 0),
              heightInCells * cellSize,
              WALL_COLOR
            )
          }
          .flatMap { it.parallelStream() }
          .forEach { shadowMatrix.insert(it) }
      }
      launch {
        IntStream.rangeClosed(0, heightInCells)
          .mapToObj { y ->
            createHorizontalPixels(
              Point(0, y * cellSize),
              widthInCells * cellSize,
              WALL_COLOR
            )
          }
          .flatMap { it.parallelStream() }
          .forEach { shadowMatrix.insert(it) }
      }
    }
  }

  private fun generateRandomHoles() {
    val randomPoints = heightInCells * widthInCells * HOLE_RANDOM_POINTS_PERCENTAGE
    (0..randomPoints.toInt()).forEach { _ ->
      val point = Point(
        Random.nextInt(widthInCells - 1) * cellSize,
        Random.nextInt(heightInCells - 1) * cellSize
      )

      when (Random.nextBoolean()) {
        true -> removeHorizontalBorderToRightOf(point, Color.BLUE)
        false -> removeVerticalBorderToBottomOf(point, Color.BLUE)
      }
    }
  }

  private fun createEdges(edge: Edge) {
    val from = getOriginPointOfCell(min(edge.either(), edge.other()))
    val to = getOriginPointOfCell(max(edge.either(), edge.other()))

    when {
      from.x == to.x && from.y != to.y -> removeHorizontalBorderToRightOf(from)
      from.y == to.y && from.x != to.x -> removeVerticalBorderToBottomOf(from)
    }
  }

  private fun removeVerticalBorderToBottomOf(from: Point, debugColor: Color = Color.RED) {
    for (yOffset in 0..pathSize) {
      val wallPoint = from.plus(Point(cellSize, BORDER_WIDTH + yOffset))
      shadowMatrix.remove(wallPoint)
      if (DEBUG) {
        shadowMatrix.insert(Pixel(wallPoint, debugColor))
      }
    }
  }

  private fun removeHorizontalBorderToRightOf(from: Point, debugColor: Color = Color.RED) {
    for (xOffset in 0..pathSize) {
      val wallPoint = from.plus(Point(BORDER_WIDTH + xOffset, cellSize))
      shadowMatrix.remove(wallPoint)
      if (DEBUG) {
        shadowMatrix.insert(Pixel(wallPoint, debugColor))
      }
    }
  }

  private fun createMarkers() {
    runBlocking {
      launch {
        val start = Point(0, 0)
        createPixelCellMarker(start, START_COLOR).forEach { shadowMatrix.insert(it) }
      }
      launch {
        val target = if (centeredTarget) {
          Point(widthInCells / 2, heightInCells / 2)
        } else {
          Point(widthInCells - 1, heightInCells - 1)
        }
        createPixelCellMarker(target, TARGET_COLOR).forEach { shadowMatrix.insert(it) }
      }
    }
  }

  private fun createPixelCellMarker(point: Point, color: Color): Stream<Pixel> {
    val cellInsideOrigin = Point(point.x * cellSize + 1, point.y * cellSize + 1)
    return IntStream.rangeClosed(cellInsideOrigin.x, cellInsideOrigin.x + pathSize)
      .mapToObj { x ->
        IntStream.rangeClosed(cellInsideOrigin.y, cellInsideOrigin.y + pathSize)
          .mapToObj { y -> Pixel(Point(x, y), color) }.toList()
      }
      .flatMap { it.stream() }
  }

  private fun getOriginPointOfCell(index: Int): Point {
    val y = (index / widthInCells) * cellSize
    val x = (index % widthInCells) * cellSize
    return Point(x, y)
  }
}
