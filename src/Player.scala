object Direction extends Enumeration {
  val Left = 1
  val Top = 2
  val Right = 3
  val Bottom = 4
}

class Player(var posX: Int, var posY: Int) {

  def move(direction: Int, map: Map): Unit = {
    direction match {
      case Direction.Left =>
        if(!map.getCell(posX-1, posY)) posX -= 1
      case Direction.Top =>
        if(!map.getCell(posX, posY-1)) posY -= 1
      case Direction.Right =>
        if(!map.getCell(posX+1, posY)) posX += 1
      case Direction.Bottom =>
        if(!map.getCell(posX, posY+1)) posY += 1
    }
  }
}