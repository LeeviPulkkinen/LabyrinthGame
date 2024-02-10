package src

import Constants._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.Stack


class Game(private val mazeSize: Int, private val amountOfLayers: Int) {

  import scala.util.Random

  val random = Random

  // array for storing the layers of the maze
  val layers = new Array[Layer](amountOfLayers)

  // create and initialize layers
  for (i <- 0 until amountOfLayers) {
    layers(i) = new Layer(mazeSize, mazeSize, i)
  }

  // create a rat
  val rat = new Rat(Vector3D(mazeSize / 2, mazeSize / 2, 0))

  private var currentL = layers(0)
  private var currentLNum = 0

  def changeCurrentLayer(layerNum: Int) = {
    if ((layerNum > 0) && (layerNum <= amountOfLayers)) this.currentL = layers(layerNum)
    this.currentLNum = layerNum
  }

  def currentLayer = this.currentL

  def currenLayerNum = currentLNum

  def cells = layers.map(_.getCells)

  def layersNum = this.amountOfLayers

  def size = this.mazeSize

  //attempts to find a cell in the maze, if cell is not found returns a wall
  def findCell(v: Vector3D) = {
    if (v.x < mazeSize && v.y < mazeSize && v.z < amountOfLayers && v.x >= 0 && v.y >= 0 && v.z >= 0) {
      this.cells(v.z)(v.y)(v.x)
    } else Wall
  }

  def findNeighbour(cell: Cell, direction: Vector3D) = {
    this.findCell(cell.location + direction)
  }

  // all neighbours of a cell, strored in a map by direction
  def allNeighbours(cell: Cell) = {
    var neighbours = Map[Cell, Vector3D]()
    for (direction <- DIRECTIONS) {
      val neighbour = this.findNeighbour(cell, direction)
      if (!neighbour.isWall)
        neighbours = neighbours + (neighbour -> direction)
    }
    neighbours
  }

  // returns true if a connection can be added, false if not
  def canAddConnection(cell1: Cell, cell2: Cell): Boolean = {
    this.allNeighbours(cell1).contains(cell2)
  }

  def addConnection(cell1: Cell, cell2: Cell): Boolean = {
    if (canAddConnection(cell1, cell2)) {
      val direction: Vector3D = this.allNeighbours(cell1)(cell2)
      cell1.addAccess(direction)
      cell2.addAccess(direction.reverse)
      true
    } else false
  }

  def ratCell(rat: Rat) = this.findCell(rat.location)

  def moveRat(direction: Vector3D, rat: Rat): Boolean = {
    if (this.ratCell(rat).canMove(direction)) {
      rat.move(direction)
      true
    } else false
  }

  def neighbourDirection(cell: Cell, neighbour: Cell) = {
    val neigbours = allNeighbours(cell)
    if (neigbours.contains(neighbour)) {
      neigbours(neighbour)
    } else Vector3D(-1, -1, -1)
  }


  /*

  recursive backtracer algorithm

  This algorithm creates the maze structure. The algorithm starts at cell 0,0,0 and randomly moves to a neighbouring cell. A connection is added between the cells.
  Cells that have been visited are marked and they cannot be visited again. Once the algorithm moves to a cell, it is pushed
  onto the stack. Once no non-visited neighbours are available, the algorithm begins to backtract by popping cells
  from the stack, until a cell with non visited neighbours is found. Once the stack is empty, all the cells have been visited
  and a perfect maze has been created.


  * */
  def createMaze() = {


    val stack = Stack[Cell]()

    // init the algorithm
    var currentCell = this.findCell(Vector3D(0, 0, 0))
    currentCell.setVisited(true)
    stack.push(currentCell)
    var cheeseAdded = false

    while (stack.nonEmpty) {

      // non visited neighbours
      val nvn = allNeighbours(currentCell).keys.filter(!_.cellVisited).toBuffer

      // This assures that there can't be a cell that has ladders up and down
      if (currentCell.canMove(NORTHEAST) && nvn.contains(this.findNeighbour(currentCell, SOUTHWEST)))
        nvn -= this.findNeighbour(currentCell, SOUTHWEST)
      else if (currentCell.canMove(SOUTHWEST) && nvn.contains(this.findNeighbour(currentCell, NORTHEAST)))
        nvn -= this.findNeighbour(currentCell, NORTHEAST)

      if (nvn.nonEmpty) {
        val nexCell = nvn(random.nextInt(nvn.length))
        this.addConnection(currentCell, nexCell)
        currentCell = nexCell
        currentCell.setVisited(true)
        stack.push(currentCell)

        // if no suitable neighbours are found, backtrack
      } else {
        val cell = stack.pop()

        // avoid adding cheese to a cell that has a ladder
        if (!cheeseAdded && !cell.canMove(NORTHEAST, SOUTHWEST)) {
          cell.toggleCheese()
          cheeseAdded = true
        }
        if (stack.nonEmpty)
          currentCell = stack.top
      }
    }
  }

