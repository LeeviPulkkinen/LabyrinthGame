package src

// a simple vector class for easier coordinate usage.
case class Vector3D(x: Int, y: Int, z: Int) {

  def +(other: Vector3D): Vector3D = Vector3D(this.x + other.x, this.y + other.y, this.z + other.z)

  // vector can be multiplied by an integer
  def *(int: Int): Vector3D = Vector3D(this.x * int, this.y * int, this.z * int)

  // a method for reversing a 3D vector
  def reverse: Vector3D = this * -1

}
