package src

import scala.collection.mutable.Buffer
import scala.swing.Image

// models a single layer of the maze

class Layer(private val height: Int, private val width: Int, private val lvl: Int) {

  private val cells = Buffer[Vector[Cell]]()

  initLayer()

  // Creates all the required cells of the grid, outer cells are created as walls

  def initLayer() = {

    for (i <- 0 until this.height){
      val row = Buffer[Cell]()
      for (j <- 0 until this.width){
          row += new Cell(Vector3D(j, i, this.level))

      }
      cells += row.toVector
    }
  }

  def getCells = this.cells.toVector // returns all the cells of the layer in original format

  def getCellImages: Vector[Vector[Image]] = this.getCells.map(_.map(_.getImage)) // returns a 2D vector containing images for each cell

  def rows = this.height

  def colls = this.width

  def level = this.lvl

}
