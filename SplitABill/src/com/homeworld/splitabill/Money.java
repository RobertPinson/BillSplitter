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
package com.homeworld.splitabill;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * This interface represents Money. Money always has a currency and a value. Implementations should be immutable.
 * When an instance of Money needs to construct another instance resulting from an operation e.g. division, the new
 * new immutable instance should have it's default rounding mode set to the rounding mode used to derive it.
 * @author scot.mcphee@gmail.com
 */
public interface Money {

    /**
     * The numerical value of the Money. 
     * @return the value of the money as a BigDecimal.
     */
    BigDecimal value();
    
    /**
     * The Currency of the Money. 
     * @return java.util.Currency
     */
    Currency currency();

    /**
     * Add some Money to this Money and return the new Money. Generally implementations should pay attention to 
     * the Currency, e.g. reject Money with a different Currency to this.  The new immutable instance should have 
     * it's default rounding mode set to the rounding mode used to derive it.
     * @param money The Money to be added to this Money.
     * @return The new Money value.
     */
    Money add(Money money);
    
    /**
     * Divide the money into as equal portions as possible. In practice this means the modulo is distributed  in the
     * smallest possible units among the first (or last) elements, e.g.AUD10 into 3 periods returns {3.34, 3.33, 3.33}. 
     * JPY10 into 3 periods is {4, 3, 3} (Yen are whole units only).
     *  
     * The sum of all of the elements of the returned array should exactly equal the value of the original money 
     * instance (without rounding errors).
     *  
     * @param periods a positive integer - the number of periods to divide the money into 
     * @return an array of Money with size == periods.
     */
    Money[] proRate(int periods);
    
    /**
     * This method divides the money into shareweighting.length pieces with the amount weighted according
     * to the integer amount, for example, $30 weighted {1, 2} results in {$10, $20).
     */
    Money[] proRateWeighted(int[] shareWeightings);


    /**
     * Divide the money by the divisor, using a default rounding scheme. The new immutable instance should have 
     * it's default rounding mode set to the rounding mode used to derive it.
     * @param divisor the amount this money is to be divided by.
     * @return the new Money value, rounded to a default rounding mode.
     */
    Money divide(BigDecimal divisor);

   /**
    * Divide the money by the divisor, using the supplied rounding scheme. The new immutable instance should have 
     * it's default rounding mode set to the rounding mode used to derive it.
    * @param divisor the amount this money is to be divided by.
    * @param mode the rounding mode to use.
    * @return the new Money value, rounded according to the rule passed in.
    */
    Money divide(BigDecimal divisor, RoundingMode mode);

    /**
     * Divide the money by the divisor, without rounding. Difference.value() has as much precision as needed. 
     * The new immutable instance should have it's default rounding mode set to the original rounding mode.
     * @param divisor the amount this money is to be divided by.
     * @return the new Money value, not rounded.
     */
    Money dividePrecise(BigDecimal divisor);

    /**
     * Substract money from this money and return a new amount of money representing the remainder.
     * @param subtrahend the amount of money to be substracted from this Money. The new immutable instance should have 
     * it's default rounding mode set to the rounding mode used to derive it.
     * @return left over Money.
     */
    Money subtract(Money subtrahend);

    /**
     * Negate money amount. In other words, turn $1.23 into ($1.23) or -1.23 dollars. The new immutable instance 
     * should have it's default rounding mode set to the rounding mode used to derive it.
     * @return Money, negated.
     */
    Money negate();

    /**
     * Multiply money and return a new amount of money using the default rounding mode The new immutable instance 
     * should have it's default rounding mode set to the rounding mode used to derive it... 
     * E.g. $5.00 multiplied by 3 is $15.00.
     * @param multiplicand The amount to multiply money by.
     * @return the new amount of money.
     */
    Money multiply(BigDecimal multiplicand);

    /**
     * Multiple money and return a new amount of money using the specified rounding mode. The new immutable instance 
     * should have it's default rounding mode set to the rounding mode used to derive it.
     * @param multiplicand the amount to multiply money by.
     * @param mode the rounding mode (see java.math.RoundingMode)
     * @return new amount of money, rounded according to RoundingMode.
     */
    Money multiply(BigDecimal multiplicand, RoundingMode mode);

    /**
     * Multiply the money by the multiplicand, without rounding. The new immutable instance should have 
     * it's default rounding mode set to the original rounding mode.
     * @param multiplicand the amount to multiply by.
     * @return new Money.
     */
    Money multiplyPrecise(BigDecimal multiplicand);

    /**
     * Get the scale of this Money instance.
     * @return the scale, e.g. if set to default, in American dollars, 2, if Japanese Yen, 0.
     */
    int scale();

    /**
     * Returns a new amount of Money rounded to the default rounding, which is HALF_EVEN.
     * @return new Money appropriately rounded.
     */
    Money round();

    /**
     * Returns a new amount of Money rounded to the specified mode.
     * @param mode the rounding mode to use.
     * @return new Money appropriately rounded.
     */
    Money round(RoundingMode mode);

    /**
     * The default rounding mode of the Money amount.
     * @return the rounding mode used in numerical operations where no other rounding is specified and rounding
     * may be required (e.g. division, explicit rounding etc).
     */
    RoundingMode roundingMode();

    /**
     * The whole number of units of the Money amount, e.g. $5.99 returns 5. 
     * No rounding is applied, and implementations may inherit the limitations
     * of BigDecimal.longValue().
     * @return the whole number of units in the Money amount.
     */
    BigInteger wholeUnits();

    /**
     * The fractional amount of the Money amount, rounded to the nearest
     * minor unit of the underlying Currency, e.g. $5.99 returns 99, or $5.5595
     * returns 56 if normal rounding is applied. If the currency has no fractional
     * units (i.e. Yen), always returns zero.
     * @return the whole number of minor units for the underlying currency, 
     * i.e. the fractional component, in the Money amount.
     */
    BigInteger fractionUnits();
    
    /**
     * The precise amount of fractional units, irrespective of the minor unit.
     * E.g. $5.99 will return 99, but $5.9995 (ie. 5 dollars 99 cents and 95 micro-cents)
     * will return 9995.
     * @return the entire fractional amount in the Money amount.
     */
    BigInteger fractionUnitsPrecise();
    
}
