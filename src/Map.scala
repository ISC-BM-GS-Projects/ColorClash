class Map(val cellsX: Int, val cellsY: Int) {

  val cells: Array[Array[Boolean]] = generateCells(cellsX, cellsY)

  def generateCells(cellsX: Int, cellsY: Int): Array[Array[Boolean]] = {
    val cells: Array[Array[Boolean]] = Array.fill(cellsX, cellsY)(false)
    for(i <- 0 until cellsX) {
      for(j <- 0 until cellsY) {
        if(i == 0 || j == 0 || i == cellsX-1 || j == cellsY-1) cells(i)(j) = true
      }
    }
    cells
  }

  def getCell(posX: Int, posY: Int): Boolean = cells(posX)(posY)
}
