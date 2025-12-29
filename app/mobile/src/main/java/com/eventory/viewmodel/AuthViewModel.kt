package com.eventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventory.data.model.User
import com.eventory.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isOnboardingComplete: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            val user = authRepository.getCachedUser()
            val isOnboardingComplete = authRepository.isOnboardingComplete()
            _state.value = _state.value.copy(
                isLoggedIn = isLoggedIn,
                user = user,
                isOnboardingComplete = isOnboardingComplete
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { auth ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = auth.user
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, role: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.register(name, email, password, role)
            result.fold(
                onSuccess = { auth ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = auth.user
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun updateInterests(interests: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = authRepository.updateInterests(interests)
            result.fold(
                onSuccess = { user ->
                    authRepository.setOnboardingComplete(true)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = user,
                        isOnboardingComplete = true
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

    fun setOnboardingComplete() {
        viewModelScope.launch {
            authRepository.setOnboardingComplete(true)
            _state.value = _state.value.copy(isOnboardingComplete = true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = AuthState()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
