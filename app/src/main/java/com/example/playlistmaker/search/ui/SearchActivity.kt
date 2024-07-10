package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.ui.models.TracksState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var notFound: LinearLayout
    private lateinit var searchHistory: SearchHistory
    private lateinit var editText: EditText
    private lateinit var noInternet: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var tracksRecycler: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var textWatcher: TextWatcher

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var queryInput: String

    override fun onStop() {
        super.onStop()
        searchHistory.putTracks()
        Log.d("SearchActivity", "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SearchActivity", "onPause")
    }

    override fun onResume() {
        super.onResume()
        updateAdapter(historyAdapter, searchHistory.historyList)
        Log.d("SearchActivity", "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.d("SearchActivity", "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { editText.removeTextChangedListener(it) }
        Log.d("SearchActivity", "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SearchActivity", "onCreate")
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText)
        notFound = findViewById(R.id.not_found)
        noInternet = findViewById(R.id.no_internet)
        refreshButton = findViewById(R.id.refresh_button)
        tracksRecycler = findViewById(R.id.track_recycler)
        historyLayout = findViewById(R.id.history_layout)
        progressBar = findViewById(R.id.progressBar)

        queryInput = ""

        val historyPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        searchHistory = SearchHistory(historyPrefs)
        searchHistory.getTracks()

        val playerIntent = Intent(this, PlayerActivity::class.java)

        tracksAdapter = TrackAdapter(
            callback = { track ->
                searchHistory.addTrack(track)
                if (clickDebounce()) {
                    startActivity(playerIntent.putExtra(INTENT_KEY, track))
                }
            }
        )

        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)

        historyAdapter = TrackAdapter(
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


        tracksRecycler.adapter = tracksAdapter
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
            viewModel.setState(TracksState.History(searchHistory.historyList))

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                viewModel.setState(TracksState.History(searchHistory.historyList))
                Log.d("SearchActivity", "editTextHasFocus")
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
                viewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                queryInput = p0.toString()
            }
        }
        textWatcher.let { editText.addTextChangedListener(it) }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.forcedRequest(queryInput)
            }
            false
        }

        viewModel.observeState().observe(this){
            render(it)
        }

        refreshButton.setOnClickListener {
            viewModel.repeatRequest()
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
        outState.putString(KEY, queryInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(KEY)
        editText.setText(restoredText)
    }

    private fun render(state:TracksState){
        when(state){
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty()
            is TracksState.History -> showHistory(searchHistory.historyList)
        }
    }
    private fun showHistory(tracks: List<Track>){
        Log.d("SearchActivity", "showHistory")
        historyLayout.visibility =
            if (searchHistory.historyList.isEmpty()) View.GONE else View.VISIBLE

        tracksRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        noInternet.visibility = View.GONE
        notFound.visibility = View.GONE

        updateAdapter(historyAdapter, tracks)
    }
    private fun showLoading(){
        Log.d("SearchActivity", "showLoading")
        tracksRecycler.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        noInternet.visibility = View.GONE
        notFound.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }
    private fun showError(){
        Log.d("SearchActivity", "showError")
        tracksRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        noInternet.visibility = View.VISIBLE
        notFound.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }
    private fun showEmpty(){
        Log.d("SearchActivity", "showEmpty")
        tracksRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        noInternet.visibility = View.GONE
        notFound.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
    }
    private fun showContent(tracks: List<Track>){
        Log.d("SearchActivity", "showContent")
        tracksRecycler.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        noInternet.visibility = View.GONE
        notFound.visibility = View.GONE
        historyLayout.visibility = View.GONE

        updateAdapter(tracksAdapter, tracks)
    }

    private fun updateAdapter(adapter: TrackAdapter, tracks: List<Track>){
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
        const val INTENT_KEY = "key"
        private const val HISTORY_KEY = "history"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}