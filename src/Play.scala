import hevs.graphics.FunGraphics

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.{Timer, TimerTask}
import javax.swing.SwingConstants
import scala.collection.mutable.ArrayBuffer

class Play(val display: FunGraphics,val level:Int) {
  private val CELL_WIDTH: Int = 32
  private val CELLS_XNBR: Int = 20
  private val CELLS_YNBR: Int = 20
  // map cells = 2d array of Int (0=nothing,1=wall,2=bluecell,3=redcell)
  private val map: Map = new Map(CELLS_XNBR, CELLS_YNBR,level)
  private val player: Player = new Player(1, 1, 2, map)
  private val player2: Player = new Player(CELLS_XNBR-2,CELLS_XNBR-2 ,3, map)
  private val pressedKeys: ArrayBuffer[Int] = ArrayBuffer.empty[Int]
  private var timerVal: Int = 145
  private var play: Boolean = true
  var canRestart: Boolean = false
  val backgroundMusic = new Audio("/res/music1.wav")
  backgroundMusic.play()
  val victoryMusic = new Audio("/res/victory.wav")

  private def printMap(): Unit = {
    val offset: Int = CELL_WIDTH*2+3
    display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,1,"/res/ground_v2.png")
    for(i <- map.cells.indices) {
      for(j <- map.cells(i).indices) {
        map.getCell(i, j) match {
          case 2 =>
            display.setColor(new Color(255,50,50))
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH-7, CELL_WIDTH-7)
          case 3 =>
            display.setColor(new Color(170,170,255))
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH-7, CELL_WIDTH-7)
          case _ =>
        }
      }

    }
    level match{
      case 1 => display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,2,"/res/wallstone_l1.png")
      case 2 => display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,2,"/res/wallstone_l2.png")
      case 3 => display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,2,"/res/wallstone_l3.png")
      case 4 => display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,2,"/res/wallstone_l4.png")
    }
  }

  private def printTimer(): Unit = {
    val strVal: String = timerVal.toString
    display.drawString(display.getFrameWidth()/2, 35, strVal, halign = SwingConstants.CENTER)
  }

  private def printPlayer(): Unit = {
    val offset: Int = CELL_WIDTH*2+CELL_WIDTH/2-1
    display.drawTransformedPicture(offset+player.posX*CELL_WIDTH,offset+player.posY*CELL_WIDTH,0,2,"/res/player1.png")
    display.drawTransformedPicture(offset+player2.posX*CELL_WIDTH,offset+player2.posY*CELL_WIDTH,0,2,"/res/player2.png")
  }

  private def printGame(): Unit = {
    display.frontBuffer.synchronized{
      display.clear()
      printMap()
      printTimer()
      printPlayer()
    }
  }

  private def printScores(): Unit = {
    val p1Score: Int = map.cells.flatten.count(_ == 2)  // flatten converts a 2d array into a 1d array. Ex: [[1, 2, 3], [4, 5, 6]] => [1, 2, 3, 4, 5, 6]
    val p2Score: Int = map.cells.flatten.count(_ == 3)
    display.clear()
    display.drawString(display.getFrameWidth()/4, display.getFrameHeight()/3, s"Player 1 score: ${p1Score.toString}", color = Color.red, halign = SwingConstants.CENTER)
    display.drawString(display.getFrameWidth()/4*3, display.getFrameHeight()/3, s"Player 2 score: ${p2Score.toString}", color = Color.blue, halign = SwingConstants.CENTER)
   var resultMsg:String=""
      if(p1Score>p2Score){
        resultMsg = "PLAYER 1 WINS!"
        display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()-200,0,3,"/res/player1.png")
      } else if(p2Score>p1Score){
        resultMsg =  "PLAYER 2 WINS!"
        display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()-200,0,3,"/res/player2.png")
      } else{
        resultMsg = "IT'S A TIE!"
        display.drawTransformedPicture(display.getFrameWidth()/2-20,display.getFrameHeight()-200,0,3,"/res/player1.png")
        display.drawTransformedPicture(display.getFrameWidth()/2+20,display.getFrameHeight()-200,0,3,"/res/player2.png")
      }

    display.drawString(display.getFrameWidth()/2, display.getFrameHeight()/3*2, resultMsg, halign = SwingConstants.CENTER)
  }

  private def manageKeys(): Unit = {
    for(i <- pressedKeys.size-1 to 0 by -1) {
      pressedKeys(i) match {
        case KeyEvent.VK_A => player.move(Direction.Left, player2)
        case KeyEvent.VK_W => player.move(Direction.Top, player2)
        case KeyEvent.VK_D => player.move(Direction.Right, player2)
        case KeyEvent.VK_S => player.move(Direction.Bottom, player2)
        case KeyEvent.VK_LEFT => player2.move(Direction.Left, player)
        case KeyEvent.VK_UP => player2.move(Direction.Top, player)
        case KeyEvent.VK_RIGHT => player2.move(Direction.Right, player)
        case KeyEvent.VK_DOWN => player2.move(Direction.Bottom, player)
        case _ => // do nothing
      }
    }
  }

  display.setKeyManager(new KeyAdapter {
    override def keyReleased(e: KeyEvent): Unit = pressedKeys -= e.getKeyCode
    override def keyPressed(e: KeyEvent): Unit = if(!pressedKeys.contains(e.getKeyCode)) pressedKeys += e.getKeyCode
  })

  var timer: Timer = new Timer()
  val timerTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      if(timerVal != 0) timerVal -= 1
      else {
        play = false
        timer.cancel()
        display.clear()
        Thread.sleep(1000)
        printScores()
        backgroundMusic.stop()
        victoryMusic.play()
        canRestart = true
      }
    }
  }
  timer.schedule(timerTask, 1000L, 1000L)

  while(play) {
    player.alreadyMoved = false
    player2.alreadyMoved = false
    manageKeys()
    printGame()
    if(map.checkPerfectWin()) timerVal = 0
    display.syncGameLogic(15)
  }
}
