import hevs.graphics.FunGraphics

object ColorIO extends App {
  // display = map + 2 cells on each sides
  val display: FunGraphics = new FunGraphics(480, 480)
  val play: Play = new Play(display)
}
