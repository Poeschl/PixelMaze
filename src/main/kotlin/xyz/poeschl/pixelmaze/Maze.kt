package xyz.poeschl.pixelmaze

import de.amr.graph.core.api.Edge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.poeschl.kixelflut.*
import java.awt.Color
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min

class Maze(
    private val origin: Point,
    private val mazeSize: Pair<Int, Int>,
    private val pathSize: Int
) {

    companion object {
        private const val DEBUG = false
        private const val BORDER_WIDTH = 1
        private val WALL_COLOR = Color.WHITE
    }

    private val cellSize = pathSize + BORDER_WIDTH * 2
    val widthInCells = mazeSize.first / cellSize
    val heightInCells = mazeSize.second / cellSize

    private var shadowMatrix = PixelMatrix(mazeSize.first, mazeSize.second)
    private var mazeSet = setOf<Pixel>()

    fun updateMaze(edges: Stream<Edge>) {
        createGrid()
        edges.parallel().forEach { createEdges(it) }
        mazeSet = shadowMatrix.getPixelSet().map { Pixel(it.point.plus(origin), it.color) }.toSet()
        shadowMatrix = PixelMatrix(mazeSize.first, mazeSize.second)
    }

    fun draw(drawInterface: Pixelflut) {
        drawInterface.drawPixels(mazeSet)
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

    private fun createEdges(edge: Edge) {
        val from = getOriginPointOfCell(min(edge.either(), edge.other()))
        val to = getOriginPointOfCell(max(edge.either(), edge.other()))

        when {
            from.x == to.x && from.y != to.y -> removeVerticalBorderToBottomOf(from)
            from.y == to.y && from.x != to.x -> removeHorizontalBorderToRightOf(from)
        }
    }

    private fun removeVerticalBorderToBottomOf(from: Point) {
        for (yOffset in 0..pathSize) {
            val wallPoint = from.plus(Point(cellSize, BORDER_WIDTH + yOffset))
            shadowMatrix.remove(wallPoint)
            if (DEBUG) {
                shadowMatrix.insert(Pixel(wallPoint, Color.RED))
            }
        }
    }

    private fun removeHorizontalBorderToRightOf(from: Point) {
        for (xOffset in 0..pathSize) {
            val wallPoint = from.plus(Point(BORDER_WIDTH + xOffset, cellSize))
            shadowMatrix.remove(wallPoint)
            if (DEBUG) {
                shadowMatrix.insert(Pixel(wallPoint, Color.RED))
            }
        }
    }

    private fun getOriginPointOfCell(index: Int): Point {
        val y = (index / heightInCells) * cellSize
        val x = (index % widthInCells) * cellSize
        return Point(x, y)
    }
}
