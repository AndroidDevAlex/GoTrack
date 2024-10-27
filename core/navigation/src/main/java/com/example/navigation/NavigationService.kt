package com.example.navigation

import androidx.navigation.NavController

abstract class NavigationService(
    private val navigationController: NavController
) {

    abstract fun navigateToNextScreen()

    abstract fun navigateToTrack()

    abstract fun navigateToMap()

    abstract fun navigateFromMapToAuthFragment()
    abstract fun navigateFromAuthToSignUpFragment()
    abstract fun navigateFromSignUpToActionFragment()

    abstract fun navigateFromTrackToAuthFragment()

    abstract fun navigateToSettings()

    fun navigateToPreviousScreen() {
        navigationController.navigateUp()
    }
}