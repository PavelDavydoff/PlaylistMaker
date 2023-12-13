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

class SearchActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var text: String = EMPTY
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText)
        val notFound = findViewById<LinearLayout>(R.id.not_found)
        val noInternet = findViewById<LinearLayout>(R.id.no_internet)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        val tracksRecycler = findViewById<RecyclerView>(R.id.track_recycler)
        tracksRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val backButton = findViewById<ImageView>(R.id.backArrowImageView)//Кнопка "Назад"
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val clearButton = findViewById<ImageView>(R.id.clearImageView)

        clearButton.setOnClickListener {
            editText!!.setText(EMPTY)
            tracksRecycler.adapter = TrackAdapter(listOf())
            notFound.visibility = View.GONE
            noInternet.visibility = View.GONE
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

        fun apiRequest(text: String){
            iTunesService.getTrack(text)
                .enqueue(object : Callback<ResponseTracks> {

                    override fun onResponse(
                        call: Call<ResponseTracks>,
                        response: Response<ResponseTracks>
                    ) {
                        when (response.code()) {
                            200 -> {
                                if (response.body()?.results?.size == 0) {
                                    notFound.visibility = View.VISIBLE
                                } else {
                                    val tracks = response.body()?.results
                                    tracksRecycler.adapter = TrackAdapter(tracks!!)
                                }
                            }

                            else -> {
                                noInternet.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseTracks>, t: Throwable) {
                        noInternet.visibility = View.VISIBLE
                    }
                })
        }

        editText!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
                true
            }
            false
        }

        refreshButton.setOnClickListener() {
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
        editText!!.setText(restoredText)
    }

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
    }


}