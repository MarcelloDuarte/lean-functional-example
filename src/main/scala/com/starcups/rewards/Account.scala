package com.starcups.rewards

import java.time.MonthDay
import java.util.UUID

import cats.data.NonEmptyList
import cats.implicits._
import com.starcups.rewards.domain._
import com.starcups.rewards.domain.RewardsError

import scala.util.Try

object Account {
  sealed case class AccountError(message: String) extends RewardsError
  final object NotAValidCard extends AccountError("Not a valid Card")
  final object NegativeNumberOfStars extends AccountError("Negative number of stars")
  final object NotEnoughStars extends AccountError("Not enough stars")
  final object NotAValidUUID extends AccountError("Not a valid UUID")

  val notAValidCard = Left(NonEmptyList.one(NotAValidCard))
  val negativeNumberOfStars = Left(NonEmptyList.one(NegativeNumberOfStars))

  type AccountNumber = UUID
  type Stars = Int
  final case class Anniversary(date: MonthDay)

  sealed trait Account {
    def no: AccountNumber
    def stars: Stars
    def anniversary: Anniversary
  }

  sealed abstract case class GreenCard(no: AccountNumber, stars: Stars, anniversary: Anniversary) extends Account
  sealed abstract case class GoldCard(no: AccountNumber, stars: Stars, anniversary: Anniversary) extends Account
  sealed abstract case class InactiveCard(no: AccountNumber, stars: Stars, anniversary: Anniversary) extends Account

  def incrementCard(account: Account, quantity: Int): Valid[Account] = {
    if (quantity < 0) negativeNumberOfStars
    else account match {
      case GreenCard(n,s,a) => greenCardAccount(n,s + quantity,a)
      case GoldCard(n,s,a) => goldCardAccount(n,s + quantity,a)
      case _ => notAValidCard
    }
  }

  def newCard: Valid[GreenCard] = {
    Right(new GreenCard(UUID.randomUUID, 0, Anniversary(MonthDay.now)) {})
  }

  def greenCardAccount(no: AccountNumber, stars: Stars, anniversary: Anniversary): Valid[GreenCard] = {
    (validateAccountNo(no),
      validatesPositiveNumberOfStars(stars),
      validatesAnniversary(anniversary)).parMapN(new GreenCard(_, _, _) {})
  }

  def goldCardAccount(no: AccountNumber, stars: Stars, anniversary: Anniversary): Valid[GoldCard] = {
    (validateAccountNo(no),
      validatesPositiveNumberOfStars(stars),
      validatesAnniversary(anniversary)).parMapN(new GoldCard(_, _, _) {})
  }

  private def validatesAnniversary(a: Anniversary): Valid[Anniversary] = Right(a)

  private def validateAccountNo(no: AccountNumber): Valid[AccountNumber] =
    Try(UUID.fromString(no.toString)).toEither match {
      case Right(uuid) => Right(uuid)
      case Left(_) => Left(NonEmptyList.one(NotAValidUUID))
    }

  private def validatesStars(account: Account): Valid[Stars] = for {
    _ <- validatesEnoughStarsForAClaim(account)
    _ <- validatesPositiveNumberOfStars(account.stars)
  } yield account.stars

  private def validatesPositiveNumberOfStars(stars: Stars): Valid[Stars] = {
    if (stars < 0) Left(NonEmptyList.one(NegativeNumberOfStars))
    else Right(stars)
  }

  private def validatesEnoughStarsForAClaim(account: Account): Valid[Stars] = account match {
    case GreenCard(_, stars, _) if stars >= 15 => Right(stars)
    case GoldCard(_, stars, _) if stars >= 10 => Right(stars)
    case _ => Left(NonEmptyList.one(NotEnoughStars))
  }
}
