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

    fun formatInput(minorUnits: Long, currency: Currency): String = DecimalInput.format(fractionDigits = currency.minorDigits, scaled = minorUnits)

    fun parseMinorUnits(text: String, currency: Currency): Long? = DecimalInput.parse(fractionDigits = currency.minorDigits, maxWholeDigits = MAX_WHOLE_DIGITS, text = text)

    private fun groupThousands(digits: String): String = digits.reversed().chunked(GROUPING_SIZE).joinToString(GROUPING_SEPARATOR.toString()).reversed()
}
