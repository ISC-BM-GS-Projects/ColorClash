import hevs.graphics.FunGraphics

import java.awt.Color
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.SwingConstants
import scala.util.Random

class Button(var posX: Int, var posY: Int, var width: Int, var height: Int, text: String, display: FunGraphics
             , color: Color = Color.lightGray, textColor: Color = Color.black) {

  var visible: Boolean = false
  var clicked: Boolean = false

  def draw(): Unit = {
    visible = true
    display.setColor(color)
    display.drawFillRect(posX, posY, width, height)
    display.setColor(Color.black)
    display.drawRect(posX, posY, width, height)
    display.drawString(posX+width/2, posY+height/2, text, color = textColor, halign = SwingConstants.CENTER, valign = SwingConstants.CENTER)
  }
}

object ColorIO extends App {
  // display = map + 2 cells on each sides
  val display: FunGraphics = new FunGraphics(768, 768,420,100,"ColorIO",true)
  display.displayFPS(true)
  var play: Option[Play] = None

  private val startBtn: Button = new Button(display.width/2-75, display.height/2-37, 150, 75, "START GAME", display, color = Color.green)

  def drawMenu(): Unit = {
    display.clear()
    startBtn.draw()
    display.drawTransformedPicture(display.getFrameWidth()/2,170,0,0.4,"/res/logo.png")
  }

  display.addMouseListener(new MouseAdapter() {
    override def mouseClicked(e: MouseEvent): Unit = {
      // Start game
      if(e.getX >= startBtn.posX
        && e.getX <= startBtn.posX + startBtn.width
        && e.getY >= startBtn.posY
        && e.getY <= startBtn.posY + startBtn.height
        && startBtn.visible
      ) {
        startBtn.clicked = true
      } else {
        if(play.isDefined && play.get.canRestart) {
          play.get.victoryMusic.stop()
          drawMenu()
        }
      }
    }
  })

  drawMenu()
  while(true) {
    Thread.sleep(10)
    // Start Button
    if(startBtn.clicked) {
      startBtn.clicked = false
      startBtn.visible = false
      if(play.isEmpty || play.get.canRestart) play = Some(new Play(display, Random.nextInt(4)+1))
    }
  }
}
