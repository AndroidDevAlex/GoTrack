package com.example.settingsapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settingsapplication.ApplicationLanguage
import com.example.settingsapplication.ApplicationSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsApplicationViewModel @Inject constructor(
    private val applicationSettings: ApplicationSettings
) : ViewModel() {


    private val _languageSettingStateFlow: MutableStateFlow<ApplicationLanguage> = applicationSettings.applicationLanguage

    val languageSettingStateFlow: StateFlow<ApplicationLanguage> = _languageSettingStateFlow

    init {
        getLanguage()
    }

    fun setLanguage(language: ApplicationLanguage) {
        viewModelScope.launch {
            applicationSettings.setApplicationLanguage(language)
        }
    }

    private fun getLanguage() {
        viewModelScope.launch {
            applicationSettings.applicationLanguage.collect {
                _languageSettingStateFlow.value = it
            }
        }
    }
}