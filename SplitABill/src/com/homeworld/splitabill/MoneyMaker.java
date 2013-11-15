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
import java.math.RoundingMode;
import java.util.Currency;

/**
 * A basic factory for constructing instances of Money.
 * @author smcphee
 *
 */
public final class MoneyMaker {
    
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private MoneyMaker() {
        // No instances please
    }

    /**
     * Makes a money instance with the currency and the value, with a default rounding mode of BANKERS_ROUNDING,
     * and a scale that is the max of the currency default and the value's acutal scale.
     * @param currency The currency of the Money.
     * @param value The amount of the Money.
     * @return and instance of Money.
     */
    public static Money makeMoney(Currency currency, BigDecimal value) {
        return MoneyAmount.instance(currency, value, DEFAULT_ROUNDING);
    }
    
    /**
     * Makes a money instance with the currency and the value, a default rounding mode that has been specified,
     * and a scale that is the max of the currency default and the value's actual scale. The rounding mode is used
     * when money.round() is called, which returns a new Money instance with the Currency's default scale, rounded 
     * according to the default rounding mode given (N.B. the money interface has another round method that allows you
     * pass in an ad-hoc rounding mode).
     * @param currency The currency of the Money.
     * @param value The amount of the Money.
     * @param defaultRoundingMode the rounding mode this money instance should use by default.
     * @return an instance of Money.
     */
    public static Money makeMoney(Currency currency, BigDecimal amount, RoundingMode defaultRoundingMode) {
        return MoneyAmount.instance(currency, amount, defaultRoundingMode);
    }
    

}
