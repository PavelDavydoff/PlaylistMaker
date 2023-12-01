package com.example.playlistmaker

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var viewTrack = itemView.findViewById<FrameLayout>(R.id.view_track)
    private var imageTrack = itemView.findViewById<ImageView>(R.id.image_track)
    private var textTrackName = itemView.findViewById<TextView>(R.id.text_track_name)
    private var textArtist = itemView.findViewById<TextView>(R.id.text_artist)
    private var textTime = itemView.findViewById<TextView>(R.id.text_time)
    private var forwardArrow = itemView.findViewById<ImageView>(R.id.image_forward_arrow)

    fun bind(model: Track){
        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(2))
            .centerCrop()
            .placeholder(R.drawable.cross)
            .into(imageTrack)
        textTrackName.text = model.trackName
        textArtist.text = model.artistName
        textTime.text = model.trackTime

    }
}