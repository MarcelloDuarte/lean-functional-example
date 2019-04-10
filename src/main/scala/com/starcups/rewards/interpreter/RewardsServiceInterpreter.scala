package com.starcups.rewards.interpreter

import cats.data.{Kleisli, NonEmptyList}
import com.starcups.rewards.domain.{DbError, Valid}
import com.starcups.rewards.Account._
import com.starcups.rewards.Order._
import com.starcups.rewards.{RewardsRepository, RewardsService}

class RewardsServiceInterpreter extends RewardsService {
  lazy val dbError: Valid[Account] = Left(NonEmptyList.one(DbError))
  type RewardsOperation[A] = Kleisli[Valid, RewardsRepository, A]

  override def collectStars(no: AccountNumber, order: Order) : Kleisli[Valid, RewardsRepository, Account] = Kleisli { repo =>
    repo.query(no) match {
      case Some(acc) => for {
          account <- incrementCard(acc, drinksInOrder(order)).map(repo.store) match {
            case Right(Some(a)) => Right(a)
            case Right(None) => dbError
            case Left(err) => Left(err)
          }
        } yield account
      case None => dbError
    }
  }
}
