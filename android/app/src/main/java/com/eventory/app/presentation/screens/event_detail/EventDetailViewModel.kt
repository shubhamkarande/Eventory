package com.eventory.app.presentation.screens.event_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.app.data.model.Event
import com.eventory.app.data.model.RSVP
import com.eventory.app.data.model.ReminderSettings
import com.eventory.app.data.repository.EventRepository
import com.eventory.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    
    private val _state = mutableStateOf(EventDetailState())
    val state: State<EventDetailState> = _state
    
    fun loadEventDetails(eventId: String) {
        repository.getEventDetails(eventId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { event ->
                        _state.value = _state.value.copy(
                            event = event,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
    
    fun rsvpToEvent(
        eventId: String,
        userId: String,
        reminderMinutes: Int = 60
    ) {
        val reminderSettings = ReminderSettings(
            enabled = true,
            minutesBefore = reminderMinutes
        )
        
        repository.rsvpToEvent(eventId, userId, reminderSettings).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { rsvp ->
                        _state.value = _state.value.copy(
                            rsvp = rsvp,
                            isRsvpLoading = false,
                            rsvpError = null,
                            event = _state.value.event?.copy(
                                isRsvped = true,
                                currentAttendees = _state.value.event!!.currentAttendees + 1
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isRsvpLoading = false,
                        rsvpError = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isRsvpLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
    
    fun cancelRSVP(eventId: String) {
        repository.cancelRSVP(eventId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        rsvp = null,
                        isRsvpLoading = false,
                        rsvpError = null,
                        event = _state.value.event?.copy(
                            isRsvped = false,
                            currentAttendees = maxOf(0, _state.value.event!!.currentAttendees - 1)
                        )
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isRsvpLoading = false,
                        rsvpError = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isRsvpLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
    
    fun clearRsvpError() {
        _state.value = _state.value.copy(rsvpError = null)
    }
}

data class EventDetailState(
    val event: Event? = null,
    val rsvp: RSVP? = null,
    val isLoading: Boolean = false,
    val isRsvpLoading: Boolean = false,
    val error: String? = null,
    val rsvpError: String? = null
)