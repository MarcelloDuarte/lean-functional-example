package com.starcups.rewards


import cats.data.Kleisli
import com.starcups.rewards.domain.Valid
import com.starcups.rewards.Account.Account
import com.starcups.rewards.Order._
import com.starcups.rewards.Product.{Drink, Item}
import com.starcups.rewards.interpreter.{InMemRewardsRepo, RewardsServiceInterpreter}

object RewardsApp extends App {
  type RewardsOperation = Kleisli[Valid, RewardsRepository, Account]
  override def main(args: Array[String]): Unit = {
    val repo: RewardsRepository = new InMemRewardsRepo
    val card: Account = Account.newCard.toOption.get
    val drink: Item = Item("COF", "coffee", Drink)
    val order: Order = Order.updateOrder(Order.newOrder,OrderItem(drink, 2)).getOrElse(Order.newOrder)

    val service: RewardsOperation = (new RewardsServiceInterpreter).collectStars(card.no, order)

    repo.store(card)
    val result = service.run(repo)

    println(result)
  }
}
