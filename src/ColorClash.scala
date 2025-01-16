import hevs.graphics.FunGraphics

import java.awt.Color
import java.awt.event.{MouseAdapter, MouseEvent}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.{JFileChooser, JOptionPane, SwingConstants}
import scala.util.Random

class Button(var posX: Int, var posY: Int, var width: Int, var height: Int, text: String, display: FunGraphics
             , color: Color = Color.lightGray, textColor: Color = Color.black) {

  var visible: Boolean = false
  var clicked: Boolean = false

  def draw(): Unit = {
    visible = true
    display.setColor(color)
    display.drawFillRect(posX, posY, width, height)
    display.setColor(Color.black)
    display.drawRect(posX, posY, width, height)
    display.drawString(posX+width/2, posY+height/2, text, color = textColor, halign = SwingConstants.CENTER, valign = SwingConstants.CENTER)
  }
}

object ColorClash extends App {
  // display = map + 2 cells on each sides
  val display: FunGraphics = new FunGraphics(768, 768,420,100,"ColorClash",true)
  display.displayFPS(true)
  var play: Option[Play] = None
  private var p1ImgPath: String = "/res/player1.png"
  private var p2ImgPath: String = "/res/player2.png"
  private var p1ImgScale: Double = 1.9
  private var p2ImgScale: Double = 1.9

  drawLoadingScreen()

  private def drawLoadingScreen(): Unit = {
    display.clear(Color.black)
    val scMusic = new Audio("/res/music/supercell-jingle.wav")
    scMusic.play()
    Thread.sleep(500)
    display.drawTransformedPicture(display.getFrameWidth()/2,display.getFrameHeight()/2,0,1,"/res/isc-logo.png")
    Thread.sleep(1500)
  }

  private val startBtn: Button = new Button(display.width/2-75, display.height/2-37, 150, 75, "START GAME", display, color = Color.green)
  private val p1SpriteBtn: Button = new Button(display.width/4-37, display.height/4*3-15, 75, 30, "Browse", display)
  private val p2SpriteBtn: Button = new Button(display.width/4*3-37, display.height/4*3-15, 75, 30, "Browse", display)

  private def drawMenu(): Unit = {
    display.clear()
    display.drawTransformedPicture(display.getFrameWidth()/2,170,0,0.4,"/res/logo.png")
    startBtn.draw()
    p1SpriteBtn.draw()
    p2SpriteBtn.draw()
    display.drawTransformedPicture(p1SpriteBtn.posX+p1SpriteBtn.width/2, p1SpriteBtn.posY-30, 0, p1ImgScale*2, p1ImgPath)
    display.drawTransformedPicture(p2SpriteBtn.posX+p2SpriteBtn.width/2, p2SpriteBtn.posY-30, 0, p2ImgScale*2, p2ImgPath)
  }

  private def browseClicked(btn: Button, imgPath: String, scale: Double): (String, Double) = {
    btn.clicked = false
    val fileChooser: JFileChooser = new JFileChooser()
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    fileChooser.setMultiSelectionEnabled(false)
    fileChooser.setCurrentDirectory(new File("./src/res"))
    val imageFilter = new FileNameExtensionFilter( "Image Files", "jpg", "jpeg", "png", "gif")
    fileChooser.setFileFilter(imageFilter)
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
      val path: String = fileChooser.getSelectedFile.getAbsolutePath
      if(path.isEmpty
        || path.substring(0,path.length-path.split("\\\\").last.length-1) != new File("./src/res").getAbsolutePath.replace("\\.", "")
      ) {
        JOptionPane.showMessageDialog(null, "Your image is not compatible !\n\nPlease check that your image is in the res folder of the app.", null, JOptionPane.PLAIN_MESSAGE)
      } else {
        val img = ImageIO.read(fileChooser.getSelectedFile)
        return (s"/res/${path.split("\\\\").last}", 25.0/Math.max(img.getWidth, img.getHeight()))
      }
    }
    (imgPath, scale)
  }

  display.addMouseListener(new MouseAdapter() {
    override def mouseClicked(e: MouseEvent): Unit = {
      // Start game
      if(e.getX >= startBtn.posX
        && e.getX <= startBtn.posX + startBtn.width
        && e.getY >= startBtn.posY
        && e.getY <= startBtn.posY + startBtn.height
        && startBtn.visible
      ) {
        startBtn.clicked = true
      } else {
        if(play.isDefined && play.get.canRestart) {
          play.get.victoryMusic.stop()
          drawMenu()
        }
      }
      // Browse p1 sprite
      if(e.getX >= p1SpriteBtn.posX
        && e.getX <= p1SpriteBtn.posX + p1SpriteBtn.width
        && e.getY >= p1SpriteBtn.posY
        && e.getY <= p1SpriteBtn.posY + p1SpriteBtn.height
        && p1SpriteBtn.visible
      ) p1SpriteBtn.clicked = true
      // Browse p2 sprite
      if(e.getX >= p2SpriteBtn.posX
        && e.getX <= p2SpriteBtn.posX + p2SpriteBtn.width
        && e.getY >= p2SpriteBtn.posY
        && e.getY <= p2SpriteBtn.posY + p2SpriteBtn.height
        && p2SpriteBtn.visible
      ) p2SpriteBtn.clicked = true
    }
  })

  drawMenu()
  while(true) {
    Thread.sleep(10)
    // Start Button
    if(startBtn.clicked) {
      startBtn.clicked = false
      startBtn.visible = false
      p1SpriteBtn.visible = false
      p2SpriteBtn.visible = false
      if(play.isEmpty || play.get.canRestart) play = Some(new Play(display, Random.nextInt(4)+1, p1ImgPath, p1ImgScale, p2ImgPath, p2ImgScale))
    }
    // Browse p1 sprite
    if(p1SpriteBtn.clicked) {
      val (path, scale) = browseClicked(p1SpriteBtn, p1ImgPath, p1ImgScale)
      p1ImgPath = path
      p1ImgScale = scale
      drawMenu()
    }
    // Browse p2 sprite
    if(p2SpriteBtn.clicked) {
      val (path, scale) = browseClicked(p2SpriteBtn, p2ImgPath, p2ImgScale)
      p2ImgPath = path
      p2ImgScale = scale
      drawMenu()
    }
  }
}
