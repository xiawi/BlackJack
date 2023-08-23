package blackjack.model

import blackjack.model.Rank.Rank
import blackjack.model.Suit.Suit

case class Card(suit: Suit, rank: Rank) {
  val imageUrl: String = s"/${suit.toString}/${rank.toString}.png"

  def rankValue: Int = rank match{
    case Rank.`10` | Rank.J | Rank.Q | Rank.K => 10
    case Rank.A => 1
    case _ => rank.id + 2
  }
}
