package com.starcups.rewards

import cats.data.Kleisli
import com.starcups.rewards.domain._
import com.starcups.rewards.Account._
import com.starcups.rewards.Order._

trait RewardsService {
  def collectStars(no: AccountNumber, order: Order): Kleisli[Valid, RewardsRepository, Account]
}
