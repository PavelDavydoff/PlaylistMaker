package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val HISTORY_KEY = "history"

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private var text: String = EMPTY
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    override fun onStop() {
        super.onStop()
        val historyPreferences = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        historyPreferences.edit().putString(HISTORY_KEY, SearchHistory.jsonFromList(SearchHistory.historyList)).apply()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText)
        val notFound = findViewById<LinearLayout>(R.id.not_found)
        val noInternet = findViewById<LinearLayout>(R.id.no_internet)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        val tracksRecycler = findViewById<RecyclerView>(R.id.track_recycler)
        val historyLayout = findViewById<LinearLayout>(R.id.history_layout)
        val tracks = mutableListOf<Track>()
        val adapter = TrackAdapter(tracks)

        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)

        val historyPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)

        SearchHistory.historyJson = historyPrefs.getString(HISTORY_KEY, null)
        SearchHistory.historyList = SearchHistory.listFromJson(SearchHistory.historyJson)

        historyLayout.visibility = if (SearchHistory.historyList.isEmpty()) View.GONE else View.VISIBLE

        val historyAdapter = TrackAdapter(SearchHistory.historyList)
        historyRecycler.adapter = historyAdapter
        historyRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tracksRecycler.adapter = adapter
        tracksRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val clearHistory = findViewById<Button>(R.id.clear_history)
        clearHistory.setOnClickListener{
            historyPrefs.edit().clear().apply()
            SearchHistory.historyList.clear()
            historyLayout.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        val backButton = findViewById<ImageView>(R.id.backArrowImageView)
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val clearButton = findViewById<ImageView>(R.id.clearImageView)

        clearButton.setOnClickListener {
            editText.setText(EMPTY)
            tracks.clear()
            adapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
            notFound.visibility = View.GONE
            noInternet.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
                if (editText.hasFocus() && p0?.isEmpty() == false) historyLayout.visibility = View.GONE
                else {
                    historyLayout.visibility = View.VISIBLE
                    historyAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)

        fun apiRequest(text: String) {
            iTunesService.getTrack(text)
                .enqueue(object : Callback<TracksResponse> {

                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        val tracksFromResp = response.body()?.results

                        if (response.isSuccessful && tracksFromResp != null) {
                            if (tracksFromResp.isEmpty()) {
                                notFound.visibility = View.VISIBLE
                                noInternet.visibility = View.GONE
                                tracks.clear()
                            } else {
                                tracks.clear()
                                tracks.addAll(tracksFromResp.toMutableList())
                                notFound.visibility = View.GONE
                                noInternet.visibility = View.GONE
                            }
                        } else {
                            tracks.clear()
                            noInternet.visibility = View.VISIBLE
                            notFound.visibility = View.GONE
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        noInternet.visibility = View.VISIBLE
                        notFound.visibility = View.GONE
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                    }
                })

        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
                true
            }
            false
        }

        refreshButton.setOnClickListener {
            apiRequest(text)
            noInternet.visibility = View.GONE
        }

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
        editText.setText(restoredText)
    }

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
    }

}