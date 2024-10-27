package com.example.gotrack.appSettings

import android.content.Context
import com.example.settingsapplication.ApplicationLanguage
import com.example.settingsapplication.ApplicationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SharedPreferencesApplicationSettings @Inject constructor(
    @ApplicationContext appContext: Context
) : ApplicationSettings, AppSettings(appContext) {

    override val applicationLanguage = MutableStateFlow(getApplicationLanguage())

    override suspend fun setApplicationLanguage(language: ApplicationLanguage) {
        if (getApplicationLanguage() == language) return
        sharedPreferences.edit()
            .putInt(PREF_CURRENT_APPLICATION_LANGUAGE, language.value)
            .apply()
        applicationLanguage.emit(language)
    }


    private fun getApplicationLanguage(): ApplicationLanguage {
        return ApplicationLanguage.fromValue(
            sharedPreferences.getInt(
                PREF_CURRENT_APPLICATION_LANGUAGE,
                ApplicationLanguage.ENGLISH.value
            )
        )
    }

    companion object {
        private const val PREF_CURRENT_APPLICATION_LANGUAGE = "currentApplicationLanguage"
    }
}