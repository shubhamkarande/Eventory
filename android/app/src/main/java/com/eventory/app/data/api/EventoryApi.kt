package com.eventory.app.data.api

import com.eventory.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface EventoryApi {
    
    @GET("events")
    suspend fun getEvents(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int = 50, // km
        @Query("category") category: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<EventsResponse>
    
    @GET("events/{eventId}")
    suspend fun getEventDetails(@Path("eventId") eventId: String): Response<Event>
    
    @POST("events/{eventId}/rsvp")
    suspend fun rsvpToEvent(
        @Path("eventId") eventId: String,
        @Body rsvpRequest: RSVPRequest
    ): Response<RSVP>
    
    @DELETE("events/{eventId}/rsvp")
    suspend fun cancelRSVP(@Path("eventId") eventId: String): Response<Unit>
    
    @GET("user/rsvps")
    suspend fun getUserRSVPs(): Response<List<RSVP>>
    
    @GET("categories")
    suspend fun getEventCategories(): Response<List<EventCategory>>
    
    @POST("qr/verify")
    suspend fun verifyQRCode(@Body qrVerifyRequest: QRVerifyRequest): Response<QRVerifyResponse>
    
    @GET("user/profile")
    suspend fun getUserProfile(): Response<User>
    
    @PUT("user/profile")
    suspend fun updateUserProfile(@Body user: User): Response<User>
}

data class EventsResponse(
    val events: List<Event>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean
)

data class RSVPRequest(
    val userId: String,
    val reminderSettings: ReminderSettings?
)

data class QRVerifyRequest(
    val qrCode: String,
    val eventId: String
)

data class QRVerifyResponse(
    val valid: Boolean,
    val rsvp: RSVP?,
    val message: String
)