package blackjack

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}

object Game extends JFXApp {
  private val rootResource = getClass.getResource("view/Root.fxml")
  private val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  private val roots = loader.getRoot[jfxs.layout.BorderPane]
  // initialize stage
  stage = new PrimaryStage {
    title = "BlackJack"
    scene = new Scene {
      root = roots
      resizable = false
      requestFocus()
    }
  }
  private def showMainMenu(): Unit = {
    val resource = getClass.getResource("view/MainMenu.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }
  showMainMenu()

  def showTable(): Unit = {
    val resource = getClass.getResource("view/Table.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }
}

