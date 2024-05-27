package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backArrowImageView)
        backButton.setOnClickListener{
            finish()
        }

        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)
        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        val checked = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme.isChecked = checked
        (applicationContext as App).switchTheme(checked)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(THEME_KEY, isChecked).apply()
        }

        fun showShareDialog() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }

        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener {
            showShareDialog()
        }
        val supportButton = findViewById<Button>(R.id.support)
        supportButton.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.dev_message))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.dev_message2))
            }

            startActivity(supportIntent)
        }

        val agreementButton = findViewById<Button>(R.id.agreement)
        agreementButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))
            startActivity(browserIntent)
        }
    }
}