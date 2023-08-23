package blackjack.view

import scalafx.animation.ScaleTransition
import scalafxml.core.macros.sfxml
import scalafx.scene.text.Text
import scalafx.util.Duration
import blackjack.Game
import javafx.animation.Interpolator

@sfxml
class MainMenuController(private val yellowText: Text) {

  initialize()

  private def initialize(): Unit = {
    animation()
  }

  private def animation(): Unit = {
    val breathe = new ScaleTransition(Duration(1000), yellowText) {
      byX = .2
      byY = .2
      interpolator = Interpolator.EASE_BOTH
      autoReverse = true
      cycleCount = ScaleTransition.Indefinite
    }
    breathe.play()
  }

  def start(): Unit = {
    Game.showTable()
  }
}
