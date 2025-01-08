class Map(val cellsX: Int, val cellsY: Int) {

  val cells: Array[Array[Int]] = generateCells(cellsX, cellsY)

  def generateCells(cellsX: Int, cellsY: Int): Array[Array[Int]] = {
    val cells: Array[Array[Int]] = Array.fill(cellsX, cellsY)(0)
    for(i <- 0 until cellsX) {
      for(j <- 0 until cellsY) {
        if(i == 0 || j == 0 || i == cellsX-1 || j == cellsY-1) cells(i)(j) = 1
      }
    }
    cells
  }

  /**
   *
   * @param posX
   * @param posY
   * @param value new value for the cell (2 for blue, 3 for red)
   */
  def setCell(posX: Int, posY: Int, value:Int ):Unit = {
    cells(posX)(posY)=value
  }

  /**
   *
   * @param posX
   * @param posY
   * @return the value inside the cell
   *         0 for an empty cell,
   *         1 for a wall,
   *         2 for player1 color (blue),
   *         3 for player2 color (red)
   */
  def getCell(posX: Int, posY: Int): Int = cells(posX)(posY)
}
