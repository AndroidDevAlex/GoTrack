package com.example.intermediate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserData
import com.example.intermediate.domain.GetUserByIdUseCase
import com.example.intermediate.domain.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ActionViewModel @Inject constructor(
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val logOutUseCase: LogOutUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    val loggedUser: SharedFlow<UserData?> = getUserByIdUseCase.getUserById()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun logOut(onSuccess: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            logOutUseCase.logOut()
            withContext(mainDispatcher) {
                onSuccess()
            }
        }
    }
}