package com.eventory.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachedEvent::class, CachedRsvp::class],
    version = 1,
    exportSchema = false
)
abstract class EventoryDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun rsvpDao(): RsvpDao
}
