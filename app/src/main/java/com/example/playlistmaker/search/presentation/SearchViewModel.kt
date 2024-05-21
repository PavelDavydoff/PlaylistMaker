package com.example.playlistmaker.search.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SingleLiveEvent
import com.example.playlistmaker.search.ui.models.ToastState
import com.example.playlistmaker.search.ui.models.TracksState
import com.example.playlistmaker.util.Creator

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    /*private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast*/

    /*private val toastState = MutableLiveData<ToastState>(ToastState.None)
    fun observeToastState(): LiveData<ToastState> = toastState*/

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText || changedText.isEmpty()) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun repeatRequest(){
        if (latestSearchText != null) {
            searchRequest(latestSearchText!!)
        }
    }

    fun setState(state: TracksState){
        stateLiveData.postValue(state)
    }

    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest $newSearchText")
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
        }

        tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = mutableListOf<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }
                when {
                    errorMessage != null -> {
                        renderState(TracksState.Error(getApplication<Application>().getString(R.string.something_went_wrong)))
                        //showToast.postValue(errorMessage)
                    }

                    tracks.isEmpty() -> {
                        Log.d("SearchViewModel", "trackIsEmpty")
                        renderState(TracksState.Empty(getApplication<Application>().getString(R.string.nothing_found)))
                    }

                    else -> {
                        renderState(TracksState.Content(tracks))
                    }
                }
            }
        })
    }

    private fun renderState(state: TracksState) {
        Log.d("SearchViewModle", "redterState: $state")
        stateLiveData.postValue(state)
    }

    fun clearButtonClicked(tracks: List<Track>) {
        renderState(TracksState.Content(tracks))
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}