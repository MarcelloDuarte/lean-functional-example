package com.starcups.rewards

import java.util.UUID

import cats.data.NonEmptyList
import com.starcups.rewards.domain._
import com.starcups.rewards.Product._
import com.starcups.rewards.domain.RewardsError

object Order {
  sealed case class OrderError(message: String) extends RewardsError
  final object NegativeQuantity extends OrderError("Negative quantity")
  val negativeQuantity: Valid[Quantity] = Left(NonEmptyList.one(NegativeQuantity))

  type OrderNumber = UUID
  type OrderItemNumber = UUID
  type Quantity = Int
  case class OrderItem(product: Item, quantity: Quantity)
  case class Order(no: OrderNumber, items: Map[OrderItemNumber, OrderItem])

  val drinksInOrder: Order => Int = (order: Order) => {
    order.items.foldLeft(0)((acc,kv) =>
      acc + (if (kv._2.product.category == Drink) kv._2.quantity else 0))
  }

  def updateOrder(order:Order, item: OrderItem): Valid[Order] = {
      Right(order.copy(items = order.items + (UUID.randomUUID() -> item)))
  }

  def newOrder: Order = Order(UUID.randomUUID, Map())
}
