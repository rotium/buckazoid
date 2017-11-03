package com.vatbox.money

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global
import com.vatbox.money.Generators._


class DslSpec extends UnitSpec {
  "MoneyExchange" should {
    "adding money" in {
      forAll { (m1: Money[_ <: Currency.Key], m2: Money[_ <: Currency.Key], m3: Money[_ <: Currency.Key], m4: Money[_ <: Currency.Key]) ⇒
        whenever(m1.currency != m2.currency && m1.currency != m3.currency && m1.currency != m4.currency) {
          val me1 = m1 + m2
          val me2 = m3 + m4
          whenReady(me1 + me2 at Instant.now) {
            _ should equal(Money(m1.amount + (fakeRatio * m2.amount) + (fakeRatio * m3.amount) + (fakeRatio * m4.amount), m1.currency))
          }
        }
      }
    }

    "substracting money" in {
      forAll { (m1: Money[_ <: Currency.Key], m2: Money[_ <: Currency.Key], m3: Money[_ <: Currency.Key], m4: Money[_ <: Currency.Key]) ⇒
        whenever(m1.currency != m2.currency && m1.currency != m3.currency && m1.currency != m4.currency) {
          val me1 = m1 + m2
          val me2 = m3 + m4
          whenReady(me1 - me2 at Instant.now) {
            _ should equal(Money(m1.amount + (fakeRatio * m2.amount) - (fakeRatio * m3.amount) - (fakeRatio * m4.amount), m1.currency))
          }
        }
      }
    }

    "added to Money" in {
      forAll { (m1: Money[_ <: Currency.Key], m2: Money[_ <: Currency.Key], m3: Money[_ <: Currency.Key]) ⇒
        whenever(m1.currency != m2.currency && m1.currency != m3.currency) {
          val me = m2 + m3
          whenReady(m1 + me at Instant.now) {
            _ should equal(Money(m1.amount + (fakeRatio * m2.amount) + (fakeRatio * m3.amount), m1.currency))
          }
        }
      }
    }
    "substracted from Money" in {
      forAll { (m1: Money[_ <: Currency.Key], m2: Money[_ <: Currency.Key], m3: Money[_ <: Currency.Key]) ⇒
        whenever(m1.currency != m2.currency && m1.currency != m3.currency) {
          val me = m2 + m3
          whenReady(m1 - me at Instant.now) {
            _ should equal(Money(m1.amount - (fakeRatio * m2.amount) - (fakeRatio * m3.amount), m1.currency))
          }
        }
      }
    }

    "allow convert to other currency" in {
      forAll { (m1: Money[_ <: Currency.Key], m2: Money[_ <: Currency.Key], c1: Currency {type Key <: Currency.Key}) ⇒
        whenever(m1.currency != m2.currency && m1.currency != c1) {
          val me1 = m1 + m2
          whenReady(me1 in c1 at Instant.now) {
            _ should equal(Money((fakeRatio * m1.amount) + (fakeRatio * m2.amount), c1))
          }
        }
      }
    }
  }

  "CurrencyExchange" should {
    "convert amount between currencies" in {
      forAll { (c1: Currency {type Key <: Currency.Key}, c2: Currency {type Key <: Currency.Key}, amount: BigDecimal) ⇒
        whenever(c1 != c2) {
          val ce = c1 to c2
          whenReady(ce convert (amount) at Instant.now) {
            _ should equal(Money((fakeRatio * amount), c2))
          }
        }
      }
    }
  }
}
