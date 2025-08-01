package com.eventory.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val startDateTime: Date,
    val endDateTime: Date,
    val venue: Venue,
    val category: EventCategory,
    val organizerId: String,
    val organizerName: String,
    val maxAttendees: Int?,
    val currentAttendees: Int,
    val price: Double?,
    val tags: List<String>,
    val isRsvped: Boolean = false
) : Parcelable

@Parcelize
data class Venue(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val state: String,
    val zipCode: String
) : Parcelable

@Parcelize
data class EventCategory(
    val id: String,
    val name: String,
    val color: String,
    val icon: String?
) : Parcelable

@Parcelize
data class RSVP(
    val id: String,
    val eventId: String,
    val userId: String,
    val qrCode: String,
    val rsvpDateTime: Date,
    val status: RSVPStatus,
    val reminderSettings: ReminderSettings?
) : Parcelable

@Parcelize
data class ReminderSettings(
    val enabled: Boolean,
    val minutesBefore: Int // 30, 60, 1440 (1 day)
) : Parcelable

enum class RSVPStatus {
    CONFIRMED,
    CANCELLED,
    CHECKED_IN
}

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val location: UserLocation?
) : Parcelable

@Parcelize
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String?,
    val state: String?
) : Parcelable