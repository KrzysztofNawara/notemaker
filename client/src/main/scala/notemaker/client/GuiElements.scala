package notemaker.client

import javafx.geometry.VPos

import scalafx.Includes._
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.{KeyCode, MouseEvent}
import scalafx.scene.layout.{Region, Pane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.scene.text._




/**
 * Created by blueeyedhush on 12/23/14.
 */
object JfxWorksheet extends Pane {
  var sequence: Seq[JfxNode] = Seq()
  content = sequence

  NodeManager.nodeListener = (n : Node) => {
    JfxWorksheet.createNode(n.x.toDouble, n.y.toDouble)
    ()
  }

  def delNode : Unit = {
    println("Deleting nodes")
    content.remove(0, sequence.length)
    sequence = sequence.filter(_ != sequence.last)
    for(elem <- sequence) {
      content.add(elem)
    }
  }

  def createNode(x1 : Double, x2 : Double) = {
    val node = new JfxNode(x1, x2)
    sequence = node +: sequence
    content.add(node)
  }

  def refreshContent = {
    content.remove(0, sequence.length)
    for(elem <- sequence) {
      elem.fill = Color.LightGrey
      content.add(elem)
    }
  }
  def setFocus(jfxNode: JfxNode) = {
    val temp = sequence.indexOf(jfxNode)
    sequence = sequence.filter(!_.equals(jfxNode)) :+ jfxNode
    refreshContent
    jfxNode.fill = Color.Grey
  }
  def checkCollisions(jfxNode: JfxNode): Boolean = {
    if(sequence.length == 1) false
    else {
      sequence.filter(!_.equals(jfxNode)).map(e => (e.x.toInt < (jfxNode.x.toInt + jfxNode.width.toInt)) && (
        (e.x.toInt + e.width.toInt) > jfxNode.x.toInt) &&
        (e.y.toInt < (jfxNode.y.toInt + jfxNode.height.toInt) &&
          ((e.y.toInt + e.height.toInt > jfxNode.y.toInt)))).reduce(_ || _)
    }
  }

  def handleKey(key: KeyCode): Unit ={
    key.toString match{
      case "DELETE" => delNode
      case _ => ()
    }

  }

  onMouseClicked = (event : MouseEvent) => {
    if(event.getClickCount == 2) {
      NodeManager.createNode(new Node(event.getX.toInt - 25, event.getY.toInt - 25))
      //createNode(x,y) - callback will be called and from there GUI node representation will be created
    }
  }

  def testIt = {
    val node = new InfoBox(50, 50)
    node.setLayoutX(100)
    node.setLayoutY(50)
    this.getChildren.add(node)
    ()
  }
}


class JfxNode(x1 : Double, y1 : Double) extends Rectangle {
  width = 50
  height = 50
  x = x1.toInt
  y = y1.toInt
  fill = Color.LightGrey
  var tempX: Int = 0
  var tempY: Int = 0
  var savedX: Int = 0
  var savedY: Int = 0
  val background = new DropShadow() {
    color = Color.LightGrey
    width = 52
    height = 52
    offsetX = -1
    offsetY = -1
  }
  onMousePressed = (event: MouseEvent) => {
    JfxWorksheet.setFocus(this)
    savedX = x.toInt
    savedY = y.toInt
    tempX = event.getX.toInt - x.toInt
    tempY = event.getY.toInt - y.toInt
    effect = background
  }
  onMouseReleased = (event: MouseEvent) => {
    effect = null
    if (JfxWorksheet.checkCollisions(this)) {
      x = savedX
      y = savedY
    }
  }
  onMouseDragged = (event: MouseEvent) => {
    if (JfxWorksheet.checkCollisions(this))
      background.color = Color.Red
    else
      background.color = Color.Grey
    x = event.getX.toInt - tempX
    y = event.getY.toInt - tempY
  }
}
