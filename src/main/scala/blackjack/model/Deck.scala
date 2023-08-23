package blackjack.model

class Deck {
  private var cards: List[Card] = generateAllCards()

  private def generateAllCards(): List[Card] = {
    for {
      suit <- Suit.values.toList
      rank <- Rank.values.toList
    } yield Card(suit, rank)
  }

  def shuffle(): Unit = {
    cards = generateAllCards()
    cards = scala.util.Random.shuffle(cards)
  }

  def deal(): Card = {
    val dealtCard = cards.head
    cards = cards.tail
    dealtCard
    }
}