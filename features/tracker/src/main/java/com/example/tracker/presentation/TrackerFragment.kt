package com.example.tracker.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.navigation.NavigationService
import com.example.network.NetworkStateObserver
import com.example.tracker.R
import com.example.tracker.TrackService
import com.example.tracker.TrackerState
import com.example.tracker.TrackerStatusState
import com.example.tracker.databinding.FragmentTrackerBinding
import com.example.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackerFragment : Fragment() {

    private lateinit var binding: FragmentTrackerBinding
    private val viewModel: TrackerViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private var pressedButton: Boolean = false

    @Inject
    lateinit var navigationService: NavigationService

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)

        val networkState = networkStateObserver.isConnected.value
        if(networkState) {
            requestLocationAndNotificationPermission()
        } else
            Utils.showToast(requireContext(), getString(R.string.no_internet))

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        checkInternetAndStartTracking()
        observeTrackerStatus()

        return binding.root
    }

    private fun observeTrackerStatus() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackerStatus.collect { state ->
                    when (state) {
                        is TrackerState.Enabled -> updateUiTrackerState(TrackerStatusState.ENABLED)

                        is TrackerState.Disabled -> updateUiTrackerState(TrackerStatusState.DISABLED)

                        is TrackerState.NoInternet -> {
                            updateUiTrackerState(TrackerStatusState.NO_INTERNET)
                            stopTracker()
                        }
                        }
                    }
                }
            }
        }

    private fun updateUiTrackerState(statusState: TrackerStatusState) {
        updateViewTrackStatus(getString(statusState.textId), statusState.colorId)

        binding.progressBarTrack.visibility =
            if (statusState.showProgressBar) View.VISIBLE else View.GONE
        binding.startBtn.text = if (statusState == TrackerStatusState.ENABLED
        ) getString(R.string.stop) else getString(R.string.start)

    }

    private fun updateViewTrackStatus(text: String, color: Int) {
        binding.trackStatus.text = text
        binding.trackStatus.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun checkInternetAndStartTracking(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkStateObserver.isConnected.collect { isConnected ->
                    binding.startBtn.setOnClickListener {
                        if (isConnected) {
                            if (!pressedButton) startTracker() else stopTracker()
                        } else {
                            Utils.showToast(requireContext(), getString(R.string.no_internet))
                           }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarMenu()
    }

    private fun setAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu_track, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        viewModel.deleteData {
                            stopTracker()
                            navigationService.navigateFromTrackToAuthFragment()
                        }
                        return true
                    }
                    R.id.back -> {
                        stopTracker()
                        navigationService.navigateToPreviousScreen()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            val isGranted = permissionResult.all { entry ->
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    entry.key
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (isGranted) {
                if (checkGpsAndStartService()) {
                    if(!pressedButton) startTracker() else stopTracker()
                } else {
                    Utils.showToast(requireContext(), getString(R.string.gps_is_off))
                    Utils.showGpsDialog(requireContext(), this)
                }
            } else {
                Utils.showToast(requireContext(),getString(R.string.the_user_did_not_grant_both_permissions))
            }
        }

    private fun checkGpsAndStartService(): Boolean {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (isGpsEnabled) {
            requestNotificationPermission()
            Utils.showToast(requireContext(), getString(R.string.gps_is_on))
            true
        } else {
            Utils.showGpsDialog(requireContext(), this)
            false
        }
    }

    private fun requestLocationAndNotificationPermission() {
        Utils.requestLocationAndNotificationPermission(requestPermissionLauncher)
    }

    private fun requestNotificationPermission() {
        Utils.requestNotificationPermission(requireContext(), this)
    }

    private fun startTracker() {

        val serviceIntent = Intent(requireContext(), TrackService::class.java).apply {
            action = TrackService.START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
        pressedButton = true
        binding.startBtn.text = getString(R.string.stop)
    }

    private fun stopTracker() {
        val serviceIntentStop = Intent(requireContext(), TrackService::class.java).apply {
            action = TrackService.STOP
        }
        requireContext().stopService(serviceIntentStop)
        binding.startBtn.text = getString(R.string.start)
        pressedButton = false
    }
}