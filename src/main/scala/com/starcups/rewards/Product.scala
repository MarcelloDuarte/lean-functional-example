package com.starcups.rewards

object Product {
  type SKU = String
  sealed trait Category
  case object Drink extends Category
  case object Extra extends Category
  case object Pastry extends Category
  case object Sandwich extends Category

  final case class Item(sku:SKU, description: String, category: Category)
}
