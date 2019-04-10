package com.starcups.rewards.interpreter

import com.starcups.rewards.Account.{Account, AccountNumber}
import com.starcups.rewards.RewardsRepository

class InMemRewardsRepo extends RewardsRepository {
  var accounts = Map.empty[AccountNumber, Account]

  override def query(no: AccountNumber): Option[Account] = {
    accounts.get(no)
  }
  override def store(account: Account): Option[Account] = {
    accounts = accounts + (account.no -> account)
    Some(account)
  }
}
