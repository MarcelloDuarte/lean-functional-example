package com.starcups.rewards

import com.starcups.rewards.Account.{Account, AccountNumber}

trait RewardsRepository {
  def query(no: AccountNumber): Option[Account]
  def store(account: Account): Option[Account]
}
