package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var text: String = EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText)

        val trackList: ArrayList<Track> = ArrayList()
        trackList.add(Track("Smells like a teen spirit", "Nirvana", "5:01", getString(R.string.smells_like)))
        trackList.add(Track("Billie Jean", "Michael Jackson", "4:35", getString(R.string.billie_jean)))
        trackList.add(Track("Stayin' Alive", "Bee Gees", "4:10", getString(R.string.stayin_alive)))
        trackList.add(Track("Whole Lotta Love", "Led Zeppelin", "5:33", getString(R.string.whole_lotta)))
        trackList.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", getString(R.string.sweet_child)))

        val backButton = findViewById<ImageView>(R.id.backArrowImageView)//Кнопка "Назад"
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val clearButton = findViewById<ImageView>(R.id.clearImageView)

        clearButton.setOnClickListener {
            editText!!.setText(EMPTY)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText!!.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        editText!!.addTextChangedListener(simpleTextWatcher)

        val tracksRecycler = findViewById<RecyclerView>(R.id.track_recycler)

        tracksRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRecycler.adapter = TrackAdapter(trackList)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(KEY)
        editText!!.setText(restoredText)
    }

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
    }


}