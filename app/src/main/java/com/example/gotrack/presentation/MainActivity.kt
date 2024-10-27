package com.example.gotrack.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gotrack.R
import com.example.gotrack.appSettings.setLocale
import com.example.gotrack.databinding.ActivityMainBinding
import com.example.network.NetworkStateObserver
import com.example.settingsapplication.presentation.SettingsApplicationViewModel
import com.example.tracker.worker.SendDataToCloudWorker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: SettingsApplicationViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var snackbar: Snackbar? = null

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.languageSettingStateFlow.collect { language ->
                    setLocale(language)
                }
            }
        }

        observeNetworkState()

        SendDataToCloudWorker.startWorker(this)
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            networkStateObserver.isConnected.collect { isConnected ->
                if (!isConnected) {
                    showSnackbar()
                } else {
                    snackbar?.dismiss()
                }
            }
        }
    }

    private fun showSnackbar() {
        snackbar = Snackbar.make(
            binding.root,
            getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE
        )
        snackbar?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        snackbar?.dismiss()
        snackbar = null
    }
}