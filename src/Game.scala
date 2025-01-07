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
  // map cells = 2d array of Boolean (true = wall, false = nothing)
  val map: Map = new Map(CELLS_XNBR, CELLS_YNBR)
  val player: Player = new Player(CELLS_XNBR/2, CELLS_YNBR/2)
  var pressedKeys: Set[Int] = Set.empty[Int]

  def printMap(): Unit = {
    val offset: Int = CELL_WIDTH*2
    display.setColor(Color.black)
    for(i <- map.cells.indices) {
      for(j <- map.cells(i).indices) {
        if(map.getCell(i, j)) display.drawFillRect(offset+i*CELL_WIDTH, offset+j*CELL_WIDTH, CELL_WIDTH, CELL_WIDTH)
      }
    }
  }

  def printPlayer(): Unit = {
    val offset: Int = CELL_WIDTH*2
    display.setColor(Color.red)
    display.drawFilledCircle(
      offset+player.posX*CELL_WIDTH,
      offset+player.posY*CELL_WIDTH,
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
