package com.example.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.TrackerState
import com.example.tracker.domain.DeleteDataUseCase
import com.example.tracker.domain.TrackerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TrackerViewModel @Inject constructor(
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val trackerStatusUseCase: TrackerStatusUseCase,
    private val deleteDataUseCase: DeleteDataUseCase
) : ViewModel() {

    private val _trackerStatus = MutableStateFlow<TrackerState>(TrackerState.Disabled)
    val trackerStatus: StateFlow<TrackerState> = _trackerStatus

    init {
        observeTrackerStatus()
    }

    private fun observeTrackerStatus() {
        viewModelScope.launch {
            trackerStatusUseCase.getTrackerStatus().collect { status ->
                _trackerStatus.value = status
            }
        }
    }

    fun deleteData(onSuccess: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            deleteDataUseCase.deleteData()
            withContext(mainDispatcher) {
                onSuccess()
            }
        }
    }
}