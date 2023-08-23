package blackjack.view

import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{AnchorPane, HBox}
import scalafx.Includes._
import scalafxml.core.macros.sfxml
import blackjack.model._
import scalafx.animation.{FadeTransition, Interpolator, KeyFrame, ParallelTransition, RotateTransition, Timeline, TranslateTransition}
import scalafx.scene.Group
import scalafx.scene.control.Label
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}
import scalafx.util.Duration

import scala.util.Random

@sfxml
class TableController(
                       val playerCardContainer: HBox,
                       val dealerCardContainer: HBox,
                       val buttonGroup: Group,
                       val tableContainer: AnchorPane) {
  private var peeking = false
  private val deck = new Deck()
  private val player = new Player()
  private val dealer = new Dealer()
  private var animationsCompleted = false
  private var isHovering = false
  private var soundPlaying = false
  private val winnerLabel = new Label() {
    font = Font.font("Lucida Sans Demibold", size=40)
    textFill = Color.Yellow
    alignmentInParent = scalafx.geometry.Pos.Center
    visible = false
  }
  winnerLabel.visible = false
  tableContainer.children.add(winnerLabel)
  private val victory = new MediaPlayer(new Media(getClass.getResource("/victory.mp3").toExternalForm)){
    onPlaying = () => soundPlaying = true
    stopTime = Duration(900)
    onEndOfMedia = () => {
      soundPlaying = false
      soundStopper()
    }
  }
  private val loss = new MediaPlayer(new Media(getClass.getResource("/loss.mp3").toExternalForm)){
    onPlaying = () => soundPlaying = true
    stopTime = Duration(900)
    onEndOfMedia = () => {
      soundPlaying = false
      soundStopper()
    }
  }
  private val draw = new MediaPlayer(new Media(getClass.getResource("/draw.mp3").toExternalForm)){
    onPlaying = () => soundPlaying = true
    stopTime = Duration(900)
    onEndOfMedia = () => {
      soundPlaying = false
      soundStopper()
    }
  }
  private val cardDeal = new MediaPlayer(new Media(getClass.getResource("/card.mp3").toExternalForm)){
    onPlaying = () => soundPlaying = true
    stopTime = Duration(900)
    onEndOfMedia = () => {
      soundPlaying = false
      soundStopper()
    }
  }

  dealerCardContainer.setSpacing(10)
  playerCardContainer.setSpacing(10)

  initialize()

  private def soundStopper(): Unit = {
    if (soundPlaying) {
      return
    }
    victory.stop()
    loss.stop()
    draw.stop()
    cardDeal.stop()
  }

  private def initialize(): Unit = {
    startGame()
  }

  private def startGame(): Unit = {
    player.hand.clear()
    dealer.hand.clear()
    playerCardContainer.children.clear()
    dealerCardContainer.children.clear()
    deck.shuffle()
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(0), onFinished = _ => {
          dealCard(player, playerCardContainer, facedown = true)
        }),
        KeyFrame(Duration(1000), onFinished = _ => dealCard(dealer, dealerCardContainer, facedown = true)),
        KeyFrame(Duration(2000), onFinished = _ => dealCard(player, playerCardContainer, facedown = true)),
        KeyFrame(Duration(3000), onFinished = _ => dealCard(dealer, dealerCardContainer, facedown = false)),
        KeyFrame(Duration(5000), onFinished = _ => takeCards()),
        KeyFrame(Duration(6000), onFinished = _ => {
          peekCard()
          buttonFadeIn()
          animationsCompleted = true
        })
      )
    }
    timeline.play()
  }

  private def dealCard(participant: Player, container: HBox, facedown: Boolean): Unit = {
    cardDeal.play()
    val randomCard: Card = deck.deal()
    val angle = new Random()
    var cardImage:ImageView = null
    if (facedown){
    cardImage = new ImageView(new Image(getClass.getResourceAsStream("/cardback.png")))}
    else{
      cardImage = new ImageView(new Image(getClass.getResourceAsStream(randomCard.imageUrl)))
    }
    cardImage.setFitWidth(125)
    cardImage.setFitHeight(181.5)
    // Create a translate transition for the animation
    val transition = new TranslateTransition(Duration(1500), cardImage) {
      toX = container.children.size
      fromY = -1000
      toY = 0
      interpolator = Interpolator.EaseOut
    }

    val rotation = new RotateTransition(Duration(1500), cardImage) {
      byAngle = 360 + angle.nextInt(55) - angle.nextInt(55)
    }

    val animation = new ParallelTransition(Seq(transition, rotation))

    container.children.add(cardImage)
    participant.receiveCard(randomCard)
    animation.play()
  }

  private def takeCards(): Unit = {
    val animation = new TranslateTransition(Duration(1000), playerCardContainer) {
      toY = 600
      interpolator = Interpolator.EaseBoth
    }
    animation.play()
  }

  private def peekCard(): Unit = {
    playerCardContainer.children.clear()
    for (card <- player.hand) {
      val cardImage = new ImageView(new Image(getClass.getResourceAsStream(card.imageUrl)))
      playerCardContainer.children.add(cardImage)
      cardImage.setFitWidth(125)
      cardImage.setFitHeight(181.5)
    }
    val animation = new TranslateTransition(Duration(1000), playerCardContainer) {
      toY = 100
    }
    animation.play()
  }

  def hoverCards(): Unit = {
    if (!animationsCompleted) {
    }
    else {
      if (peeking){
        stopHovering()
      }
      else {
        animationsCompleted = false
        val animation = new TranslateTransition(Duration(500), playerCardContainer) {
          byY = -100
          interpolator = Interpolator.EaseOut
          onFinished = _ => {
            animationsCompleted = true
            peeking = true
          }
        }
        animation.play()
      }
    }
  }

  def stopHovering(): Unit = {
    if (!animationsCompleted) {
    }
    else {
      if (!peeking){
        hoverCards()
      }
      else {
        animationsCompleted = false
        val animation = new TranslateTransition(Duration(500), playerCardContainer) {
          byY = 100
          interpolator = Interpolator.EaseOut
          onFinished = _ => {
            animationsCompleted = true
            peeking = false
          }
        }
        animation.play()
      }
    }
  }

  private def buttonFadeIn(): Unit = {
    val fadeIn = new FadeTransition(Duration(500), buttonGroup) {
      fromValue = 0
      toValue = 1
    }
    fadeIn.play()
    buttonGroup.children.foreach(child => {
      child.disable = false
    })
  }

  private def buttonFadeOut(): Unit = {
    val fadeOut = new FadeTransition(Duration(500), buttonGroup) {
      fromValue = 1
      toValue = 0
    }
    fadeOut.play()
    buttonGroup.children.foreach(child => {
      child.disable = true
    })
  }

  def hit(): Unit = {
    cardDeal.play()
    val angle = new Random()
    val newCard = new ImageView(new Image(getClass.getResourceAsStream("/cardback.png")))
    newCard.setFitWidth(125)
    newCard.setFitHeight(181.5)
    playerCardContainer.setSpacing(10)
    tableContainer.children.add(newCard)
    // Create a translate transition for the animation
    val transition = new TranslateTransition(Duration(1500), newCard) {
      toX = 450
      fromY = -1000
      toY = 800
      interpolator = Interpolator.EaseOut
    }

    val rotation = new RotateTransition(Duration(1500), newCard) {
      byAngle = 360 + angle.nextInt(55) - angle.nextInt(55)
    }

    val animation = new ParallelTransition(Seq(transition, rotation))

    val randomCard: Card = deck.deal()
    val cardImage = new ImageView(new Image(getClass.getResourceAsStream(randomCard.imageUrl)))
    cardImage.setFitWidth(125)
    cardImage.setFitHeight(181.5)

    val timeline = new Timeline() {
      keyFrames = Seq(
        KeyFrame(Duration(0), onFinished = _ => buttonFadeOut()),
        KeyFrame(Duration(0), onFinished = _ => animation.play()),
        KeyFrame(Duration(2000), onFinished = _ => takeCards()),
        KeyFrame(Duration(3000), onFinished = _ => {
          player.hit(deck)
          playerCardContainer.children += cardImage
          buttonFadeIn()
          peekCard()
        }),
        KeyFrame(Duration(4000), onFinished = _ => {
          if (player.isBusted){
            stand()
          }
        })
      )
    }
    timeline.play()
  }

  def stand(): Unit = {
    buttonFadeOut()
    dealerAnimation()
  }

  private def dealerAnimation(): Unit = {
    val takeCards = new TranslateTransition(Duration(1000), dealerCardContainer) {
      toY = -600
      interpolator = Interpolator.EaseBoth
    }
    val showHand = new TranslateTransition(Duration(1000), dealerCardContainer) {
      toY = 0
    }
    val timeline = new Timeline{
      keyFrames = Seq(
        KeyFrame(Duration(0), onFinished = _ => takeCards.play()),
        KeyFrame(Duration(1000), onFinished = _ => dealerCardContainer.children.clear()),
        KeyFrame(Duration(1000), onFinished = _ => {
          for (card <- dealer.hand) {
            val cardImage = new ImageView(new Image(getClass.getResourceAsStream(card.imageUrl)))
            dealerCardContainer.children.add(cardImage)
            cardImage.setFitWidth(125)
            cardImage.setFitHeight(181.5)
          }
        }),
        KeyFrame(Duration(1000), onFinished = _ => showHand.play()),
        KeyFrame(Duration(2000), onFinished = _ => dealerDecision())
      )
    }
    timeline.play()
  }

  private def dealerDecision(): Unit = {
    if (dealer.shouldHit()){
      val timeline = new Timeline{
        keyFrames = Seq(
          KeyFrame(Duration(0), onFinished = _ => dealCard(dealer, dealerCardContainer, facedown=false)),
          KeyFrame(Duration(1000), onFinished = _ => dealerDecision())
        )
      }
      timeline.play()
    }
    else{
      gameEnd()
    }
  }

  private def gameEnd(): Unit = {
    animationsCompleted = false
    // Animation
    val animation = new TranslateTransition(Duration(500), playerCardContainer){
      toY = 0
    }
    // Logic
    def winLogic(): Unit = {
      winnerLabel.visible = true
      if (player.isBusted && dealer.isBusted){
        winnerLabel.text = "Player and Dealer Both Busted!It's a Draw!"
        draw.play()
      }
      else if (player.isBusted){
        winnerLabel.text = "Player Busted! Dealer Wins!"
        loss.play()
      }
      else if (dealer.isBusted){
        winnerLabel.text = "Dealer Busted! Player Wins!"
        victory.play()
      }
      else{
        if (player.handValue > dealer.handValue){
          winnerLabel.text = "Player Wins!"
          victory.play()
        }
        else if (player.handValue == dealer.handValue){
          winnerLabel.text = "It's a Draw!"
          draw.play()
        }
        else{
          winnerLabel.text = "Dealer Wins!"
          loss.play()
        }
      }
    }
    // Keyframing
    val timeline = new Timeline{
      keyFrames = Seq(
        KeyFrame(Duration(1000), onFinished = _ => animation.play()),
        KeyFrame(Duration(2000), onFinished = _ => winLogic()),
        KeyFrame(Duration(4000), onFinished = _ => {
          startGame()
          winnerLabel.visible = false
        }),

      )
    }
    timeline.play()
  }
}