package io.github.poeschl.pixelflutmaze.labyrinth

import de.amr.graph.core.api.Edge
import io.github.poeschl.pixelflutmaze.shared.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.util.stream.IntStream
import java.util.stream.Stream

class Maze(private val origin: Point, val mazeSize: Pair<Int, Int>) {

    companion object {
        private const val DEBUG = false
        private const val PATH_SIZE = 16
        private const val BORDER_WIDTH = 1
        private val WALL_COLOR = Color.WHITE

        const val CELL_SIZE = PATH_SIZE + BORDER_WIDTH * 2
    }

    val widthInCells = mazeSize.first / CELL_SIZE
    val heightInCells = mazeSize.second / CELL_SIZE

    private var shadowMatrix = PixelMatrix(mazeSize.first, mazeSize.second)
    private var mazeSet = setOf<Pixel>()

    fun updateMaze(edges: Stream<Edge>) {
        createGrid()
        edges.parallel().forEach { createEdges(it) }
        mazeSet = shadowMatrix.getPixelSet().map { Pixel(it.point.plus(origin), it.color) }.toSet()
        shadowMatrix = PixelMatrix(widthInCells, heightInCells)
    }

    fun draw(drawInterface: PixelFlutInterface) {
        drawInterface.paintPixelSet(mazeSet)
    }

    fun clear() {
        mazeSet = setOf()
    }

    private fun createGrid() {
        runBlocking {
            launch {
                IntStream.rangeClosed(0, widthInCells)
                    .parallel()
                    .mapToObj { x -> createVerticalPixels(Point(x * CELL_SIZE, 0), mazeSize.second, WALL_COLOR) }
                    .flatMap { it.parallelStream() }
                    .forEach { shadowMatrix.insert(it) }
            }
            launch {
                IntStream.rangeClosed(0, heightInCells)
                    .parallel()
                    .mapToObj { y -> createHorizontalPixels(Point(0, y * CELL_SIZE), mazeSize.first, WALL_COLOR) }
                    .flatMap { it.parallelStream() }
                    .forEach { shadowMatrix.insert(it) }
            }
        }
    }

    private fun createEdges(edge: Edge) {
        val from = getOriginPointOfCell(Math.min(edge.either(), edge.other()))
        val to = getOriginPointOfCell(Math.max(edge.either(), edge.other()))

        when {
            from.x == to.x && from.y != to.y -> drawVerticalPathToBottom(from)
            from.y == to.y && from.x != to.x -> removeVerticalBorderToRightOf(from)
        }
    }

    private fun removeVerticalBorderToRightOf(from: Point) {
        for (yOffset in 0..PATH_SIZE) {
            val wallPoint = from.plus(Point(CELL_SIZE, BORDER_WIDTH + yOffset))
            shadowMatrix.remove(wallPoint)
            if (DEBUG) {
                shadowMatrix.insert(Pixel(wallPoint, Color.RED))
            }
        }
    }

    private fun drawVerticalPathToBottom(from: Point) {
        for (xOffset in 0..PATH_SIZE) {
            val wallPoint = from.plus(Point(BORDER_WIDTH + xOffset, CELL_SIZE))
            shadowMatrix.remove(wallPoint)
            if (DEBUG) {
                shadowMatrix.insert(Pixel(wallPoint, Color.RED))
            }
        }
    }

    private fun getOriginPointOfCell(index: Int): Point {
        val y = (index / heightInCells) * CELL_SIZE
        val x = (index % widthInCells) * CELL_SIZE
        return Point(x, y)
    }
}
