package xyz.poeschl.pixelmaze

import xyz.poeschl.kixelflut.Pixel
import xyz.poeschl.kixelflut.Point

class PixelMatrix(private val xSize: Int, private val ySize: Int) {

  private val dataArray = initDataArray()

  fun insert(pixel: Pixel) {
    val coord = pixel.point
    dataArray[coord.y][coord.x] = pixel
  }

  fun remove(point: Point) {
    dataArray[point.y][point.x] = null
  }

  fun getPixelSet(): Set<Pixel> {
    return dataArray.flatten().filter { it != null }.map { it!! }.toSet()
  }

  private fun initDataArray(): Array<Array<Pixel?>> {
    return Array<Array<Pixel?>>(ySize + 1) { Array(xSize + 1) { null } }
  }
}
