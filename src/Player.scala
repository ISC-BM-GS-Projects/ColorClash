import scala.collection.mutable

object Direction extends Enumeration {
  val Left = 1
  val Top = 2
  val Right = 3
  val Bottom = 4
}

class Player(var posX: Int, var posY: Int,val playerId:Int, map: Map) {
  var alreadyMoved: Boolean = false
  var canMove: Boolean = true
  private val spawnX: Int = posX
  private val spawnY: Int = posY
  spawn()

  /**
   *
   * @param direction
   * @param map
   * @param playerId 2 for player1, 3 for player2
   * @return true if the player made a perfect win
   */
  def move(direction: Int, opponent: Player): Unit = {
    if(canMove && !alreadyMoved) {
      alreadyMoved = true
      map.setCell(posX, posY, playerId)
      direction match {
        case Direction.Left =>
          if (!(map.getCell(posX - 1, posY) == 1)) posX -= 1
        case Direction.Top =>
          if (!(map.getCell(posX, posY - 1) == 1)) posY -= 1
        case Direction.Right =>
          if (!(map.getCell(posX + 1, posY) == 1)) posX += 1
        case Direction.Bottom =>
          if (!(map.getCell(posX, posY + 1) == 1)) posY += 1
      }
      map.setCell(posX, posY, playerId)
      checkFilledForm(opponent)
    }
  }

  private def moveTo(x: Int, y: Int): Unit = {
    posX = x
    posY = y
    map.setCell(posX, posY, playerId)
  }

  def spawn(): Unit = moveTo(spawnX, spawnY)

  /**
   * Checks if a form is present in the map
   * @param opponent the opposing player
   */
  private def checkFilledForm(opponent: Player): Unit = {
    // We copy the map cells to be able to manipulate our cells without breaking the game
    val cellsCopy: Array[Array[Int]] = map.cells.map(_.clone())
    val mapCopy: Map = new Map(map.cellsX, map.cellsY, map.level)
    mapCopy.setCells(cellsCopy)

    // Fill every cell that is around a potential player form with -1
    floodFill(mapCopy, 0, 0, -1, paintWall = true)
    // Look for the first cell that is not one from this player or one of the -1s
    var (startX, startY): (Int, Int) = (-1, -1)
    val stack = mutable.Stack((posX, posY))
    if(posX < map.cellsX && posX >= 0 && posY < map.cellsY && posY >= 0) {
      if(posX+1 < map.cellsX) stack.push((posX + 1, posY))
      if(posX-1 >= 0) stack.push((posX - 1, posY))
      if(posY+1 < map.cellsY) stack.push((posX, posY + 1))
      if(posY-1 >= 0) stack.push((posX, posY - 1))
    }
    for(cell <- stack) {
      if(mapCopy.getCell(cell._1, cell._2) != playerId && mapCopy.getCell(cell._1, cell._2) != -1) {
        startX = cell._1
        startY = cell._2
      }
    }

    if(startX != -1) {
      var respawnOpp: Boolean = false
      val cellsToFill: mutable.Set[(Int, Int)] = floodFill(mapCopy, startX, startY)
      // Fill cells on the game map
      for(cell <- cellsToFill) {
        map.setCell(cell._1, cell._2, playerId)
        // Check if the opposing player is inside those cells and make it respawn if it's the case
        if(opponent.posX == cell._1 && opponent.posY == cell._2) respawnOpp = true
      }
      if(respawnOpp && !map.checkPerfectWin()) {
        val eatMusic = new Audio("/res/music/eat.wav")
        eatMusic.play()
        opponent.spawn()
      }
      if(map.checkPerfectWin()) {
        val perfectMusic = new Audio("/res/music/perfect.wav")
        perfectMusic.play()
        canMove = false
        opponent.canMove = false
      }
    }
  }

  /**
   * Change the value of every cell that can reach the startX,startY cell
   * without going through one of the player's cell to a given value.
   * This only leaves intact the cells that are separated from the startX,startY cell by a form with borders of the player's color.
   *
   * @param map the map that will be modified
   * @param startX the starting x position
   * @param startY the starting y position
   * @param fillValue the value that will be set to the concerned cell
   * @param paintWall determines if the paint can be overwritten by something else
   */
  private def floodFill(ffMap: Map, startX: Int, startY: Int, fillValue: Int = playerId, paintWall: Boolean = false): mutable.Set[(Int, Int)] = {
    val stack = mutable.Stack((startX, startY))
    val visited = mutable.Set[(Int, Int)]()
    val changed = mutable.Set[(Int, Int)]()

    while (stack.nonEmpty) {
      val (x, y) = stack.pop()

      if (ffMap.getCell(x, y) != playerId && !visited.contains((x, y))) {
        if(paintWall || ffMap.getCell(x, y) != 1) {
          ffMap.setCell(x, y, fillValue)
          changed.add((x, y))
        }
        visited.add((x, y))

        // Explore all four directions
        if(x < ffMap.cellsX && x >= 0 && y < ffMap.cellsY && y >= 0) {
          if(x+1 < ffMap.cellsX) stack.push((x + 1, y))
          if(x-1 >= 0) stack.push((x - 1, y))
          if(y+1 < ffMap.cellsY) stack.push((x, y + 1))
          if(y-1 >= 0) stack.push((x, y - 1))
        }
      }
    }

    changed
  }
}