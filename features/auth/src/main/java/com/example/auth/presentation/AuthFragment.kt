package com.example.auth.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.auth.AuthResult
import com.example.auth.AuthValidator
import com.example.auth.R
import com.example.auth.databinding.FragmentAuthBinding
import com.example.auth.setLocale
import com.example.navigation.NavigationService
import com.example.network.NetworkStateObserver
import com.example.settingsapplication.ApplicationSettings
import com.example.utils.Utils
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var navigationService: NavigationService

    @Inject
    lateinit var applicationSettings: ApplicationSettings

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        checkIsUserLoggedIn()
        updateLocale()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authResult.collectLatest { result ->
                    binding.progressBar.visibility = View.GONE

                    when (result) {
                        is AuthResult.LoginResult -> {
                            Utils.showToast(
                                requireContext(),
                                getString(R.string.user_in_the_system)
                            )
                            navigationService.navigateToNextScreen()
                        }

                        is AuthResult.Error -> {
                            handleError(result.exception)
                        }

                        else -> {}
                    }
                }
            }
        }

        binding.signIn.setOnClickListener {
            buttonClick { email, password ->
                checkNetworkAndSignIn(email, password)
            }
        }

        binding.signUp.setOnClickListener {
            navigationService.navigateFromAuthToSignUpFragment()
        }

        return binding.root
    }

    private fun updateLocale() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationSettings.applicationLanguage.collect { language ->
                    requireContext().setLocale(language)

                    binding.welcomeTitle.text = getString(R.string.welcome)
                    binding.signIn.text = getString(R.string.sign_in)
                    binding.accaunt.text = getString(R.string.don_t_have_an_account)
                    binding.signUp.text = getString(R.string.sign_up)
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        val errorMessage: Int = when (exception) {
            is FirebaseAuthInvalidUserException -> R.string.user_not_registered_please_register
            is FirebaseAuthInvalidCredentialsException -> R.string.invalid_password
            else -> R.string.error_occurred
        }
        Utils.showToast(requireContext(), getString(errorMessage))
    }


    private fun checkNetworkAndSignIn(email: String, password: String) {
        lifecycleScope.launch {
            val networkState = networkStateObserver.isConnected.value
            if (networkState) {
                authViewModel.signIn(email, password)
            } else {
                Utils.showToast(requireContext(), getString(R.string.no_internet))
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun buttonClick(action: (String, String) -> Unit) {
        val email = binding.InputNameText.text.toString().trim()
        val password = binding.InputPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Utils.showToast(requireContext(), getString(R.string.fields_can_not_be_empty))

            return
        }

        if (!AuthValidator.validateEmailAndPassword(email, password)) {
            Utils.showToast(requireContext(), getString(R.string.invalid_email_or_password))
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        action(email, password)
    }

    private fun checkIsUserLoggedIn() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loggedUser.collectLatest { loggedUser ->
                    binding.progressBar.visibility = View.GONE
                    if (loggedUser != null) {
                        navigationService.navigateToNextScreen()
                    }
                }
            }
        }
    }
}