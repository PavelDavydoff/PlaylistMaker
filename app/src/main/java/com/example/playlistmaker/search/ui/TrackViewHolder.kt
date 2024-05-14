package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.TrackTime

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageTrack = itemView.findViewById<ImageView>(R.id.image_track)
    private val textTrackName = itemView.findViewById<TextView>(R.id.text_track_name)
    private val textArtist = itemView.findViewById<TextView>(R.id.text_artist)
    private val textTime = itemView.findViewById<TextView>(R.id.text_time)

    fun bind(model: TrackDto){
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(imageTrack)
        textTrackName.text = model.trackName
        textArtist.text = model.artistName
        textTime.text = TrackTime.get(model)
    }
}