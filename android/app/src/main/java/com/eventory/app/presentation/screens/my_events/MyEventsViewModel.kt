package com.eventory.app.presentation.screens.my_events

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.app.data.model.Event
import com.eventory.app.data.repository.EventRepository
import com.eventory.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    
    private val _state = mutableStateOf(MyEventsState())
    val state: State<MyEventsState> = _state
    
    fun loadMyEvents() {
        repository.getUserRSVPs().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { rsvps ->
                        // TODO: Convert RSVPs to Events - this would require additional API call
                        // For now, we'll show empty state
                        _state.value = _state.value.copy(
                            events = emptyList(), // TODO: Map RSVPs to Events
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
}

data class MyEventsState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)