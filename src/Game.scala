import hevs.graphics.FunGraphics

import java.awt.Color
import java.awt.event.{KeyEvent, KeyListener}

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
  var pressedKeys: Set[Int] = Set.empty[Int]

  def printMap(): Unit = {
    val offset: Int = CELL_WIDTH*2
    for(i <- map.cells.indices) {
      for(j <- map.cells(i).indices) {
        map.getCell(i, j) match {
          case 1 =>
            display.setColor(Color.black)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case 2 =>
            display.setColor(Color.blue)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case 3 =>
            display.setColor(Color.red)
            display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
          case _ =>
        }
      }
    }
  }

  def printPlayer(): Unit = {
    val offset: Int = CELL_WIDTH*2
    display.setColor(Color.black)
    display.drawFilledCircle(
      offset+player.posX*CELL_WIDTH,
      offset+player.posY*CELL_WIDTH,
      CELL_WIDTH
    )

    display.setColor(Color.gray)
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
      printPlayer()
    }
  }

  def manageKeys(): Unit = {
    if(pressedKeys.contains(KeyEvent.VK_A)) player.move(Direction.Left, map)
    if(pressedKeys.contains(KeyEvent.VK_W)) player.move(Direction.Top, map)
    if(pressedKeys.contains(KeyEvent.VK_D)) player.move(Direction.Right, map)
    if(pressedKeys.contains(KeyEvent.VK_S)) player.move(Direction.Bottom, map)

    if(pressedKeys.contains(KeyEvent.VK_LEFT)) player2.move(Direction.Left, map)
    if(pressedKeys.contains(KeyEvent.VK_UP)) player2.move(Direction.Top, map)
    if(pressedKeys.contains(KeyEvent.VK_RIGHT)) player2.move(Direction.Right, map)
    if(pressedKeys.contains(KeyEvent.VK_DOWN)) player2.move(Direction.Bottom, map)
  }

  display.setKeyManager(new KeyListener {
    override def keyTyped(e: KeyEvent): Unit = {}
    override def keyReleased(e: KeyEvent): Unit = pressedKeys -= e.getKeyCode
    override def keyPressed(e: KeyEvent): Unit = pressedKeys += e.getKeyCode
  })

  while(true) {
    manageKeys()
    printGame()
    display.syncGameLogic(60)
    Thread.sleep(70)
  }

}
