package com.musicplayer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.musicplayer.databinding.FragmentSettingsBinding
import com.musicplayer.utils.SettingsManager
import com.musicplayer.viewmodels.SettingsViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            // Theme settings
            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setDarkTheme(isChecked)
            }

            // Audio quality settings
            audioQualityGroup.setOnCheckedChangeListener { _, checkedId ->
                val quality = when (checkedId) {
                    highQualityButton.id -> SettingsManager.AudioQuality.HIGH
                    balancedButton.id -> SettingsManager.AudioQuality.BALANCED
                    dataSaverButton.id -> SettingsManager.AudioQuality.DATA_SAVER
                    else -> SettingsManager.AudioQuality.BALANCED
                }
                viewModel.setAudioQuality(quality)
            }

            // Version info
            versionText.text = viewModel.getAppVersion()

            // Privacy Policy
            privacyPolicyButton.setOnClickListener {
                showPrivacyPolicy()
            }

            // Terms of Service
            termsOfServiceButton.setOnClickListener {
                showTermsOfService()
            }

            // Open Source Licenses
            licensesButton.setOnClickListener {
                showOpenSourceLicenses()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeSwitch.isChecked = isDarkTheme
        }

        viewModel.audioQuality.observe(viewLifecycleOwner) { quality ->
            val buttonId = when (quality) {
                SettingsManager.AudioQuality.HIGH -> binding.highQualityButton.id
                SettingsManager.AudioQuality.BALANCED -> binding.balancedButton.id
                SettingsManager.AudioQuality.DATA_SAVER -> binding.dataSaverButton.id
            }
            binding.audioQualityGroup.check(buttonId)
        }
    }

    private fun showPrivacyPolicy() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Privacy Policy")
            .setMessage("Your privacy is important to us. This app collects minimal data necessary for its operation and does not share any personal information with third parties.")
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showTermsOfService() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Terms of Service")
            .setMessage("By using this app, you agree to comply with all applicable laws and regulations regarding music playback and copyright.")
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showOpenSourceLicenses() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Open Source Licenses")
            .setMessage("""
                This app uses the following open source libraries:
                
                • AndroidX Libraries
                Apache License 2.0
                
                • Material Components for Android
                Apache License 2.0
                
                • Glide
                BSD, MIT, and Apache 2.0 licenses
                
                • Room Database
                Apache License 2.0
                
                • Kotlin Coroutines
                Apache License 2.0
            """.trimIndent())
            .setPositiveButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
