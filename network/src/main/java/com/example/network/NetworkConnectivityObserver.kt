package com.example.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

class NetworkConnectivityObserver @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher
) : NetworkStateObserver {

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    init {
        observeConnection()
    }

    private fun observeConnection() {
        callbackFlow {
            val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            connectivityManager.registerNetworkCallback(request, connectivityCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(connectivityCallback)
            }
        }
            .distinctUntilChanged()
            .flowOn(mainDispatcher)
            .onEach { _isConnected.value = it }
            .launchIn(CoroutineScope(mainDispatcher))
    }
}