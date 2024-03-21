package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.presentation.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { apiRequest(text) }

    private lateinit var notFound: LinearLayout
    private lateinit var searchHistory: SearchHistory
    private lateinit var editText: EditText
    private lateinit var noInternet: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var tracksRecycler: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var tracks: MutableList<Track>
    private lateinit var progressBar: ProgressBar

    private var text: String = EMPTY
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onStop() {
        super.onStop()
        searchHistory.putTracks()
        historyAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText)
        notFound = findViewById(R.id.not_found)
        noInternet = findViewById(R.id.no_internet)
        refreshButton = findViewById(R.id.refresh_button)
        tracksRecycler = findViewById(R.id.track_recycler)
        historyLayout = findViewById(R.id.history_layout)
        tracks = mutableListOf()
        progressBar = findViewById(R.id.progressBar)

        val historyPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        searchHistory = SearchHistory(historyPrefs)
        searchHistory.getTracks()
        val playerIntent = Intent(this, PlayerActivity::class.java)
        adapter = TrackAdapter(
            tracks,
            callback = { track ->
                searchHistory.addTrack(track)
                if (clickDebounce()) {
                    startActivity(playerIntent.putExtra(INTENT_KEY, track))
                }
            }
        )

        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)

        historyLayout.visibility =
            if (searchHistory.historyList.isEmpty()) View.GONE else View.VISIBLE

        historyAdapter = TrackAdapter(
            searchHistory.historyList,
            callback = { track ->
                searchHistory.addTrack(track)
                if (clickDebounce()) {
                    startActivity(playerIntent.putExtra(INTENT_KEY, track))
                }
            }
        )

        historyRecycler.adapter = historyAdapter
        historyRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tracksRecycler.adapter = adapter
        tracksRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val clearHistory = findViewById<Button>(R.id.clear_history)
        clearHistory.setOnClickListener {
            historyPrefs.edit().clear().apply()
            searchHistory.historyList.clear()
            historyLayout.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        val backButton = findViewById<ImageView>(R.id.backArrowImageView)
        backButton.setOnClickListener {
            finish()
        }

        val clearButton = findViewById<ImageView>(R.id.clearImageView)

        clearButton.setOnClickListener {
            editText.setText(EMPTY)
            tracks.clear()
            adapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
            if (searchHistory.historyList.isEmpty()) historyLayout.visibility = View.GONE
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
                if (editText.hasFocus() && p0?.isEmpty() == false) historyLayout.visibility =
                    View.GONE
                else {
                    historyLayout.visibility = View.VISIBLE
                    historyAdapter.notifyDataSetChanged()
                }
                if (p0.toString().isNotEmpty()) {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
            }
            false
        }

        refreshButton.setOnClickListener {
            apiRequest(text)
            noInternet.visibility = View.GONE
        }

    }

    private fun apiRequest(text: String) {
        progressBar.visibility = View.VISIBLE
        iTunesService.getTrack(text)
            .enqueue(object : Callback<TracksResponse> {

                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    val tracksFromResp = response.body()?.results

                    if (response.isSuccessful && tracksFromResp != null) {
                        if (tracksFromResp.isEmpty()) {
                            progressBar.visibility = View.GONE
                            notFound.visibility = View.VISIBLE
                            noInternet.visibility = View.GONE
                            tracks.clear()
                        } else {
                            progressBar.visibility = View.GONE
                            tracks.clear()
                            tracks.addAll(tracksFromResp.toMutableList())
                            notFound.visibility = View.GONE
                            noInternet.visibility = View.GONE
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        tracks.clear()
                        noInternet.visibility = View.VISIBLE
                        notFound.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    noInternet.visibility = View.VISIBLE
                    notFound.visibility = View.GONE
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                }
            })

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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
        const val INTENT_KEY = "key"
        private const val HISTORY_KEY = "history"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}