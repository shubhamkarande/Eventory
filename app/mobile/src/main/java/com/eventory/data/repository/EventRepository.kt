package com.eventory.data.repository

import com.eventory.data.api.EventoryApi
import com.eventory.data.model.CreateEventRequest
import com.eventory.data.model.Event
import com.eventory.data.model.EventStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val api: EventoryApi
) {

    suspend fun getEvents(
        lat: Double? = null,
        lng: Double? = null,
        radius: Double? = null,
        category: String? = null
    ): Result<List<Event>> {
        return try {
            val response = api.getEvents(lat, lng, radius, category)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEvent(eventId: String): Result<Event> {
        return try {
            val response = api.getEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Event not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.createEvent(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.updateEvent(eventId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            val response = api.deleteEvent(eventId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrganizerEvents(): Result<List<Event>> {
        return try {
            val response = api.getOrganizerEvents()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch organizer events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventStats(eventId: String): Result<EventStats> {
        return try {
            val response = api.getEventStats(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch event stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
