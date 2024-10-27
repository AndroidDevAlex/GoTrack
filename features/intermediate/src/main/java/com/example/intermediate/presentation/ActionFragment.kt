package com.example.intermediate.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.intermediate.R
import com.example.intermediate.databinding.FragmentOptionsBinding
import com.example.navigation.NavigationService
import com.example.network.NetworkStateObserver
import com.example.tracker.TrackService
import com.example.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActionFragment : Fragment() {

    private lateinit var binding: FragmentOptionsBinding
    private val viewModel: ActionViewModel by viewModels()

    @Inject
    lateinit var navigationService: NavigationService

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(inflater, container, false)
        showCurrentUser()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.logOut {
                navigationService.navigateToPreviousScreen()
            }
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarOptions)

        checkInternetState()

        return binding.root
    }

    private fun showCurrentUser() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loggedUser.collectLatest { loggedUser ->
                    if (loggedUser != null) {
                    binding.currentUser.text = loggedUser.email
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
                menuInflater.inflate(R.menu.toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        viewModel.logOut {
                            stopTracker()
                            navigationService.navigateToPreviousScreen()
                        }
                        return true
                    }
                    R.id.settings -> {
                        navigationService.navigateToSettings()
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun stopTracker() {
        val serviceIntentStop = Intent(requireContext(), TrackService::class.java).apply {
            action = TrackService.STOP
        }
        requireContext().stopService(serviceIntentStop)
    }

    private fun checkInternetState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkStateObserver.isConnected.collect { isConnected ->
                    binding.goToTrack.setOnClickListener {
                        if (isConnected) {
                            navigationService.navigateToTrack()
                        } else {
                            Utils.showToast(requireContext(), getString(R.string.no_internet))
                        }
                    }

                    binding.goToMap.setOnClickListener {
                        if (isConnected) {
                            navigationService.navigateToMap()
                        } else {
                            Utils.showToast(requireContext(), getString(R.string.no_internet))
                        }
                    }
                }
            }
        }
    }
}