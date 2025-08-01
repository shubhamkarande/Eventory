package com.eventory.app.data.repository

import com.eventory.app.data.api.EventoryApi
import com.eventory.app.data.api.EventsResponse
import com.eventory.app.data.api.RSVPRequest
import com.eventory.app.data.model.*
import com.eventory.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val api: EventoryApi
) {
    
    fun getEvents(
        latitude: Double,
        longitude: Double,
        radius: Int = 50,
        category: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 0
    ): Flow<Resource<EventsResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getEvents(latitude, longitude, radius, category, startDate, endDate, page)
            if (response.isSuccessful) {
                response.body()?.let { eventsResponse ->
                    emit(Resource.Success(eventsResponse))
                } ?: emit(Resource.Error("No data received"))
            } else {
                emit(Resource.Error("Failed to fetch events: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
    
    fun getEventDetails(eventId: String): Flow<Resource<Event>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getEventDetails(eventId)
            if (response.isSuccessful) {
                response.body()?.let { event ->
                    emit(Resource.Success(event))
                } ?: emit(Resource.Error("Event not found"))
            } else {
                emit(Resource.Error("Failed to fetch event details: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
    
    fun rsvpToEvent(
        eventId: String,
        userId: String,
        reminderSettings: ReminderSettings?
    ): Flow<Resource<RSVP>> = flow {
        try {
            emit(Resource.Loading())
            val request = RSVPRequest(userId, reminderSettings)
            val response = api.rsvpToEvent(eventId, request)
            if (response.isSuccessful) {
                response.body()?.let { rsvp ->
                    emit(Resource.Success(rsvp))
                } ?: emit(Resource.Error("Failed to create RSVP"))
            } else {
                emit(Resource.Error("RSVP failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
    
    fun cancelRSVP(eventId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.cancelRSVP(eventId)
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Failed to cancel RSVP: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
    
    fun getUserRSVPs(): Flow<Resource<List<RSVP>>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getUserRSVPs()
            if (response.isSuccessful) {
                response.body()?.let { rsvps ->
                    emit(Resource.Success(rsvps))
                } ?: emit(Resource.Error("No RSVPs found"))
            } else {
                emit(Resource.Error("Failed to fetch RSVPs: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
    
    fun getEventCategories(): Flow<Resource<List<EventCategory>>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getEventCategories()
            if (response.isSuccessful) {
                response.body()?.let { categories ->
                    emit(Resource.Success(categories))
                } ?: emit(Resource.Error("No categories found"))
            } else {
                emit(Resource.Error("Failed to fetch categories: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        }
    }
}