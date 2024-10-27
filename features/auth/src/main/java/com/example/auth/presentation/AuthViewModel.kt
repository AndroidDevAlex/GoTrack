package com.example.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthResult
import com.example.auth.domain.GetUserByIdUseCase
import com.example.auth.domain.RegisterUserUseCase
import com.example.auth.domain.SaveUserToDBUseCase
import com.example.auth.domain.SignInUserUseCase
import com.example.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val saveUserToDBUseCase: SaveUserToDBUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val signInUserUseCase: SignInUserUseCase,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult: SharedFlow<AuthResult> = _authResult

    val loggedUser: SharedFlow<UserData?> = getUserByIdUseCase.getUserById()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun registration(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                registerUserUseCase(email, password)
                saveUserToDBUseCase(email)
                _authResult.emit(AuthResult.RegisterResult)
            } catch (e: Exception) {
                _authResult.emit(AuthResult.Error(e))
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                signInUserUseCase(email, password)
                saveUserToDBUseCase(email)
                _authResult.emit(AuthResult.LoginResult)
            } catch (e: Exception) {
                _authResult.emit(AuthResult.Error(e))
            }
        }
    }
}