package com.iatv.app.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class NetworkState { Connected, Disconnected, Limited, Unknown }

@Singleton class NetworkMonitor @Inject constructor(@ApplicationContext context: Context) {
    private val manager = context.getSystemService(ConnectivityManager::class.java)
    val state = callbackFlow {
        fun snapshot() {
            val network = manager.activeNetwork
            val caps = network?.let(manager::getNetworkCapabilities)
            trySend(when {
                network == null -> NetworkState.Disconnected
                caps == null -> NetworkState.Unknown
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> NetworkState.Connected
                else -> NetworkState.Limited
            })
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { snapshot() }
            override fun onLost(network: Network) { snapshot() }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) { snapshot() }
        }
        manager.registerDefaultNetworkCallback(callback)
        snapshot()
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
