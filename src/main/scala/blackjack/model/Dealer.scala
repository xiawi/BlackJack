package blackjack.model

class Dealer extends Player {
  def shouldHit():Boolean = {
    if (handValue >= 17) {false}
    else {true}
  }
  }
