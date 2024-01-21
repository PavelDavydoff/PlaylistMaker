package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(){
    companion object{
        var historyJson: String? = null
        var historyList = mutableListOf<Track>()
        fun addTrack(track: Track){
            historyList = listFromJson(historyJson)
            if (historyList.contains(track)) {
                historyList.remove(track)
            }
            if (historyList.size > 9){
                historyList.removeAt(9)
            }
            historyList.add(0, track)
            historyJson = jsonFromList(historyList)
        }

        fun jsonFromList(list: MutableList<Track>): String{
            val gson = Gson()
            return gson.toJson(list)
        }

        fun listFromJson(json: String?): MutableList<Track>{
            val gson = Gson()
            val listType = object : TypeToken<MutableList<Track>>() {}.type
            return gson.fromJson(json, listType) ?: mutableListOf()
        }

        fun refreshList(sharedPreferences: SharedPreferences, key: String){
            historyJson = sharedPreferences.getString(key, null)
            historyList = listFromJson(historyJson)
        }
    }
}