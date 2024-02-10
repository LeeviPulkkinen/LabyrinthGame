package src

import src.Constants.{LAVA, mainWidth}
import java.awt.Color
import scala.collection.mutable.HashMap
import scala.swing.{BorderPanel, BoxPanel, Button, Dimension, Graphics2D, Label, Orientation, Slider}
import scala.swing.Swing.{EmptyBorder, LineBorder}
import scala.swing.event.ButtonClicked


// object for the start menu
object startScreen extends BorderPanel {

  // start game
  GUI.gameOver = false

  val sizes = new Dimension(mainWidth / 2, 50)

  val sizeSlider = new Slider {

    background = Color.lightGray

    minimumSize = sizes
    maximumSize = sizes
    preferredSize = sizes

    max = 20
    min = 3
    value = min

    majorTickSpacing = 1
    paintTicks = true

    val values = HashMap[Int, Label]()
    for (i <- min to max) {
      values += i -> new Label(i.toString)
    }

    labels = values
    paintLabels = true

  }

  val layerSlider = new Slider {

    background = Color.lightGray

    minimumSize = sizes
    maximumSize = sizes
    preferredSize = sizes

    max = 5
    min = 1
    value = min

    majorTickSpacing = 1
    paintTicks = true

    val values = HashMap[Int, Label]()
    for (i <- min to max) {
      values += i -> new Label(i.toString)
    }

    labels = values
    paintLabels = true
  }

  val start = new Button {

    minimumSize = sizes
    text = "Start Game"
  }

  listenTo(sizeSlider, layerSlider, start)

  reactions += {
    case click: ButtonClicked => {
      if (click.source == start)
        GUI.startGame(sizeSlider.value, layerSlider.value)
    }
  }

  val rules = new BoxPanel(Orientation.Vertical) {


    border = LineBorder(Color.BLACK)

    contents += new Label("Rules and instructions:") {
      border = EmptyBorder(5)
    }
    contents += new Label("Move the rat with arrow keys across the maze and try to find the cheese") {
      border = EmptyBorder(5)
    }
    contents += new Label("Multiple layers can be selected and ladders can be used to move between them by pressing space.") {
      border = EmptyBorder(5)
    }
    contents += new Label("Setting the maze size and layer amounts to high numbers makes the maze very difficult! ") {
      border = EmptyBorder(5)
    }

  }

  val sliders = new BoxPanel(Orientation.Vertical) {

    contents += new BorderPanel {

      override def paintComponent(g: Graphics2D): Unit = {
        g.drawImage(LAVA, 0, 0, null)
      }

      border = EmptyBorder(50)
      add(new Label("Choose the size of the maze"), BorderPanel.Position.North)
      add(sizeSlider, BorderPanel.Position.Center)
    }

    contents += new BorderPanel {

      override def paintComponent(g: Graphics2D): Unit = {
        g.drawImage(LAVA, 0, 0, null)
      }

      border = EmptyBorder(50)
      add(new Label("Choose the amount of layers"), BorderPanel.Position.North)
      add(layerSlider, BorderPanel.Position.Center)
    }
  }


  add(rules, BorderPanel.Position.North)
  add(sliders, BorderPanel.Position.Center)
  add(start, BorderPanel.Position.South)
}