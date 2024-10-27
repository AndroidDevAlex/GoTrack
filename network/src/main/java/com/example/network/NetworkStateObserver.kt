package com.example.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkStateObserver {
    val isConnected: StateFlow<Boolean>
}