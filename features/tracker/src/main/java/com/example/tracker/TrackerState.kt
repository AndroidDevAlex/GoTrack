package com.example.tracker

sealed class TrackerState {

    data object Enabled : TrackerState()
    data object NoInternet : TrackerState()
    data object Disabled : TrackerState()
}