package xyz.poeschl.pixelmaze

import de.amr.graph.core.api.Edge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.poeschl.kixelflut.*
import java.awt.Color
import java.util.stream.IntStream
import java.util.stream.Stream

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
                    //.parallel()
                    .mapToObj { x ->
                        createVerticalPixels(
                            Point(x * cellSize, 0),
                            heightInCells * (cellSize - BORDER_WIDTH),
                            WALL_COLOR
                        )
                    }
                    .flatMap { it.parallelStream() }
                    .forEach { shadowMatrix.insert(it) }
            }
            launch {
                IntStream.rangeClosed(0, heightInCells)
                    //.parallel()
                    .mapToObj { y ->
                        createHorizontalPixels(
                            Point(0, y * cellSize),
                            widthInCells * (cellSize - BORDER_WIDTH),
                            WALL_COLOR
                        )
                    }
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
        for (yOffset in 0..pathSize) {
            val wallPoint = from.plus(Point(cellSize, BORDER_WIDTH + yOffset))
            shadowMatrix.remove(wallPoint)
            if (DEBUG) {
                shadowMatrix.insert(Pixel(wallPoint, Color.RED))
            }
        }
    }

    private fun drawVerticalPathToBottom(from: Point) {
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
