package org.example.evenly.model

import androidx.compose.runtime.Immutable

@Immutable
data class ExchangeRate(val micros: Long, val from: Currency, val to: Currency) {
    companion object {
        const val MICROS_PER_UNIT: Long = 1_000_000L
    }

    fun convert(money: Money): Money {
        require(money.currency == from) { "Expected an amount in $from but got ${money.currency}" }
        require(!money.isNegative) { "Only non-negative amounts are converted" }
        val scaleUp = powerOfTen(to.minorDigits - from.minorDigits)
        val scaleDown = powerOfTen(from.minorDigits - to.minorDigits)
        val fractionProduct = money.minorUnits * (micros % MICROS_PER_UNIT) * scaleUp
        val total = money.minorUnits * (micros / MICROS_PER_UNIT) * scaleUp + fractionProduct / MICROS_PER_UNIT
        val remainderMicros = (total % scaleDown) * MICROS_PER_UNIT + fractionProduct % MICROS_PER_UNIT
        val roundsUp = remainderMicros * 2 >= scaleDown * MICROS_PER_UNIT
        return Money(minorUnits = total / scaleDown + if (roundsUp) 1L else 0L, currency = to)
    }

    private fun powerOfTen(exponent: Int): Long = (1..exponent).fold(1L) { power, _ -> power * 10L }
}
