package src
import Constants._


// A rat that can be moved in the maze
class Rat(private var loc: Vector3D){

  // move the rat if the direction is valid
  def move(direction: Vector3D): Unit ={
    if (DIRECTIONS.contains(direction)){
      this.setLocation(direction)
    }
  }

  private def setLocation(direction: Vector3D): Unit ={
    this.loc += direction
  }

  def location = this.loc

  override def toString: String = s"Rat at: ${this.location}"
}

