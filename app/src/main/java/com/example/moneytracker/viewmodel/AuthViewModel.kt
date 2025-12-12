package com.example.moneytracker.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val TAG = "AuthViewModel"

    private val _uiState = MutableStateFlow(AuthUiState(
        isLoggedIn = authRepository.isLoggedIn,
        userId = authRepository.getCurrentUserId()
    ))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        authRepository.signInWithEmail(email, password)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Email sign in successful: ${result.user?.uid}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = result.user?.uid
                )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Email sign in failed: ${exception.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Sign in failed"
                )
            }
    }

    fun signUpWithEmail(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        authRepository.signUpWithEmail(email, password)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Email sign up successful: ${result.user?.uid}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = result.user?.uid
                )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Email sign up failed: ${exception.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Sign up failed"
                )
            }
    }

    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            Log.d(TAG, "Starting Google Sign-In with webClientId: $webClientId")

            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                Log.d(TAG, "Requesting credential...")
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                
                Log.d(TAG, "Credential received, extracting Google ID token...")
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleIdTokenCredential.idToken
                Log.d(TAG, "Got ID token, signing in with Firebase...")

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = authRepository.signInWithCredential(firebaseCredential)

                Log.d(TAG, "Google Sign-In successful: ${authResult.user?.uid}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = authResult.user?.uid
                )
            } catch (e: NoCredentialException) {
                Log.e(TAG, "No Google accounts available: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No Google accounts found. Please add a Google Account in your device settings."
                )
            } catch (e: GetCredentialCancellationException) {
                Log.e(TAG, "User cancelled: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null // User cancelled, no error message needed
                )
            } catch (e: GetCredentialException) {
                Log.e(TAG, "GetCredentialException: ${e.type} - ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Google Sign-In failed: ${e.message}"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Google Sign-In: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Sign in failed: ${e.message}"
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
