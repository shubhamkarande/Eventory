package com.eventory.data.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val interests: String? = null
)

data class Event(
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
    val createdAt: String? = null
)

data class Rsvp(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val userId: String,
    val userName: String,
    val qrCode: String,
    val checkedIn: Boolean = false,
    val checkedInAt: String? = null,
    val createdAt: String
)

// Auth DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String? = null,
    val interests: String? = null
)

data class AuthResponse(
    val token: String,
    val user: User
)

// Event DTOs
data class CreateEventRequest(
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
    val maxAttendees: Int? = null
)

data class EventStats(
    val totalRsvps: Long,
    val checkedIn: Long
)
