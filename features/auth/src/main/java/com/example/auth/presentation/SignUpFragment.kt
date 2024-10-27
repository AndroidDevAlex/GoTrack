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
import com.example.auth.databinding.FragmentSignUpBinding
import com.example.auth.setLocale
import com.example.navigation.NavigationService
import com.example.network.NetworkStateObserver
import com.example.settingsapplication.ApplicationSettings
import com.example.utils.Utils
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var navigationService: NavigationService

    @Inject
    lateinit var networkStateObserver: NetworkStateObserver

    @Inject
    lateinit var applicationSettings: ApplicationSettings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        observeRegistrationResult()
        updateLocale()
        checkNetworkAndSignUp()

        return binding.root
    }

    private fun updateLocale() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationSettings.applicationLanguage.collect { language ->
                    requireContext().setLocale(language)
                    binding.registrationTitle.text = getString(R.string.registration)
                    binding.createAccountButton.text = getString(R.string.create_account)
                }
            }
        }
    }

    private fun checkNetworkAndSignUp() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkStateObserver.isConnected.collect { isConnected ->
                    binding.createAccountButton.setOnClickListener {
                        if (isConnected) {
                            val email = binding.emailEditText.text.toString().trim()
                            val password = binding.passwordEditText.text.toString().trim()
                            val repeatPassword =
                                binding.repeatPasswordEditText.text.toString().trim()

                            if (validateInput(email, password, repeatPassword)) {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.registration(email, password)
                            }
                        } else {
                            Utils.showToast(requireContext(), getString(R.string.no_internet))
                        }
                    }
                }
            }
        }
    }

    private fun observeRegistrationResult() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authResult.collect { result ->
                    binding.progressBar.visibility = View.GONE
                    when (result) {
                        is AuthResult.RegisterResult -> {
                            Utils.showToast(
                                requireContext(),
                                getString(R.string.registration_are_successful)
                            )
                            binding.progressBar.visibility = View.GONE
                            navigationService.navigateFromSignUpToActionFragment()
                        }

                        is AuthResult.Error -> {
                            handleAuthError(result.exception)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String, repeatPassword: String): Boolean {
        return when {
            AuthValidator.isEmailAndPasswordIsEmpty(email, password, repeatPassword) -> {
                Utils.showToast(requireContext(), getString(R.string.all_fields_must_be_completed))
                false
            }

            !AuthValidator.isEmailMatcher(email) -> {
                Utils.showToast(requireContext(), getString(R.string.invalid_email))
                false
            }

            !AuthValidator.isPasswordMatcher(password) -> {
                Utils.showToast(requireContext(), getString(R.string.invalid_password))
                false
            }

            !AuthValidator.validateRepeatPassword(password, repeatPassword) -> {
                Utils.showToast(requireContext(), getString(R.string.passwords_do_not_match))
                false
            }

            else -> true
        }
    }

    private fun handleAuthError(exception: Exception) {
        val errorMessage: Int = when (exception) {
            is FirebaseAuthUserCollisionException -> R.string.email_already_exists
            is FirebaseAuthInvalidCredentialsException -> R.string.invalid_password
            else -> R.string.error_occurred
        }
        Utils.showToast(requireContext(), getString(errorMessage))
    }
}