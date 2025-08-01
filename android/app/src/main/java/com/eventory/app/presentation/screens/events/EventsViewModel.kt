package com.eventory.app.presentation.screens.events

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.app.data.model.Event
import com.eventory.app.data.model.EventCategory
import com.eventory.app.data.repository.EventRepository
import com.eventory.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    
    private val _state = mutableStateOf(EventsState())
    val state: State<EventsState> = _state
    
    private val _categories = mutableStateOf<List<EventCategory>>(emptyList())
    val categories: State<List<EventCategory>> = _categories
    
    init {
        loadCategories()
    }
    
    fun loadEvents(
        latitude: Double,
        longitude: Double,
        radius: Int = 50,
        category: String? = null,
        refresh: Boolean = false
    ) {
        if (refresh) {
            _state.value = _state.value.copy(
                events = emptyList(),
                isLoading = true,
                error = null
            )
        }
        
        repository.getEvents(
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            category = category,
            page = if (refresh) 0 else _state.value.currentPage
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { response ->
                        _state.value = _state.value.copy(
                            events = if (refresh) response.events else _state.value.events + response.events,
                            isLoading = false,
                            error = null,
                            currentPage = response.currentPage,
                            hasNextPage = response.hasNext,
                            totalPages = response.totalPages
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
                    if (!refresh && _state.value.events.isEmpty()) {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
    
    private fun loadCategories() {
        repository.getEventCategories().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { categories ->
                        _categories.value = categories
                    }
                }
                is Resource.Error -> {
                    // Handle error silently for categories
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
        }.launchIn(viewModelScope)
    }
    
    fun loadMoreEvents(latitude: Double, longitude: Double, category: String? = null) {
        if (_state.value.hasNextPage && !_state.value.isLoading) {
            loadEvents(latitude, longitude, category = category)
        }
    }
    
    fun refreshEvents(latitude: Double, longitude: Double, category: String? = null) {
        loadEvents(latitude, longitude, category = category, refresh = true)
    }
    
    fun filterByCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }
}

data class EventsState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    val selectedCategory: String? = null
)