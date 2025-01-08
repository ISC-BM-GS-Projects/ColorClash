import hevs.graphics.FunGraphics

import java.awt.Color
import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import java.util.{Timer, TimerTask}
import javax.swing.SwingConstants
import scala.collection.mutable.ArrayBuffer

object Game extends App {

  val CELL_WIDTH: Int = 20
  val CELLS_XNBR: Int = 20
  val CELLS_YNBR: Int = 20
  val MAP_WIDTH: Int = CELL_WIDTH * CELLS_XNBR
  val MAP_HEIGHT: Int = CELL_WIDTH * CELLS_YNBR
  // display = map + 2 cells on each sides
  val display: FunGraphics = new FunGraphics(MAP_WIDTH + 4*CELL_WIDTH, MAP_HEIGHT + 4*CELL_WIDTH)
  // map cells = 2d array of Int (0=nothing,1=wall,2=bluecell,3=redcell)
  val map: Map = new Map(CELLS_XNBR, CELLS_YNBR)
  val player: Player = new Player(1, 1, 2)
  val player2: Player = new Player(CELLS_XNBR-2,CELLS_XNBR-2 ,3)
  var play: Boolean = false
  var pressedKeys: ArrayBuffer[Int] = ArrayBuffer.empty[Int]
  var timerVal: Int = 30

  def printMap(): Unit = {
    val offset: Int = CELL_WIDTH*2
    for(i <- map.cells.indices) {
      for(j <- map.cells(i).indices) {
        map.getCell(i, j) match {
          case 1 =>
            display.setColor(Color.black)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case 2 =>
            display.setColor(Color.red)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case 3 =>
            display.setColor(Color.blue)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case _ =>
        }
      }
    }
  }

  def printTimer(): Unit = {
    val strVal: String = timerVal.toString
    display.drawString(display.getFrameWidth()/2, 35, strVal, halign = SwingConstants.CENTER)
  }

  def printPlayer(): Unit = {
    val offset: Int = CELL_WIDTH*2
    display.setColor(new Color(150,0,0))
    display.drawFilledCircle(
      offset+player.posX*CELL_WIDTH,
      offset+player.posY*CELL_WIDTH,
      CELL_WIDTH
    )

    display.setColor(new Color(0,0,130))
    display.drawFilledCircle(
      offset+player2.posX*CELL_WIDTH,
      offset+player2.posY*CELL_WIDTH,
      CELL_WIDTH
    )
  }

  def printGame(): Unit = {
    display.frontBuffer.synchronized{
      display.clear()
      printMap()
      printTimer()
      printPlayer()
    }
  }

  def printScores(): Unit = {
    val p1Score: Int = map.cells.flatten.count(_ == 2)  // flatten converts a 2d array into a 1d array. Ex: [[1, 2, 3], [4, 5, 6]] => [1, 2, 3, 4, 5, 6]
    val p2Score: Int = map.cells.flatten.count(_ == 3)
    display.drawString(display.getFrameWidth()/4, display.getFrameHeight()/3, s"Player 1 score: ${p1Score.toString}", color = Color.red, halign = SwingConstants.CENTER)
    display.drawString(display.getFrameWidth()/4*3, display.getFrameHeight()/3, s"Player 2 score: ${p2Score.toString}", color = Color.blue, halign = SwingConstants.CENTER)
    val resultMsg: String = {
      if(p1Score>p2Score) "PLAYER 1 WINS!"
      else if(p2Score>p1Score) "PLAYER 2 WINS!"
      else "IT'S A TIE!"
    }
    display.drawString(display.getFrameWidth()/2, display.getFrameHeight()/3*2, resultMsg, halign = SwingConstants.CENTER)
  }

  def manageKeys(): Unit = {
    for(i <- pressedKeys.size-1 to 0 by -1) {
      pressedKeys(i) match {
        case KeyEvent.VK_A => player.move(Direction.Left, map)
        case KeyEvent.VK_W => player.move(Direction.Top, map)
        case KeyEvent.VK_D => player.move(Direction.Right, map)
        case KeyEvent.VK_S => player.move(Direction.Bottom, map)
        case KeyEvent.VK_LEFT => player2.move(Direction.Left, map)
        case KeyEvent.VK_UP => player2.move(Direction.Top, map)
        case KeyEvent.VK_RIGHT => player2.move(Direction.Right, map)
        case KeyEvent.VK_DOWN => player2.move(Direction.Bottom, map)
        case _ => // do nothing
      }
    }
  }

  display.setKeyManager(new KeyListener {
    override def keyTyped(e: KeyEvent): Unit = {}
    override def keyReleased(e: KeyEvent): Unit = pressedKeys -= e.getKeyCode
    override def keyPressed(e: KeyEvent): Unit = if(!pressedKeys.contains(e.getKeyCode)) pressedKeys += e.getKeyCode
  })

  val timer: Timer = new Timer()
  val timerTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      if(timerVal != 0) timerVal -= 1
      else {
        timer.cancel()
        play = false
        display.clear()
        Thread.sleep(1000)
        printScores()
      }
    }
  }

  play = true
  while(true) {
    if(play) {
      timerVal = 30
      timer.schedule(timerTask, 1000L, 1000L)
    }
    while(play) {
      player.alreadyMoved = false
      player2.alreadyMoved = false
      manageKeys()
      printGame()
      display.syncGameLogic(60)
      Thread.sleep(70)
    }
  }

}
