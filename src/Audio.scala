import javax.sound.sampled.{AudioSystem, Clip}
class Audio (path : String) {
  var audioClip: Clip = _

  try {
    val url = classOf[Audio].getResource(path)
    val audioStream = AudioSystem.getAudioInputStream(url)

    audioClip = AudioSystem.getClip.asInstanceOf[Clip]
    audioClip.open(audioStream)
  } catch {
    case e: Exception =>
      e.printStackTrace()
  }

  def play(): Unit = {
    // Open stream and play
    try {
      if (!audioClip.isOpen) audioClip.open()
      audioClip.stop()
      audioClip.setMicrosecondPosition(0)
      audioClip.start()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
  def stop(): Unit = {
    // Open stream and play
    try {
      if (audioClip.isOpen) audioClip.stop()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}