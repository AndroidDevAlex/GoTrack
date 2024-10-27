package com.example.map.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data.LocationData
import com.example.map.CoordinatesResult
import com.example.map.CustomDatePickerDialog
import com.example.map.R
import com.example.map.databinding.FragmentMapBinding
import com.example.navigation.NavigationService
import com.example.network.NetworkStateObserver
import com.example.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TRACK_START = "Start of the track"
        const val TRACK_END = "End of the track"
        const val MARKER_TITLE = "Marker google map"
        const val DIALOG_TAG = "CustomDialogCalendar"
    }

    private lateinit var binding: FragmentMapBinding
    private lateinit var myGoogleMap: GoogleMap
    private val viewModel: MapViewModel by viewModels()

    @Inject
    lateinit var navigationService: NavigationService

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarMap)

        requestInternetPermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarMenu()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.coordinatesFlow.collect { result ->
                    when (result) {
                        is CoordinatesResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is CoordinatesResult.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (::myGoogleMap.isInitialized) {
                                updateMap(result.coordinates)
                            }
                        }

                        is CoordinatesResult.NoData -> {
                            binding.progressBar.visibility = View.GONE
                            if (::myGoogleMap.isInitialized) {
                                myGoogleMap.clear()
                            }
                            Utils.showToast(
                                requireContext(),
                                getString(R.string.no_available_coordinates)
                            )
                        }

                        is CoordinatesResult.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Utils.showToast(requireContext(), result.message)
                        }
                    }
                }
            }
        }
    }

    private fun initializeMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        myGoogleMap.uiSettings.isZoomControlsEnabled = true
        myGoogleMap.let {
            childFragmentManager.setFragmentResultListener(
                CustomDatePickerDialog.SELECTED_DAY_KEY,
                this
            ) { _, result ->
                val newDateResult = result.getLong(CustomDatePickerDialog.SELECTED_DAY_KEY)
                viewModel.getCoordinatesBySelectedDate(newDateResult)
            }
        }
    }

    private fun requestInternetPermissions() {
        val network = networkStateObserver.isConnected.value
        if (network) {
            initializeMap()
        } else {
            Utils.showToast(requireContext(), getString(R.string.turn_on_the_internet))
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
        }
    }

    private fun setAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.actionLogout -> {
                        viewModel.logOut {
                            navigationService.navigateFromMapToAuthFragment()
                        }
                        return true
                    }

                    R.id.back -> {
                        navigationService.navigateToPreviousScreen()
                        return true
                    }

                    R.id.calendar_ -> {
                        showCalendarDialog()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun showCalendarDialog() {
        val lastSelectedDate = viewModel.getLastSelectedDate()

        val dialogCalendar = CustomDatePickerDialog.newInstance(lastSelectedDate)
        dialogCalendar.show(childFragmentManager, DIALOG_TAG)
    }

    private fun updateMap(coordinates: List<LocationData>) {
        if (coordinates.isNotEmpty()) {
            myGoogleMap.clear()
            val polylinePoints = mutableListOf<LatLng>()

            val firstCoordinate = coordinates.first()
            val lastCoordinate = coordinates.last()

            myGoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(firstCoordinate.latitude, firstCoordinate.longitude))
                    .title(TRACK_START)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            myGoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lastCoordinate.latitude, lastCoordinate.longitude))
                    .title(TRACK_END)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )

            for (newCoordinate in coordinates) {
                val latLng = LatLng(newCoordinate.latitude, newCoordinate.longitude)
                polylinePoints.add(latLng)

                myGoogleMap.addMarker(
                    MarkerOptions().position(latLng).title(MARKER_TITLE)
                )
            }

            if (polylinePoints.size > 1) {
                val polylineOptions = PolylineOptions()
                    .addAll(polylinePoints)
                    .width(5f)
                    .color(Color.BLUE)

                myGoogleMap.addPolyline(polylineOptions)
            }
            myGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(firstCoordinate.latitude, firstCoordinate.longitude),
                    2f
                )
            )
        }
    }
}