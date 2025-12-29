package com.eventory.data.repository

import com.eventory.data.api.EventoryApi
import com.eventory.data.local.TokenManager
import com.eventory.data.model.AuthResponse
import com.eventory.data.model.LoginRequest
import com.eventory.data.model.RegisterRequest
import com.eventory.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: EventoryApi,
    private val tokenManager: TokenManager
) {

    suspend fun register(name: String, email: String, password: String, role: String? = null, interests: String? = null): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password, role, interests))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUser(authResponse.user)
                Result.success(authResponse)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUser(authResponse.user)
                Result.success(authResponse)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                tokenManager.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to fetch user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInterests(interests: String): Result<User> {
        return try {
            val response = api.updateInterests(interests)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                tokenManager.saveUser(user)
                tokenManager.saveInterests(interests)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to update interests"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearAll()
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    suspend fun getCachedUser(): User? = tokenManager.getUser()

    suspend fun isOnboardingComplete(): Boolean = tokenManager.isOnboardingComplete()

    suspend fun setOnboardingComplete(complete: Boolean) = tokenManager.setOnboardingComplete(complete)
}
