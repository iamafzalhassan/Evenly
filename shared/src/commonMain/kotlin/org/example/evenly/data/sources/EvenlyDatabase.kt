package org.example.evenly.data.sources

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal const val DATABASE_FILE_NAME: String = "evenly.db"

@Database(entities = [GroupEntity::class, MemberEntity::class], version = 1)
@ConstructedBy(EvenlyDatabaseConstructor::class)
abstract class EvenlyDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
}

internal fun RoomDatabase.Builder<EvenlyDatabase>.buildEvenlyDatabase(): EvenlyDatabase = setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build()

@Suppress("KotlinNoActualForExpect")
expect object EvenlyDatabaseConstructor : RoomDatabaseConstructor<EvenlyDatabase> {
    override fun initialize(): EvenlyDatabase
}
