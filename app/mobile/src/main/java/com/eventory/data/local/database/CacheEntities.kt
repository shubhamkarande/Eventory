package com.eventory.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_events")
data class CachedEvent(
    @PrimaryKey
    val id: String,
    val organizerId: String,
    val organizerName: String,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val venueName: String? = null,
    val startTime: String,
    val endTime: String,
    val category: String,
    val isFree: Boolean = true,
    val price: Double = 0.0,
    val maxAttendees: Int? = null,
    val attendeeCount: Long = 0,
    val createdAt: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_rsvps")
data class CachedRsvp(
    @PrimaryKey
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val userId: String,
    val userName: String,
    val qrCode: String,
    val checkedIn: Boolean = false,
    val checkedInAt: String? = null,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
