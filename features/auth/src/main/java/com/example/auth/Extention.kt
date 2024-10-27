package com.example.auth

import android.content.Context
import com.example.settingsapplication.ApplicationLanguage
import java.util.Locale


fun Context.setLocale(language: ApplicationLanguage){
    val locale = when (language) {
        ApplicationLanguage.ENGLISH -> Locale("en")
        ApplicationLanguage.UKRAINIAN -> Locale("uk")
        ApplicationLanguage.RUSSIAN -> Locale("ru")
        else -> Locale.getDefault()
    }

    Locale.setDefault(locale)
    val config = this.resources.configuration
    config.setLocale(locale)

    this.createConfigurationContext(config)
    this.resources.updateConfiguration(config, this.resources.displayMetrics)
}