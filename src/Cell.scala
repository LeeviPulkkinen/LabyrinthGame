package src

import src.Constants._

import scala.swing.Image

// A cell models a single square in the labyrinth that can be accessed.

class Cell(private val loc: Vector3D) {

  // Available directions to move to are stored here.
  // All directions are unavailable by default but thay are modified when the maze is created.

  private var directions = Map(
    NORTH -> false,
    NORTHEAST -> false,
    EAST -> false,
    SOUTH -> false,
    SOUTHWEST -> false,
    WEST -> false,
  )

  private var visited = false

  private var cheese = false

  private var count = 0

  def location: Vector3D = this.loc

  def isWall = false

  def availableDirections: Vector[Vector3D] = this.directions.filter(_._2).keys.toVector


  def canMove(direction: Vector3D*): Boolean = direction.forall(availableDirections.contains(_))

  // Toggles the possibility to move to a certain direction.
  def addAccess(direction: Vector3D) = if (DIRECTIONS.contains(direction)) this.directions = this.directions + (direction -> true)


  // methods for tracking if a cell has been visited (used by the maze creation algorithm)
  def cellVisited: Boolean = this.visited

  // methods for tracking how many times a cell has been visited (used by the maze solving algorithm)
  def cellVisitedCount: Int = this.count

  def setVisitedCount(set: Int) = this.count = set

  def addVisit() = this.count += 1

  def setVisited(bool: Boolean) = this.visited = bool

  // returns an image that corresponds to available directions
  def getImage: Image = {
    if (this.canMove(NORTH, EAST, SOUTH, WEST))
      Constants.imageMap(CROSS)
    else if (this.canMove(NORTH, EAST, SOUTH))
      Constants.imageMap(VERRIGHT)
    else if (this.canMove(NORTH, EAST, WEST))
      Constants.imageMap(HORUP)
    else if (this.canMove(NORTH, SOUTH, WEST))
      Constants.imageMap(VERLEFT)
    else if (this.canMove(EAST, SOUTH, WEST))
      Constants.imageMap(HORDOWN)
    else if (this.canMove(NORTH, SOUTH))
      Constants.imageMap(VERTICAL)
    else if (this.canMove(WEST, EAST))
      Constants.imageMap(HORIZONTAL)
    else if (this.canMove(NORTH, EAST))
      Constants.imageMap(RIGHTUP)
    else if (this.canMove(SOUTH, EAST))
      Constants.imageMap(RIGHTDOWN)
    else if (this.canMove(WEST, SOUTH))
      Constants.imageMap(LEFTDOWN)
    else if (this.canMove(WEST, NORTH))
      Constants.imageMap(LEFTUP)
    else if (this.canMove(NORTH))
      Constants.imageMap(DOWNEND)
    else if (this.canMove(EAST))
      Constants.imageMap(LEFTEND)
    else if (this.canMove(SOUTH))
      Constants.imageMap(UPEND)
    else if (this.canMove(WEST))
      Constants.imageMap(RIGHTEND)
    else
      Constants.imageMap(DOWNEND)
  }

  def hasCheese = this.cheese

  def toggleCheese() = this.cheese = true


  override def toString = s"(Cell: ${location.x}, ${location.y}, ${location.z}, count ${cellVisitedCount})"

}

// a wall represents a cell that cant be accessed
// a walls location is not relevant, a wall is just used for practicality when trying to access cells that dont exist

object Wall extends Cell(Vector3D(-1, -1, -1)) {

  override def isWall = true

  override def availableDirections: Vector[Vector3D] = Vector()

  override def toString = "WALL"
}
