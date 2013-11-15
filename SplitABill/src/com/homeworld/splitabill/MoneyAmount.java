package com.homeworld.splitabill;

/* 
 * http://crazymcphee.net/svn/money/
 * 
 * Copyright (c) 2008-2009 Scot Mcphee
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;

public class MoneyAmount implements Money {

    private final BigDecimal value;

    private final Currency currency;

    private final RoundingMode roundingMode;

    MoneyAmount(Currency currency, BigDecimal value, RoundingMode defaultRoundingMode) {
        this.currency = currency;
        this.value = value;
        this.roundingMode = defaultRoundingMode;
    }

    final static Money instance(Currency currency, BigDecimal amount, RoundingMode defaultRoundingMode) {
        return new MoneyAmount(currency,
                amount.setScale(Math.max(currency.getDefaultFractionDigits(), amount.scale())), defaultRoundingMode);
    }

    public BigDecimal value() {
        return value;
    }

    public Currency currency() {
        return currency;
    }

    public Money add(Money augend) {
        if (currency().equals(augend.currency())) {
            return MoneyAmount.instance(currency(), value().add(augend.value()), roundingMode);
        }
        throw new IllegalArgumentException("Currency value of money '" + augend.currency()
                + "' does not match this currency '" + currency() + "'");
    }

    public Money subtract(Money subtrahend) {
        return this.add(subtrahend.negate());
    }

    /**
     * The implementation of this algorithm always distributes the remainder R
     * to the *first* R elements.
     */
    public Money[] proRate(int periods) throws IllegalArgumentException, ArithmeticException {
        if (periods < 1) {
            throw new IllegalArgumentException("Number of periods must be a positive integer.");
        }
        Money[] proRate = new Money[periods];
        BigInteger divisor = BigInteger.valueOf(periods);
        BigInteger unscaledValue = value.unscaledValue();
        BigInteger[] dividendAndRemainder = unscaledValue.divideAndRemainder(divisor);
        BigInteger remainder = dividendAndRemainder[1];
        for (int i = 0; i < periods; i++) {
            // assure that we distribute the remainder in
            // as small as portion across as many of periods
            // as possible, e.g. in increments of one cent for USD.
            BigInteger proRateAmount = dividendAndRemainder[0];
            if (remainder.compareTo(BigInteger.ONE) >= 0) {
                proRateAmount = proRateAmount.add(BigInteger.ONE);
                remainder = remainder.subtract(BigInteger.ONE);
            }
            proRate[i] = MoneyAmount
                    .instance(this.currency, new BigDecimal(proRateAmount, value.scale()), roundingMode);
        }
        if (remainder.compareTo(BigInteger.ZERO) > 0) {
            throw new ArithmeticException("Remainder of '" + remainder
                    + "' should have distributed evenly through all prorata elements!");
        }
        return proRate;
    }

    /**
     * The implementation of this algorithm always distributes the remainder R
     * to the *first* R elements.
     */
    public Money[] proRateWeighted(int[] shareWeightings) throws IllegalArgumentException, ArithmeticException {
        if (shareWeightings == null || shareWeightings.length < 1) {
            throw new IllegalArgumentException("Must be more than one period.");
        }
        int elements = shareWeightings.length;
        int divisor = 0;
        for (int share : shareWeightings) {
            divisor = divisor + share;
        }
        final BigInteger bigDivisor = BigInteger.valueOf(divisor);
        BigInteger[] weightedUnscaledShares = new BigInteger[elements];

        BigInteger remainder = unscaledValueWeightedShareArray(shareWeightings, elements, bigDivisor,
                weightedUnscaledShares);

        if (remainder.intValue() >= elements) {
            throw new ArithmeticException("Remainder of " + remainder
                    + " cannot be greater than or equal to the number (" + elements
                    + ") of elements to be distributed to.");
        }

        Money[] sharesAsMoney = new Money[elements];

        remainder = unscaledValueToWeightedMoneyArray(remainder, weightedUnscaledShares, sharesAsMoney);

        if (remainder.compareTo(BigInteger.ZERO) > 0) {
            throw new ArithmeticException("Reminder of '" + remainder
                    + "' should have distributed evenly through all elements!");
        }
        return sharesAsMoney;
    }

    private BigInteger unscaledValueWeightedShareArray(final int[] shareWeightings, final int elements,
            final BigInteger bigDivisor, final BigInteger[] shares) {
        BigInteger unscaledValue = value.unscaledValue();
        BigInteger remainder = unscaledValue;
        for (int i = 0; i < elements; i++) {
            shares[i] = unscaledValue.multiply(BigInteger.valueOf(shareWeightings[i])).divide(bigDivisor);
            remainder = remainder.subtract(shares[i]);
        }
        return remainder;
    }

    private BigInteger unscaledValueToWeightedMoneyArray(final BigInteger remainder, final BigInteger[] shares,
            final Money[] sharesAsMoney) {
        BigInteger workingRemainder = remainder;
        for (int i = 0; i < shares.length; i++) {
            if (workingRemainder.compareTo(BigInteger.ONE) >= 0) {
                shares[i] = shares[i].add(BigInteger.ONE);
                workingRemainder = workingRemainder.subtract(BigInteger.ONE);
            }
            BigDecimal shareAmountDecimal = new BigDecimal(shares[i], value.scale());
            sharesAsMoney[i] = MoneyAmount.instance(this.currency, shareAmountDecimal, roundingMode);
        }
        return workingRemainder;
    }

    public Money divide(BigDecimal divisor) {
        return divide(divisor, roundingMode);
    }

    public Money divide(BigDecimal divisor, RoundingMode mode) {
        BigDecimal dividend = value.divide(divisor, mode);
        return MoneyAmount.instance(currency, dividend.setScale(this.scale(), mode), mode);
    }

    public Money dividePrecise(BigDecimal divisor) {
        BigDecimal dividend = value.divide(divisor);
        return MoneyAmount.instance(currency, dividend, roundingMode);
    }

    public Money multiply(BigDecimal multiplicand) {
        return multiply(multiplicand, roundingMode);
    }

    public Money multiply(BigDecimal multiplicand, RoundingMode mode) {
        BigDecimal product = value.multiply(multiplicand);
        return MoneyAmount.instance(currency, product.setScale(this.scale(), mode), mode);
    }

    public Money multiplyPrecise(BigDecimal multiplicand) {
        BigDecimal product = value.multiply(multiplicand);
        return MoneyAmount.instance(currency, product, roundingMode);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Money other = (Money) obj;
        return this.currency().equals(other.currency()) && this.value().equals(other.value());
    }

    @Override
    public int hashCode() {
        return ((this.currency().hashCode() + this.value().hashCode()) / 7) * 13;
    }

    public Money negate() {
        return MoneyAmount.instance(currency, value.negate(), roundingMode);
    }

    public int scale() {
        return value.scale();
    }

    public Money round() {
        return round(roundingMode);
    }

    public Money round(RoundingMode mode) {
        return MoneyAmount.instance(currency, value.setScale(currency.getDefaultFractionDigits(), mode), mode);
    }

    public RoundingMode roundingMode() {
        return roundingMode;
    }

    public BigInteger wholeUnits() {
        return value.toBigInteger();
    }

    public BigInteger fractionUnits() {
        return fractions(currency.getDefaultFractionDigits() < 0 ? scale() : currency.getDefaultFractionDigits());
    }

    public BigInteger fractionUnitsPrecise() {
        return fractions(scale());
    }

    private BigInteger fractions(int rescaleValue) {
        return value.subtract(new BigDecimal(value.toBigInteger())).movePointRight(
                rescaleValue).toBigInteger();
    }

}
