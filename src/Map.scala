class Map(val cellsX: Int, val cellsY: Int,val level: Int) {

  val cells: Array[Array[Int]] = generateCells(cellsX, cellsY,level)

  def generateCells(cellsX: Int, cellsY: Int,level:Int ): Array[Array[Int]] = {
    val cells: Array[Array[Int]] = Array.fill(cellsX, cellsY)(0)
    for(i <- 0 until cellsX) {
      for(j <- 0 until cellsY) {
        if(i == 0 || j == 0 || i == cellsX-1 || j == cellsY-1) cells(i)(j) = 1
      }
    }
    level match{
      case 1 =>
      case 2 =>
        for(i <- 0 until 5) {
          for(j <- 0 until 5) {
            if(i==0 || i==4){
              if(j!=2) cells(i+4)(j+4) = 1
              if(j!=2) cells(i+4)(j+11) = 1
              if(j!=2) cells(i+11)(j+4) = 1
              if(j!=2) cells(i+11)(j+11) = 1
            }
            if(i==1 || i==3){
              if(j==0 || j==4) cells(i+4)(j+4) = 1
              if(j==0 || j==4) cells(i+4)(j+11) = 1
              if(j==0 || j==4) cells(i+11)(j+4) = 1
              if(j==0 || j==4) cells(i+11)(j+11) = 1
            }
          }
        }
      case 3 =>
        for(i <- 9 to 10) {
          for(j <- 0 until cellsY) {
            if(j!=4 && j!=15){
              cells(i)(j) = 1
            }
          }
        }
        for(j <- 9 to 10) {
          for(i <- 0 until cellsX) {
            if(i!=4 && i!=15){
              cells(i)(j) = 1
            }
          }
        }
      case 4 =>
        for(i <- 2 until 18) {
          for(j <- 2 until 18) {
            if(i==2 || i==17){
              if(j!=9 && j!=10) cells(i)(j) = 1
            }
            if(i==3 || i==16){
              if(j==2 || j==17) cells(i)(j) = 1
            }
            if(i==4 || i==15){
              if(j!=3 && j!=6 && j!=13 && j!=16) cells(i)(j) = 1
            }
            if(i==5 || i==14){
              if(j==2 || j==4 || j==15 || j==17) cells(i)(j) = 1
            }
            if(i==6 || i==13){
              if(j!=3 && j!=4 && j!=5 && j!=9 && j!=10 && j!=14 && j!=15 && j!=16) cells(i)(j) = 1
            }
            if(i==7 || i==12){
              if(j==2 || j==4 || j==6 || j==13 || j==15 || j==17) cells(i)(j) = 1
            }

            if(i==8 || i==11){
              if(j==2 || j==4 || j==6 || j==8 || j==11 || j==13 || j==15 || j==17) cells(i)(j) = 1
            }
            if(i==9 || i==10){
              if(j==4 || j==9 || j==10 || j==15) cells(i)(j) = 1
            }

          }
        }
      case _ =>
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
