package org.example.evenly

import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import org.example.evenly.data.sources.DATABASE_FILE_NAME
import org.example.evenly.data.sources.EvenlyDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun createAppGraph(): AppGraph = AppGraph(Room.databaseBuilder<EvenlyDatabase>(name = "${documentDirectory()}/$DATABASE_FILE_NAME"))

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(directory = NSDocumentDirectory, inDomain = NSUserDomainMask, appropriateForURL = null, create = false, error = null)
    return requireNotNull(url?.path)
}
