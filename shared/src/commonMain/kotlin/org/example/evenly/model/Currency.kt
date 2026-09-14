package org.example.evenly.model

enum class Currency(val minorDigits: Int) {
    EUR(minorDigits = 2),
    GBP(minorDigits = 2),
    LKR(minorDigits = 2),
    USD(minorDigits = 2),
    ;

    companion object {
        fun fromCode(code: String): Currency = entries.firstOrNull { it.name == code } ?: LKR
    }

    val minorScale: Long get() = (1..minorDigits).fold(1L) { scale, _ -> scale * 10L }
}
