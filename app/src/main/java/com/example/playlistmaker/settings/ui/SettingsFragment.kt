package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentaion.SettingsViewModel
import com.example.playlistmaker.settings.ui.models.ThemeState
import org.koin.androidx.viewmodel.ext.android.viewModel

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeTheme().observe(viewLifecycleOwner) {
            render(it)
        }

        val checked = viewModel.getTheme()
        binding.switchTheme.isChecked = checked

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            viewModel.setTheme(isChecked)
        }

        fun showShareDialog() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.share_app)))
        }

        binding.share.setOnClickListener {
            showShareDialog()
        }
        binding.support.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.dev_message))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.dev_message2))
            }

            startActivity(supportIntent)
        }

        binding.agreement.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))
            startActivity(browserIntent)
        }
    }

    private fun render(state: ThemeState) {
        when (state) {
            ThemeState.Light -> {
                App.switchTheme(false)
            }

            ThemeState.Dark -> {
                App.switchTheme(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}