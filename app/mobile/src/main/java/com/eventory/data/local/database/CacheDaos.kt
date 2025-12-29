package com.eventory.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM cached_events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<CachedEvent>>

    @Query("SELECT * FROM cached_events WHERE category = :category ORDER BY startTime ASC")
    fun getEventsByCategory(category: String): Flow<List<CachedEvent>>

    @Query("SELECT * FROM cached_events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): CachedEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CachedEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CachedEvent)

    @Delete
    suspend fun deleteEvent(event: CachedEvent)

    @Query("DELETE FROM cached_events")
    suspend fun clearAllEvents()

    @Query("DELETE FROM cached_events WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
}

@Dao
interface RsvpDao {
    @Query("SELECT * FROM cached_rsvps ORDER BY createdAt DESC")
    fun getAllRsvps(): Flow<List<CachedRsvp>>

    @Query("SELECT * FROM cached_rsvps WHERE eventId = :eventId")
    suspend fun getRsvpByEventId(eventId: String): CachedRsvp?

    @Query("SELECT * FROM cached_rsvps WHERE id = :rsvpId")
    suspend fun getRsvpById(rsvpId: String): CachedRsvp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRsvps(rsvps: List<CachedRsvp>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRsvp(rsvp: CachedRsvp)

    @Delete
    suspend fun deleteRsvp(rsvp: CachedRsvp)

    @Query("DELETE FROM cached_rsvps WHERE eventId = :eventId")
    suspend fun deleteRsvpByEventId(eventId: String)

    @Query("DELETE FROM cached_rsvps")
    suspend fun clearAllRsvps()
}
