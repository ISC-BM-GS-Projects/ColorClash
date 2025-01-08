object Direction extends Enumeration {
  val Left = 1
  val Top = 2
  val Right = 3
  val Bottom = 4
}

class Player(var posX: Int, var posY: Int,val playerId:Int) {

  /**
   *
   * @param direction
   * @param map
   * @param playerId 2 for player1, 3 for player2
   */
  def move(direction: Int, map: Map): Unit = {
    direction match {
      case Direction.Left =>
        if(!(map.getCell(posX-1, posY)==1)) posX -= 1
      case Direction.Top =>
        if(!(map.getCell(posX, posY-1)==1)) posY -= 1
      case Direction.Right =>
        if(!(map.getCell(posX+1, posY)==1)) posX += 1
      case Direction.Bottom =>
        if(!(map.getCell(posX, posY+1)==1)) posY += 1
    }
    map.setCell(this.posX,this.posY,playerId)
  }
}