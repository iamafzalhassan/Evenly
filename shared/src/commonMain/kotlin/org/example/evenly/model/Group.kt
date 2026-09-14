package org.example.evenly.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class Group(val name: String, val members: List<Member>, val currency: Currency, val id: GroupId, val createdAt: Instant)
