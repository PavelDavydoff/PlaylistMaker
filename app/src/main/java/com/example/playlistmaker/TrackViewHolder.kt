package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageTrack = itemView.findViewById<ImageView>(R.id.image_track)
    private val textTrackName = itemView.findViewById<TextView>(R.id.text_track_name)
    private val textArtist = itemView.findViewById<TextView>(R.id.text_artist)
    private val textTime = itemView.findViewById<TextView>(R.id.text_time)

    fun bind(model: Track){
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(itemView, 2f)))
            .into(imageTrack)
        textTrackName.text = model.trackName
        textArtist.text = model.artistName
        textTime.text = model.trackTime

    }
    private fun dpToPx(view: View, dp: Float) : Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
    }
}