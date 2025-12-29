package com.eventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.data.model.Rsvp
import com.eventory.data.model.User
import com.eventory.data.repository.AuthRepository
import com.eventory.data.repository.RsvpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val rsvps: List<Rsvp> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val rsvpRepository: RsvpRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Load user
            val user = authRepository.getCachedUser()
            _state.value = _state.value.copy(user = user)

            // Load RSVPs
            val result = rsvpRepository.getUserRsvps()
            result.fold(
                onSuccess = { rsvps ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        rsvps = rsvps
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

    fun refresh() {
        loadProfile()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
