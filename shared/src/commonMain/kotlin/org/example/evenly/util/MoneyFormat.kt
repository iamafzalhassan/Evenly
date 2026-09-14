package org.example.evenly.util

import org.example.evenly.model.Currency
import org.example.evenly.model.Money
import kotlin.math.abs

object MoneyFormat {
    private const val DECIMAL_SEPARATOR: Char = '.'
    private const val GROUPING_SEPARATOR: Char = ','

    private const val GROUPING_SIZE: Int = 3
    private const val MAX_WHOLE_DIGITS: Int = 12

    fun format(money: Money): String {
        val currency = money.currency
        val magnitude = abs(money.minorUnits)
        val sign = if (money.isNegative) "-" else ""
        val whole = groupThousands((magnitude / currency.minorScale).toString())
        val fraction = (magnitude % currency.minorScale).toString().padStart(currency.minorDigits, '0')
        return if (currency.minorDigits == 0) "$sign${currency.name} $whole" else "$sign${currency.name} $whole$DECIMAL_SEPARATOR$fraction"
    }

    fun formatInput(minorUnits: Long, currency: Currency): String {
        val whole = minorUnits / currency.minorScale
        val fraction = (minorUnits % currency.minorScale).toString().padStart(currency.minorDigits, '0')
        return if (currency.minorDigits == 0) whole.toString() else "$whole$DECIMAL_SEPARATOR$fraction"
    }

    fun parseMinorUnits(text: String, currency: Currency): Long? {
        val cleaned = text.filterNot { it == GROUPING_SEPARATOR || it.isWhitespace() }
        val whole = cleaned.substringBefore(DECIMAL_SEPARATOR)
        val fraction = cleaned.substringAfter(DECIMAL_SEPARATOR, missingDelimiterValue = "")
        val isMalformed = cleaned.count { it == DECIMAL_SEPARATOR } > 1 || !whole.all(Char::isDigit) || !fraction.all(Char::isDigit)
        if (isMalformed || (whole.isEmpty() && fraction.isEmpty()) || whole.length > MAX_WHOLE_DIGITS || fraction.length > currency.minorDigits) return null
        return whole.ifEmpty { "0" }.toLong() * currency.minorScale + fraction.padEnd(currency.minorDigits, '0').ifEmpty { "0" }.toLong()
    }

    private fun groupThousands(digits: String): String = digits.reversed().chunked(GROUPING_SIZE).joinToString(GROUPING_SEPARATOR.toString()).reversed()
}
