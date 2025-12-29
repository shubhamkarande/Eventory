package com.eventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.data.model.CreateEventRequest
import com.eventory.data.model.Event
import com.eventory.data.model.EventStats
import com.eventory.data.model.Rsvp
import com.eventory.data.repository.EventRepository
import com.eventory.data.repository.RsvpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrganizerState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvent: Event? = null,
    val eventStats: EventStats? = null,
    val attendees: List<Rsvp> = emptyList(),
    val checkInResult: Rsvp? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class OrganizerViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val rsvpRepository: RsvpRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrganizerState())
    val state: StateFlow<OrganizerState> = _state.asStateFlow()

    init {
        loadOrganizerEvents()
    }

    fun loadOrganizerEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = eventRepository.getOrganizerEvents()
            result.fold(
                onSuccess = { events ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        events = events
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = eventRepository.createEvent(request)
            result.fold(
                onSuccess = { event ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Event created successfully",
                        events = _state.value.events + event
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun updateEvent(eventId: String, request: CreateEventRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = eventRepository.updateEvent(eventId, request)
            result.fold(
                onSuccess = { event ->
                    val updatedEvents = _state.value.events.map { 
                        if (it.id == eventId) event else it 
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Event updated successfully",
                        events = updatedEvents
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = eventRepository.deleteEvent(eventId)
            result.fold(
                onSuccess = {
                    val filteredEvents = _state.value.events.filter { it.id != eventId }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Event deleted",
                        events = filteredEvents
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun loadEventStats(eventId: String) {
        viewModelScope.launch {
            val result = eventRepository.getEventStats(eventId)
            result.fold(
                onSuccess = { stats ->
                    _state.value = _state.value.copy(eventStats = stats)
                },
                onFailure = { /* Ignore stats errors */ }
            )
        }
    }

    fun loadEventAttendees(eventId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = rsvpRepository.getEventAttendees(eventId)
            result.fold(
                onSuccess = { attendees ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        attendees = attendees
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun checkIn(qrCode: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, checkInResult = null)
            val result = rsvpRepository.checkIn(qrCode)
            result.fold(
                onSuccess = { rsvp ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        checkInResult = rsvp,
                        successMessage = "Check-in successful for ${rsvp.userName}"
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Check-in failed"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null, checkInResult = null)
    }
}
