package blackjack.model

import scala.collection.mutable.ListBuffer

class Player {
  var hand: ListBuffer[Card] = ListBuffer.empty[Card]
  def isBusted: Boolean = {
    if (handValue > 21) true
    else false
  }

  def receiveCard(card: Card): Unit = {
    hand += card
  }

  def handValue: Int = {
    val nonAceCards = hand.filterNot(_.rank == Rank.A)
    val aceCount = hand.count(_.rank == Rank.A)

    val nonAceValue = nonAceCards.map(_.rankValue).sum
    val potentialAceValue = nonAceValue + aceCount + 10 // Using Ace as 11

    if (aceCount == 0) {
      nonAceValue
    } else if (potentialAceValue <= 21) {
      potentialAceValue
    } else {
      nonAceValue + aceCount // Use all Aces as 1
    }
  }

  def hit(deck: Deck): Unit = {
    receiveCard(deck.deal())
  }
}
