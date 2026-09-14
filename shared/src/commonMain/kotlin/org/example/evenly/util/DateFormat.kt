package org.example.evenly.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

object DateFormat {
    fun formatShort(instant: Instant): String {
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${date.day} ${MonthNames.ENGLISH_ABBREVIATED.names[date.month.ordinal]} ${date.year}"
    }
}
