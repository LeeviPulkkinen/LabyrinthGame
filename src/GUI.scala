package src

import java.awt.Color
import scala.swing._
import scala.swing.event._
import src.Constants._
import scala.swing.event.KeyPressed
import javax.swing.JOptionPane


object GUI extends SimpleSwingApplication {


  // placeholder for game, will be updated according to user input
  var game = new Game(3, 1)

  // different layer UIs are stored here
  var layers = new Array[LayerUI](game.layersNum)

  // current layer
  var layer: Option[LayerUI] = None

  // rat's image can be changed depending on direction
  var mouseImage = Constants.imageMap(MOUSELEFT)

  var gameOver = false


  // create a mainwindow
  val mainWindow = new MainFrame {

    // size of the window cant be changed
    preferredSize = new Dimension(mainWidth, mainHeight)
    maximumSize = new Dimension(mainWidth, mainHeight)
    minimumSize = new Dimension(mainWidth, mainHeight)
    resizable = false

    background = Color.BLACK

    title = "Labyrinth"

    contents = startScreen

    // center the window
    peer.setLocationRelativeTo(null)

  }

  def top = this.mainWindow

  def startGame(size: Int, layerAmount: Int) = {

    // create a new game
    this.game = new Game(size, layerAmount)

    game.createMaze()

    layers = new Array[LayerUI](game.layersNum)

    // create a layer UI for each layer of the game
    var i = 0
    for (l <- game.layers) {
      layers(i) = (new LayerUI(l.rows, l.colls, l, game.rat))
      i += 1
    }

    // the first layer is used by default
    this.layer = Some(layers(0))

    // update cell width and height. Rezise images
    Constants.cellWidth = (width - 10) / game.size
    Constants.cellHeight = (height - 30) / game.size
    Constants.resizeImages(Constants.images)

    this.mainWindow.contents = newContents

    // update mouse image
    mouseImage = Constants.imageMap(MOUSELEFT)

  }


  def newContents: BoxPanel = {

    new BoxPanel(Orientation.Horizontal) {
      contents += layer.get
      contents += Buttons

      background = Color.DARK_GRAY
    }
  }


  // a method for changing the current layer.
  def changeLayer() = {
    var direction = SOUTHWEST
    if (game.ratCell(game.rat).canMove(NORTHEAST))
      direction = NORTHEAST

    if (game.moveRat(direction, game.rat)) {
      val newLayerNum = game.currenLayerNum + direction.z

      if (newLayerNum <= game.layersNum - 1 && newLayerNum >= 0) {
        this.layer = Some(layers(game.currenLayerNum + direction.z))
        game.changeCurrentLayer(game.currenLayerNum + direction.z)
        this.mainWindow.contents = newContents
        this.mainWindow.repaint()
      }
    }

  }

  def refresh() = {

    val correctLayer = game.ratCell(game.rat).location.z

    if (game.currenLayerNum != correctLayer) {
      game.changeCurrentLayer(correctLayer)
      this.layer = Some(layers(correctLayer))
      this.mainWindow.contents = newContents
      this.mainWindow.repaint()
    }

    this.layer.get.repaint()


    // game over poppup
    if (game.ratCell(game.rat).hasCheese) {

      val answer = JOptionPane.showConfirmDialog(null, "You found the cheese! \nPlay again?", "Game Over", JOptionPane.YES_NO_OPTION)
      if (answer == 0) {
        mainWindow.contents = startScreen
        gameOver = false
        mainWindow.repaint()
      } else
        sys.exit(0)
    }
  }


}

// A layer UI is created for each layer of the maze, this class draws a layer on the screen.

class LayerUI(rows: Int, colls: Int, layer: Layer, rat: Rat) extends Component {


  listenTo(keys)

  // reactions from pressing buttons
  reactions += {

    case KeyPressed(_, Key.Up, _, _) => if (!GUI.gameOver) GUI.game.moveRat(NORTH, rat); GUI.refresh()
    case KeyPressed(_, Key.Down, _, _) => if (!GUI.gameOver) GUI.game.moveRat(SOUTH, rat); GUI.refresh()
    case KeyPressed(_, Key.Left, _, _) => if (!GUI.gameOver) GUI.game.moveRat(WEST, rat); GUI.refresh(); GUI.mouseImage = Constants.imageMap(MOUSELEFT)
    case KeyPressed(_, Key.Right, _, _) => if (!GUI.gameOver) GUI.game.moveRat(EAST, rat); GUI.refresh(); GUI.mouseImage = Constants.imageMap(MOUSERIGHT)
    case KeyPressed(_, Key.Space, _, _) => if (!GUI.gameOver) GUI.changeLayer(); GUI.refresh()


  }

  override def paintComponent(g: Graphics2D): Unit = {
    g.drawImage(LAVA, 0, 0, null)

    focusable = true
    requestFocus


    var x = 0
    var y = 0

    for (row <- layer.getCells) {
      for (cell <- row) {

        // draw the image of the current cell
        g.drawImage(cell.getImage, x, y, null)

        // draw a ladder or cheese if required
        if (cell.canMove(NORTHEAST))
          g.drawImage(Constants.imageMap(LADDERUP), x, y, null)
        else if (cell.canMove(SOUTHWEST))
          g.drawImage(Constants.imageMap(LADDERDOWN), x, y, null)

        if (cell.hasCheese)
          g.drawImage(Constants.imageMap(CHEESE), x, y, null)

        x += cellWidth
      }
      x = 0
      y += cellHeight
    }


    g.drawImage(GUI.mouseImage, rat.location.x * cellWidth, rat.location.y * cellHeight, null)

  }
}


// object for the buttons shown on the screen
object Buttons extends BoxPanel(Orientation.Vertical) {


  val buttonSize = new Dimension(mainWidth - width, mainWidth - width - 50)

  val solve = new Button {
    text = "Solve maze"

    preferredSize = buttonSize
    maximumSize = buttonSize
    minimumSize = buttonSize
  }

  val save = new Button {
    text = "Save to a file"

    preferredSize = buttonSize
    maximumSize = buttonSize
    minimumSize = buttonSize
  }

  contents += solve
  contents += save

  listenTo(solve, save)

  reactions += {
    case click: ButtonClicked => {
      val source = click.source

      if (source == solve)
        GUI.game.fastSolveMaze()
      else if (source == save)
        GUI.game.saveMaze()
    }
  }
}
