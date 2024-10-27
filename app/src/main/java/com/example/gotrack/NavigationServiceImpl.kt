package com.example.gotrack

import androidx.navigation.NavController
import com.example.navigation.NavigationService
import javax.inject.Inject

class NavigationServiceImpl @Inject constructor(
    private val navController: NavController
): NavigationService(navController) {

    override fun navigateToNextScreen() {
        if (navController.currentDestination?.id == R.id.authFragment) {
            navController.navigate(R.id.action_authFragment_to_optionsFragment)
        }
    }

    override fun navigateToTrack() {
        if (navController.currentDestination?.id == R.id.optionsFragment) {
            navController.navigate(R.id.action_optionsFragment_to_trackerFragment)
        }
    }

    override fun navigateToMap() {
        if (navController.currentDestination?.id == R.id.optionsFragment) {
            navController.navigate(R.id.action_optionsFragment_to_mapFragment)
        }
    }

    override fun navigateFromMapToAuthFragment() {
        if (navController.currentDestination?.id == R.id.mapFragment) {
            navController.navigate(R.id.action_mapFragment_to_authFragment)
        }
    }

    override fun navigateFromAuthToSignUpFragment() {
        if (navController.currentDestination?.id == R.id.authFragment){
            navController.navigate(R.id.action_authFragment_to_signUpFragment)
        }
    }

    override fun navigateFromSignUpToActionFragment() {
        if (navController.currentDestination?.id == R.id.signUpFragment){
            navController.navigate(R.id.action_signUpFragment_to_optionsFragment)
        }
    }

    override fun navigateFromTrackToAuthFragment() {
        if (navController.currentDestination?.id == R.id.trackerFragment) {
            navController.navigate(R.id.action_trackerFragment_to_authFragment)
        }
    }

    override fun navigateToSettings() {
        if (navController.currentDestination?.id == R.id.optionsFragment) {
            navController.navigate(R.id.action_optionsFragment_to_settingsApplicationFragment)
        }
    }
}