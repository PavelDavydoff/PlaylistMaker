package com.example.playlistmaker.search.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class SearchHistory(val context: Context){
    companion object{
        private const val HISTORY_KEY = "history"
        private const val MAX_SIZE = 10
        private const val LAST_INDEX = 9
        private const val FIRST_INDEX = 0

        var historyList = mutableListOf<Track>()
    }

    private val prefs = context.getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)

    fun clearPrefs(){
        prefs.edit().clear().apply()
    }

    fun getTracks(): List<Track>{
        val s = prefs.getString(HISTORY_KEY, null)
        historyList = listFromJson(s)
        return historyList
    }

    fun putTracks(){
        val s = jsonFromList(historyList)
        prefs.edit().putString(HISTORY_KEY, s).apply()
    }

    fun addTrack(track: Track){
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size >= MAX_SIZE){
            historyList.removeAt(LAST_INDEX)
        }
        historyList.add(FIRST_INDEX, track)
    }

    private fun jsonFromList(list: MutableList<Track>): String{
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun listFromJson(json: String?): MutableList<Track>{
        val gson = Gson()
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, listType) ?: mutableListOf()
    }
}