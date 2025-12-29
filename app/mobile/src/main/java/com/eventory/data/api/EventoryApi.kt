package com.eventory.data.api

import com.eventory.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface EventoryApi {

    // Auth endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<User>

    @PUT("api/auth/interests")
    suspend fun updateInterests(@Body interests: String): Response<User>

    // Event endpoints
    @GET("api/events")
    suspend fun getEvents(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("radius") radius: Double? = null,
        @Query("category") category: String? = null
    ): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEvent(@Path("id") eventId: String): Response<Event>

    @POST("api/events")
    suspend fun createEvent(@Body request: CreateEventRequest): Response<Event>

    @PUT("api/events/{id}")
    suspend fun updateEvent(
        @Path("id") eventId: String,
        @Body request: CreateEventRequest
    ): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") eventId: String): Response<Unit>

    @GET("api/events/organizer")
    suspend fun getOrganizerEvents(): Response<List<Event>>

    @GET("api/events/{eventId}/stats")
    suspend fun getEventStats(@Path("eventId") eventId: String): Response<EventStats>

    // RSVP endpoints
    @POST("api/events/{eventId}/rsvp")
    suspend fun rsvpToEvent(@Path("eventId") eventId: String): Response<Rsvp>

    @GET("api/events/{eventId}/rsvp")
    suspend fun getUserRsvpForEvent(@Path("eventId") eventId: String): Response<Rsvp>

    @DELETE("api/events/{eventId}/rsvp")
    suspend fun cancelRsvp(@Path("eventId") eventId: String): Response<Unit>

    @GET("api/rsvps")
    suspend fun getUserRsvps(): Response<List<Rsvp>>

    @GET("api/events/{eventId}/attendees")
    suspend fun getEventAttendees(@Path("eventId") eventId: String): Response<List<Rsvp>>

    @POST("api/rsvps/checkin")
    suspend fun checkIn(@Body request: Map<String, String>): Response<Rsvp>
}
