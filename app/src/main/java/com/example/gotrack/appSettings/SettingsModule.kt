package com.example.gotrack.appSettings

import com.example.settingsapplication.ApplicationSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindApplicationSettings(
        applicationSettings: SharedPreferencesApplicationSettings
    ) : ApplicationSettings
}