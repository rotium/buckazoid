# Buckazoid
**Sensible money library**

Named after [SpaceQuest money](http://spacequest.wikia.com/wiki/Buckazoid).

Scala library providing Money Type safe DLS, exchange rate conversion with Dates with support for external services calls, easy money arithmetic and more.

[ ![Download](https://api.bintray.com/packages/vatbox-oss/maven/buckazoid/images/download.svg) ](https://bintray.com/vatbox-oss/maven/buckazoid/_latestVersion)
[![CircleCI](https://circleci.com/gh/VATBox/buckazoid.svg?style=svg)](https://circleci.com/gh/VATBox/buckazoid)

## Installation
To use Buckazoid in your SBT project add the following dependency to your build.


    "com.vatbox"  %% "buckazoid"  % "0.0.8"


To use Buckazoid in your Maven / Gradle project look at the [bintray repository](https://bintray.com/vatbox-oss/maven/buckazoid)


## Usage
```scala
scala> import com.vatbox.money._
       import java.time.Instant
       import scala.concurrent.Future
       import scala.concurrent._
       import scala.concurrent.duration._
       import scala.concurrent.ExecutionContext.Implicits.global


scala> USD(10)
res0: com.vatbox.money.Money[com.vatbox.money.USD.Key] = 10 USD

scala> EUR(50)
res1: com.vatbox.money.Money[com.vatbox.money.EUR.Key] = 50 EUR

scala> 5(USD) + 10(USD) * 2 + USD(20) + Money(5, USD)
res2: com.vatbox.money.Money[com.vatbox.money.USD.Key] = 50 USD

scala> implicit object ExchangeRateDemo extends ExchangeRate {
         override def convert(base: Currency, counter: Currency, amount: BigDecimal, exchangeDate: Instant): Future[BigDecimal] = {
           (base, counter) match {
             case (f, b) if f == b ⇒ Future.successful { amount }
             case (ILS, USD) ⇒ convert(USD, ILS, 1, exchangeDate) map (1 / _ * amount)
             case (USD, ILS) ⇒ Future.successful { 5 * amount }
             case (ILS, EUR) ⇒ convert(EUR, ILS, 1, exchangeDate) map (1 / _ * amount)
             case (EUR, ILS) ⇒ Future.successful { 10 * amount }
             case (EUR, USD) ⇒ convert(EUR, ILS, 1, exchangeDate) flatMap {ratio ⇒ convert(ILS, USD, ratio * amount, exchangeDate)}
             case (USD, EUR) ⇒ convert(USD, ILS, 1, exchangeDate) flatMap {ratio ⇒ convert(ILS, EUR, ratio * amount, exchangeDate)}
             case (Currency("TST",_,_,_), USD) ⇒ Future.successful { 99 * amount }
             case (USD, Currency("TST",_,_,_)) ⇒ convert(Currency("TST"), USD, 1, exchangeDate) map (1 / _ * amount)
             case _ ⇒ Future.failed {
               ExchangeRateException(s"Fail to convert $base to $counter")
             }
           }
         }
       }
defined object ExchangeRateDemo

scala> val total = EUR(5) + USD(10) + ILS(20) at Instant.now foreach println
total: Unit = ()
12.0 EUR

scala> val totalinILS = EUR(5) + USD(10) + ILS(20) in ILS at Instant.now foreach println
totalinILS: Unit = ()
120 ILS

scala> val usd2eur = USD to EUR at Instant.now
usd2eur: com.vatbox.money.CurrencyExchangeRate[com.vatbox.money.USD.Key,com.vatbox.money.EUR.Key] = CurrencyExchangeRate(Currency(USD,US Dollar,$,2),Currency(EUR,Euro,€,2),2017-11-03T06:41:01.336Z)

scala> usd2eur convert (10) foreach println
5.0 EUR

scala> usd2eur.rate foreach println
0.5


scala> val eur2usd = usd2eur.inverse
eur2usd: com.vatbox.money.CurrencyExchangeRate[com.vatbox.money.EUR.Key,com.vatbox.money.USD.Key] = CurrencyExchangeRate(Currency(EUR,Euro,€,2),Currency(USD,US Dollar,$,2),2017-11-03T06:41:01.336Z)
       
scala> eur2usd.rate foreach println
2.0


scala> val handerEurToUSD = 100(EUR) in USD
handerEurToUSD: com.vatbox.money.MoneyExchange[com.vatbox.money.USD.Key] = MoneyExchange(Currency(USD,US Dollar,$,2),List(100 EUR))

scala> handerEurToUSD at Instant.now foreach println
200.0 USD

```

## Other money libraries
- [lambdista/money](https://github.com/lambdista/money)
- [squants](https://github.com/typelevel/squants)
- [joda-money](http://www.joda.org/joda-money/)
