package org.example.evenly.model

import androidx.compose.runtime.Immutable

sealed interface SplitRule {
    @Immutable
    data class Equal(val participants: List<MemberId>) : SplitRule

    @Immutable
    data class Exact(val minorUnits: Map<MemberId, Long>) : SplitRule

    @Immutable
    data class Percentage(val basisPoints: Map<MemberId, Int>) : SplitRule
}
