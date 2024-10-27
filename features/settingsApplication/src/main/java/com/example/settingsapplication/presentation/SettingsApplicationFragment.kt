package com.example.settingsapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.settingsApplication.R
import com.example.settingsApplication.databinding.FragmentSettingsApplicationBinding
import com.example.settingsapplication.DialogSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsApplicationFragment : DialogSettings() {

    private lateinit var binding: FragmentSettingsApplicationBinding
    private val viewModel: SettingsApplicationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsApplicationBinding.inflate(inflater, container, false)
        initialBinding()
        return binding.root
    }

    private fun initialBinding() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.languageSettingStateFlow.collect {
                    binding.languageSettingValue.text =
                        it.name.lowercase().replaceFirstChar { char -> char.uppercase() }
                    binding.settings.text = getString(R.string.settings)
                    binding.currentLanguage.text = getString(R.string.interface_language_settings)
                    binding.languageSettingValue.text = it.getLocalizedName(requireContext())

                    binding.languageSettingsContainer.setOnClickListener { _ ->
                        showMultipleChoiceSettingsDialog(
                            title = requireContext().getString(R.string.interface_language_settings),
                            subtitle = requireContext().getString(R.string.change_the_language),
                            setting = it
                        ) { onSelectSettings ->
                            viewModel.setLanguage(onSelectSettings)
                        }
                    }
                }
            }
        }
    }
}