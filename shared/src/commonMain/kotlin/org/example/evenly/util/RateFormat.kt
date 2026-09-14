package org.example.evenly.util

object RateFormat {
    private const val FRACTION_DIGITS: Int = 6
    private const val MAX_WHOLE_DIGITS: Int = 6

    fun formatInput(micros: Long): String = DecimalInput.format(fractionDigits = FRACTION_DIGITS, scaled = micros).trimEnd('0').trimEnd('.')

    fun parseMicros(text: String): Long? = DecimalInput.parse(fractionDigits = FRACTION_DIGITS, maxWholeDigits = MAX_WHOLE_DIGITS, text = text)?.takeIf { it > 0L }
}
