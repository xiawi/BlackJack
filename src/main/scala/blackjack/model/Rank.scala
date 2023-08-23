package blackjack.model

object Rank extends Enumeration {
  type Rank = Value
  val `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`, J, Q, K, A = Value
}