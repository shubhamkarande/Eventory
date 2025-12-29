package com.eventory.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.data.model.Event
import com.eventory.data.model.Rsvp
import com.eventory.data.repository.EventRepository
import com.eventory.data.repository.RsvpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val userRsvp: Rsvp? = null,
    val hasRsvped: Boolean = false,
    val error: String? = null,
    val rsvpSuccess: Boolean = false
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val rsvpRepository: RsvpRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String? = savedStateHandle["eventId"]

    private val _state = MutableStateFlow(EventDetailState())
    val state: StateFlow<EventDetailState> = _state.asStateFlow()

    init {
        eventId?.let { loadEvent(it) }
    }

    fun loadEvent(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val eventResult = eventRepository.getEvent(id)
            eventResult.fold(
                onSuccess = { event ->
                    _state.value = _state.value.copy(event = event)
                    // Check if user has RSVPed
                    checkUserRsvp(id)
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

    private fun checkUserRsvp(eventId: String) {
        viewModelScope.launch {
            val result = rsvpRepository.getUserRsvpForEvent(eventId)
            result.fold(
                onSuccess = { rsvp ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        userRsvp = rsvp,
                        hasRsvped = true
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasRsvped = false
                    )
                }
            )
        }
    }

    fun rsvpToEvent() {
        val event = _state.value.event ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = rsvpRepository.rsvpToEvent(event.id)
            result.fold(
                onSuccess = { rsvp ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        userRsvp = rsvp,
                        hasRsvped = true,
                        rsvpSuccess = true
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

    fun cancelRsvp() {
        val event = _state.value.event ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = rsvpRepository.cancelRsvp(event.id)
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        userRsvp = null,
                        hasRsvped = false
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

    fun clearRsvpSuccess() {
        _state.value = _state.value.copy(rsvpSuccess = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
