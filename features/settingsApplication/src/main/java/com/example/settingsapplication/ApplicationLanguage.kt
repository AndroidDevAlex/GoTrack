package com.example.settingsapplication

import android.content.Context
import com.example.settingsApplication.R

enum class ApplicationLanguage(val value: Int){
    ENGLISH(0),
    UKRAINIAN(1),
    RUSSIAN(2);

    fun getLocalizedName(context: Context): String {
        return when (this) {
            ENGLISH -> context.getString(R.string.language_english)
            UKRAINIAN -> context.getString(R.string.language_ukrainian)
            else ->  context.getString(R.string.language_russian)
        }
    }

    companion object {
        fun fromValue(value: Int): ApplicationLanguage {
            return entries.firstOrNull { it.value == value } ?: ENGLISH
        }
    }
}