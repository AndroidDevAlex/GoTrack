package com.example.settingsapplication

import kotlinx.coroutines.flow.MutableStateFlow

interface ApplicationSettings {

    val applicationLanguage: MutableStateFlow<ApplicationLanguage>

    suspend fun setApplicationLanguage(language: ApplicationLanguage)

}