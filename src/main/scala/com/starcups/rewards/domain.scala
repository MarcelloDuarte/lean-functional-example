package com.starcups.rewards

import cats.data.EitherNel

object domain {
  trait Error
  trait RewardsError extends Error {
    def message: String
  }
  case object DbError extends Error

  type Valid[A] = EitherNel[Error, A]
}
