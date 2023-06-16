package xyz.poeschl.pixelmaze

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import de.amr.graph.core.api.Edge
import de.amr.graph.core.api.UndirectedEdge
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
            UndirectedEdge(0, 2),
            UndirectedEdge(1, 3)
        )


        // *******
        // *0 *1 *
        // *  *  *
        // *******
        // *2 *3 *
        // *  *  *
        // ******

        val maze = Maze(Point(0, 0), Pair(6, 6), 1)

        // THEN
        maze.updateMaze(edges.stream())

        // *******
        // *     *
        // *     *
        // *  *  *
        // *  *  *
        // *  *  *
        // ******

        // VERIFY
        val pixels = maze.mazeSet
        assertThat(pixels).containsExactlyInAnyOrder(
            Pixel(Point(0, 0), Color.WHITE),
            Pixel(Point(1, 0), Color.WHITE),
            Pixel(Point(2, 0), Color.WHITE),
            Pixel(Point(3, 0), Color.WHITE),
            Pixel(Point(4, 0), Color.WHITE),
            Pixel(Point(5, 0), Color.WHITE),
            Pixel(Point(6, 0), Color.WHITE),
            Pixel(Point(0, 1), Color.WHITE),
            Pixel(Point(6, 1), Color.WHITE),
            Pixel(Point(0, 2), Color.WHITE),
            Pixel(Point(6, 2), Color.WHITE),
            Pixel(Point(0, 3), Color.WHITE),
            Pixel(Point(3, 3), Color.WHITE),
            Pixel(Point(6, 3), Color.WHITE),
            Pixel(Point(0, 4), Color.WHITE),
            Pixel(Point(3, 4), Color.WHITE),
            Pixel(Point(6, 4), Color.WHITE),
            Pixel(Point(0, 5), Color.WHITE),
            Pixel(Point(3, 5), Color.WHITE),
            Pixel(Point(6, 5), Color.WHITE),
            Pixel(Point(0, 6), Color.WHITE),
            Pixel(Point(1, 6), Color.WHITE),
            Pixel(Point(2, 6), Color.WHITE),
            Pixel(Point(3, 6), Color.WHITE),
            Pixel(Point(4, 6), Color.WHITE),
            Pixel(Point(5, 6), Color.WHITE)
        )
    }

    @Test
    internal fun testDivisorSizeMazeDrawing() {
        // WHEN
        val edges: Set<Edge> = setOf(
            UndirectedEdge(0, 1),
            UndirectedEdge(1, 2),
            UndirectedEdge(0, 4),
            UndirectedEdge(1, 5),
            UndirectedEdge(5, 6),
            UndirectedEdge(3, 7)
        )

        // *************
        // *0 *1 *2 *3 *
        // *  *  *  *  *
        // *************
        // *4 *5 *6 *7 *
        // *  *  *  *  *
        // ************

        val maze = Maze(Point(0, 0), Pair(12, 6), 1)

        // THEN
        maze.updateMaze(edges.stream())

        // *************
        // *        *  *
        // *        *  *
        // *  *  ****  *
        // *  *     *  *
        // *  *     *  *
        // ************

        // VERIFY
        val pixels = maze.mazeSet
        assertThat(pixels).containsExactlyInAnyOrder(
            Pixel(Point(0, 0), Color.WHITE),
            Pixel(Point(1, 0), Color.WHITE),
            Pixel(Point(2, 0), Color.WHITE),
            Pixel(Point(3, 0), Color.WHITE),
            Pixel(Point(4, 0), Color.WHITE),
            Pixel(Point(5, 0), Color.WHITE),
            Pixel(Point(6, 0), Color.WHITE),
            Pixel(Point(7, 0), Color.WHITE),
            Pixel(Point(8, 0), Color.WHITE),
            Pixel(Point(9, 0), Color.WHITE),
            Pixel(Point(10, 0), Color.WHITE),
            Pixel(Point(11, 0), Color.WHITE),
            Pixel(Point(12, 0), Color.WHITE),
            Pixel(Point(0, 1), Color.WHITE),
            Pixel(Point(9, 1), Color.WHITE),
            Pixel(Point(12, 1), Color.WHITE),
            Pixel(Point(0, 2), Color.WHITE),
            Pixel(Point(9, 2), Color.WHITE),
            Pixel(Point(12, 2), Color.WHITE),
            Pixel(Point(0, 3), Color.WHITE),
            Pixel(Point(3, 3), Color.WHITE),
            Pixel(Point(6, 3), Color.WHITE),
            Pixel(Point(7, 3), Color.WHITE),
            Pixel(Point(8, 3), Color.WHITE),
            Pixel(Point(9, 3), Color.WHITE),
            Pixel(Point(12, 3), Color.WHITE),
            Pixel(Point(0, 4), Color.WHITE),
            Pixel(Point(3, 4), Color.WHITE),
            Pixel(Point(9, 4), Color.WHITE),
            Pixel(Point(12, 4), Color.WHITE),
            Pixel(Point(0, 5), Color.WHITE),
            Pixel(Point(3, 5), Color.WHITE),
            Pixel(Point(9, 5), Color.WHITE),
            Pixel(Point(12, 5), Color.WHITE),
            Pixel(Point(0, 6), Color.WHITE),
            Pixel(Point(1, 6), Color.WHITE),
            Pixel(Point(2, 6), Color.WHITE),
            Pixel(Point(3, 6), Color.WHITE),
            Pixel(Point(4, 6), Color.WHITE),
            Pixel(Point(5, 6), Color.WHITE),
            Pixel(Point(6, 6), Color.WHITE),
            Pixel(Point(7, 6), Color.WHITE),
            Pixel(Point(8, 6), Color.WHITE),
            Pixel(Point(9, 6), Color.WHITE),
            Pixel(Point(10, 6), Color.WHITE),
            Pixel(Point(11, 6), Color.WHITE)
        )
    }
}