  /*
  *  Simple maze solving algorithm
  *
  * This algorithm solves the maze by moving the rat and following the left wall. Moving between layers has been mapped
  * to 2 dimensions and they are in the following order NORTH, NORTHEAST, EAST, SOUTH, SOUTHWEST, WEST.
  * When there is an intercetion, the rat always chooses the left most path, according to it's current direction.
  *
  * This algorithm is not the one used in the final version of the project, but if one wants visualize the pathfinding algorithm, this function can be called
  * instead of fastSolveMaze in row 232 of GUI.
  *
  * */

  // this is used to prevent triggering the maze solving algorithm again while it's running
  private var solving = false

  /*

   This algorithm solves the maze by moving the rat and following the left wall. Moving between layers has been mapped
   to 2 dimensions and they are in the following order NORTH, NORTHEAST, EAST, SOUTH, SOUTHWEST, WEST.
   When there is an intercetion, the rat always chooses the left most path, according to it's current direction.

   The algorithm keeps track how many times different cells are visited, and a path to the cheese can be formed
   by moving the rat to cells that have been only visited once.

  */


  def fastSolveMaze() = {

    GUI.gameOver = true

    if (!this.solving) {

      this.solving = true
      GUI.refresh()


      Future {


        var index = 1

        // current direction is initialized to NORTH
        var currentDirection = NORTH
        var currentCell = ratCell(rat)

        while (!currentCell.hasCheese) {

          // directions are iterated while the rat cant move, starting from the "leftmost" one
          // index starts from one so the rat doesnt immediately go backwards
          val newDirection = DIRECTIONS(((DIRECTIONS.indexOf(currentDirection.reverse) + index) % 6))

          if (currentCell.canMove(newDirection)) {

            currentCell = findNeighbour(currentCell, newDirection)

            // if the rat can move to this direction, update current direction and set index to one.

            /*
            *
            * The cell visited count can't be simply incremented when a cell is visited, since the is some special cells that are visited multiple times (intersections)
            *
            * */


            // the cell with cheese should always have count 1
            if (currentCell.hasCheese)
              currentCell.setVisitedCount(1)

            // dead end, increase visit count to 2 so the mouse never goes here
            else if (currentCell.availableDirections.length == 1) {
              currentCell.addVisit()
              currentCell.addVisit()

              // This models the case where an intersection is deemed to lead to deadends, and therefore it should not be moved to by the rat
            } else if (currentCell.cellVisitedCount == 1 &&
                this.allNeighbours(currentCell).count(part => part._1.cellVisitedCount == 2 &&
                  currentCell.canMove(part._2)) == (currentCell.availableDirections.length - 1))
              currentCell.addVisit()

            // When arriving at an intersection for the first time, visited count sgould be increased.
            else if (currentCell.cellVisitedCount == 0 && currentCell.availableDirections.length > 2)
              currentCell.addVisit()

            // this models a cell that is in a corridor and it's count should always be incremented
            else if (currentCell.availableDirections.length == 2)
              currentCell.addVisit()

            currentDirection = newDirection
            index = 1

          } else {

            // if the rat can't move, continue iterating

            index += 1
          }
        }


        currentCell = ratCell(rat)
        var previousCell = currentCell

        // The path to the cheese has now been determined, time to move the rat.

        while (!ratCell(rat).hasCheese) {

          // next direction is determined in the following way

          // take the neighbours of the current cell and filter
          val nextDir: Vector3D = allNeighbours(currentCell).filter(part =>

            // cell is visited once
            part._1.cellVisitedCount == 1 &&

              // cell is different than the last cell (so the rat won't go back and forth)
              part._1 != previousCell &&

              // rat can move to this cell
              currentCell.canMove(neighbourDirection(currentCell, part._1))).values.head

          moveRat(nextDir, rat)

          previousCell = currentCell

          currentCell = ratCell(rat)

          GUI.refresh()

        }

        this.solving = false
      }
    }

  }






  /*
  this function saves the maze layout to a file using symbols.
  # => wall
  " " => space that the rat can move to
  CH => cheese
  UP => ladder up
  DW => ladder down
  */

  def saveMaze() = {

    import java.io._

    val pw = new PrintWriter(new File("saveFile.txt"))

    for (layer <- this.layers) {
      pw.write("#" * (layer.rows * 4 + 1))
      pw.write("\n")
      for (row <- layer.getCells) {
        pw.write("#")
        for (cell <- row) {
          var first = "  "
          if (cell.canMove(NORTHEAST))
            first = "UP"
          else if (cell.canMove(SOUTHWEST))
            first = "DW"
          else if (cell.hasCheese)
            first = "CH"

          var second = "##"
          if (cell.canMove(EAST))
            second = "  "

          pw.write(first + second)
        }
        pw.write("\n#")
        for (cell <- row) {
          var first = "##"
          if (cell.canMove(SOUTH))
            first = "  "
          pw.write(first + "##")
        }
        pw.write("\n")
      }

    }
    pw.close()
  }

}


