package src

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object Constants {

  // size of the window
  val mainWidth = 1000
  val mainHeight = 650

  // size of the maze itself
  val width = 800
  val height = mainHeight

  // these values are placeholders, and will be updated on user input
  var cellWidth = 10
  var cellHeight = 10

  // Cell coordinate origin is located in the top left corner.
  // NORTHEAST and SUOTHWEST represent moving up and down layers.
  val NORTH = Vector3D(0, -1, 0)
  val NORTHEAST = Vector3D(0, 0, 1)
  val EAST = Vector3D(1, 0, 0)
  val SOUTH = Vector3D(0, 1, 0)
  val SOUTHWEST = Vector3D(0, 0, -1)
  val WEST = Vector3D(-1, 0, 0)

  val DIRECTIONS = Array(NORTH, NORTHEAST, EAST, SOUTH, SOUTHWEST, WEST)

  // All different pictures

  val LAVA = ImageIO.read(new File("pictures/lava.png")).getScaledInstance(mainWidth, mainHeight, Image.SCALE_DEFAULT)

  val VERTICAL = (ImageIO.read(new File("pictures/straightU.png")))
  val HORIZONTAL = (ImageIO.read(new File("pictures/straightH.png")))
  val RIGHTUP = (ImageIO.read(new File("pictures/UpRight.png")))
  val RIGHTDOWN = (ImageIO.read(new File("pictures/RightDown.png")))
  val LEFTUP = (ImageIO.read(new File("pictures/LeftUp.png")))
  val LEFTDOWN = (ImageIO.read(new File("pictures/DownLeft.png")))
  val CROSS = (ImageIO.read(new File("pictures/cross.png")))
  val HORDOWN = (ImageIO.read(new File("pictures/HorDown.png")))
  val HORUP = (ImageIO.read(new File("pictures/HorUP.png")))
  val VERLEFT = (ImageIO.read(new File("pictures/VerLeft.png")))
  val VERRIGHT = (ImageIO.read(new File("pictures/VerRight.png")))
  val RIGHTEND = (ImageIO.read(new File("pictures/RightEnd.png")))
  val LEFTEND = (ImageIO.read(new File("pictures/LeftEnd.png")))
  val UPEND = (ImageIO.read(new File("pictures/UpEnd.png")))
  val DOWNEND = (ImageIO.read(new File("pictures/DownEnd.png")))
  val MOUSERIGHT = (ImageIO.read(new File("pictures/mouseRight.png")))
  val MOUSELEFT = (ImageIO.read(new File("pictures/mouseLeft.png")))
  val CHEESE = (ImageIO.read(new File("pictures/Cheese.png")))
  val LADDERUP = (ImageIO.read(new File("pictures/LadderUp.png")))
  val LADDERDOWN = (ImageIO.read(new File("pictures/LadderDown.png")))

  var images = Array(VERTICAL, HORIZONTAL, RIGHTUP, RIGHTDOWN, LEFTUP, LEFTDOWN, CROSS, HORDOWN, HORUP, VERLEFT, VERRIGHT, RIGHTEND, LEFTEND, UPEND, DOWNEND, MOUSERIGHT, MOUSELEFT, CHEESE, LADDERUP, LADDERDOWN)

  // image map is used for the possibility to rezise images while ensuring they can be accessed by their original name.
  var imageMap: Map[BufferedImage, Image] = images.map(image => image -> image).toMap


  // Helper method for resizing images
  def resizeImages(images: Array[BufferedImage]) = imageMap = imageMap.map(image => (image._1 -> image._2.getScaledInstance(cellWidth, cellHeight, Image.SCALE_DEFAULT)))
}
