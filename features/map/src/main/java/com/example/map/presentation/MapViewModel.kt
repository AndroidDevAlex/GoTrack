package com.example.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.map.CoordinatesResult
import com.example.map.domain.GetCoordinatesUseCase
import com.example.map.domain.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MapViewModel @Inject constructor(
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val logOutUseCase: LogOutUseCase,
    private val getCoordinatesUseCase: GetCoordinatesUseCase
) : ViewModel() {

    private val _coordinatesFlow = MutableStateFlow<CoordinatesResult>(CoordinatesResult.Loading)
    val coordinatesFlow = _coordinatesFlow.asStateFlow()

    private var selectedDate: Long = System.currentTimeMillis()

    init {
        getCoordinatesBySelectedDate(selectedDate)
    }

    fun getCoordinatesBySelectedDate(date: Long) {
        selectedDate = date
        viewModelScope.launch(ioDispatcher) {
            _coordinatesFlow.emit(CoordinatesResult.Loading)
            val result = getCoordinatesUseCase.getCoordinatesForDay(date)
            _coordinatesFlow.emit(result)
        }
    }

    fun getLastSelectedDate(): Long {
        return selectedDate
    }

    fun logOut(onSuccess: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
           logOutUseCase.deleteData()
            withContext(mainDispatcher) {
                onSuccess()
            }
        }
    }
}