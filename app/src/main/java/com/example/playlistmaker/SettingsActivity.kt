package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back)//Кнопка "Назад"
        backButton.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }



        val switchTheme = findViewById<Switch>(R.id.switchTheme)//Переключатель тёмной темы

        switchTheme.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun showShareDialog() {//Кнопка "Поделиться"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = resources.getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, resources.getString(R.string.share)))
        }

        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener {
            showShareDialog()
        }

        val supportButton = findViewById<Button>(R.id.support)//Кнопка "Написать в поддержку"
        supportButton.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(resources.getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.dev_message))
                putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.dev_message2))
            }

            startActivity(supportIntent)
        }

        val agreementButton = findViewById<Button>(R.id.agreement)//Кнопка "Пользовательское соглашение"
        agreementButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.offer)))
            startActivity(browserIntent)
        }
    }
}