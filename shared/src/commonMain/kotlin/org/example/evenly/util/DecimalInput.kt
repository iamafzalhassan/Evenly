package org.example.evenly.util

object DecimalInput {
    private const val DECIMAL_SEPARATOR: Char = '.'
    private const val GROUPING_SEPARATOR: Char = ','

    fun format(scaled: Long, fractionDigits: Int): String {
        val scale = scaleFor(fractionDigits)
        val whole = scaled / scale
        val fraction = (scaled % scale).toString().padStart(fractionDigits, '0')
        return if (fractionDigits == 0) whole.toString() else "$whole$DECIMAL_SEPARATOR$fraction"
    }

    fun parse(text: String, fractionDigits: Int, maxWholeDigits: Int): Long? {
        val cleaned = text.filterNot { it == GROUPING_SEPARATOR || it.isWhitespace() }
        val whole = cleaned.substringBefore(DECIMAL_SEPARATOR)
        val fraction = cleaned.substringAfter(DECIMAL_SEPARATOR, missingDelimiterValue = "")
        val isMalformed = cleaned.count { it == DECIMAL_SEPARATOR } > 1 || !whole.all(Char::isDigit) || !fraction.all(Char::isDigit)
        if (isMalformed || (whole.isEmpty() && fraction.isEmpty()) || whole.length > maxWholeDigits || fraction.length > fractionDigits) return null
        return whole.ifEmpty { "0" }.toLong() * scaleFor(fractionDigits) + fraction.padEnd(fractionDigits, '0').ifEmpty { "0" }.toLong()
    }

    fun scaleFor(fractionDigits: Int): Long = (1..fractionDigits).fold(1L) { scale, _ -> scale * 10L }
}
