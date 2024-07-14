package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"
class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

        val sharedPrefs = requireActivity().getSharedPreferences(THEME, AppCompatActivity.MODE_PRIVATE)
        val checked = sharedPrefs.getBoolean(THEME_KEY, false)
        binding.switchTheme.isChecked = checked
        App.switchTheme(checked)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            App.switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(THEME_KEY, isChecked).apply()
        }

        fun showShareDialog() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }

        binding.share.setOnClickListener {
            showShareDialog()
        }
        binding.support.setOnClickListener{
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}