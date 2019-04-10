package com.starcups.rewards

import java.time.MonthDay
import java.util.UUID

import com.starcups.rewards.Account._
import com.starcups.rewards.domain.{Valid => Correct}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.forAll

object MyGenerators {
  implicit def arbAccountNo: Arbitrary[AccountNumber] = Arbitrary {
    UUID.randomUUID():AccountNumber
  }

  def genMonthDay: Gen[MonthDay] = {
    for {
      d <- Gen.choose(1,28)
      m <- Gen.choose(1,12)
    } yield MonthDay.of(m,d)
  }

  implicit def arbAnniversary: Arbitrary[Anniversary] = Arbitrary {
    genMonthDay.map(Anniversary)
  }
}

object AccountSpecification extends Properties("Account") {
  import MyGenerators._

  val validGreenCardAccount: Gen[Correct[Account]] = for {
    no <- arbitrary[AccountNumber]
    stars <- Gen.posNum[Int]
    anniversary <- arbitrary[Anniversary]
  } yield greenCardAccount(no, stars, anniversary)

  val negativeStarsGreenCardAccount: Gen[Correct[Account]] = for {
    no <- arbitrary[AccountNumber]
    stars <- Gen.negNum[Int]
    anniversary <- arbitrary[Anniversary]
  } yield greenCardAccount(no, stars, anniversary)

  property("Green Card Account creation successful") =
    forAll(validGreenCardAccount)(_.isRight)

  property("Green Card Account cannot be created with negative stars") =
    forAll(negativeStarsGreenCardAccount)(_.isLeft)

  property("Incremented card still has a positive number of stars") =
    forAll(validGreenCardAccount, Gen.negNum[Int]) { (card, stars) =>
      card.flatMap(incrementCard(_, stars))
        .filterOrElse(c => c.stars + stars < 0, notAValidCard).isLeft
    }
}
