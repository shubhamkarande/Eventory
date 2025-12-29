package com.eventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.data.model.Event
import com.eventory.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val selectedCategory: String? = null,
    val error: String? = null,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents(lat: Double? = null, lng: Double? = null, radius: Double? = 50.0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = eventRepository.getEvents(lat, lng, radius, _state.value.selectedCategory)
            result.fold(
                onSuccess = { events ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        events = events,
                        filteredEvents = events,
                        userLatitude = lat,
                        userLongitude = lng
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

    fun filterByCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        loadEvents(_state.value.userLatitude, _state.value.userLongitude)
    }

    fun searchEvents(query: String) {
        val events = _state.value.events
        if (query.isBlank()) {
            _state.value = _state.value.copy(filteredEvents = events)
        } else {
            val filtered = events.filter { event ->
                event.title.contains(query, ignoreCase = true) ||
                event.description?.contains(query, ignoreCase = true) == true ||
                event.category.contains(query, ignoreCase = true)
            }
            _state.value = _state.value.copy(filteredEvents = filtered)
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _state.value = _state.value.copy(userLatitude = lat, userLongitude = lng)
        loadEvents(lat, lng)
    }

    fun refresh() {
        loadEvents(_state.value.userLatitude, _state.value.userLongitude)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
