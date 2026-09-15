package org.example.evenly.util

import kotlinx.coroutines.CancellationException

suspend fun succeeds(action: suspend () -> Unit): Boolean = try {
    action()
    true
} catch (exception: CancellationException) {
    throw exception
} catch (exception: Exception) {
    false
}
