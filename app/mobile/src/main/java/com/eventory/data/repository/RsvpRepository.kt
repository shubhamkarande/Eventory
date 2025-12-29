package com.eventory.data.repository

import com.eventory.data.api.EventoryApi
import com.eventory.data.model.Rsvp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RsvpRepository @Inject constructor(
    private val api: EventoryApi
) {

    suspend fun rsvpToEvent(eventId: String): Result<Rsvp> {
        return try {
            val response = api.rsvpToEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to RSVP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRsvpForEvent(eventId: String): Result<Rsvp> {
        return try {
            val response = api.getUserRsvpForEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("RSVP not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRsvp(eventId: String): Result<Unit> {
        return try {
            val response = api.cancelRsvp(eventId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel RSVP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRsvps(): Result<List<Rsvp>> {
        return try {
            val response = api.getUserRsvps()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch RSVPs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventAttendees(eventId: String): Result<List<Rsvp>> {
        return try {
            val response = api.getEventAttendees(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch attendees"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkIn(qrCode: String): Result<Rsvp> {
        return try {
            val response = api.checkIn(mapOf("qrCode" to qrCode))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Invalid QR code or already checked in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
