package xyz.poeschl.pixelmaze

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import de.amr.graph.core.api.Edge
import de.amr.graph.core.api.UndirectedEdge
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import xyz.poeschl.kixelflut.Pixel
import xyz.poeschl.kixelflut.Point
import java.awt.Color

class MazeTest {

    @Test
    internal fun testBasicMazeDrawing() {
        // WHEN
        val edges: Set<Edge> = setOf(
            UndirectedEdge(0, 1),
            UndirectedEdge(0, 2)
        )

        val maze = Maze(Point(0, 0), Pair(3, 3), 1)

        // THEN
        maze.updateMaze(edges.stream())

        // VERIFY
        val pixels = maze.mazeSet
        assertThat(pixels).containsExactlyInAnyOrder(
            Pixel(Point(0, 0), Color.WHITE),
            Pixel(Point(1, 0), Color.WHITE),
            Pixel(Point(2, 0), Color.WHITE),
            Pixel(Point(3, 0), Color.WHITE),
            Pixel(Point(0, 1), Color.WHITE),
            Pixel(Point(3, 1), Color.WHITE),
            Pixel(Point(0, 2), Color.WHITE),
            Pixel(Point(3, 2), Color.WHITE),
            Pixel(Point(0, 3), Color.WHITE),
        )
    }

    @Disabled("See https://github.com/Poeschl/PixelMaze/issues/2")
    @Test
    internal fun testDivisorSizeMazeDrawing() {
        // WHEN
        val edges: Set<Edge> = setOf(
            UndirectedEdge(0, 1),
            UndirectedEdge(1, 2),
            UndirectedEdge(0, 3),
            UndirectedEdge(3, 4),
            UndirectedEdge(4, 5)
        )

        val maze = Maze(Point(0, 0), Pair(18, 6), 1)

        // THEN
        maze.updateMaze(edges.stream())

        // VERIFY
        val pixels = maze.mazeSet
        //TODO: Needs to be calculated
        assertThat(pixels).hasSize(0)
    }
}
