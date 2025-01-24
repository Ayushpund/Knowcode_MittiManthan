package com.example.mittimanthan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mittimanthan.R
import com.example.mittimanthan.models.VideoPost

class VideoAdapter(
    private val videos: List<VideoPost>,
    private val onVideoClick: (VideoPost) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.videoCard)
        val thumbnail: ImageView = view.findViewById(R.id.videoThumbnail)
        val title: TextView = view.findViewById(R.id.videoTitle)
        val description: TextView = view.findViewById(R.id.videoDescription)
        val duration: TextView = view.findViewById(R.id.videoDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.thumbnail.setImageResource(video.thumbnailResId)
        holder.title.text = video.title
        holder.description.text = video.description
        holder.duration.text = video.duration
        holder.cardView.setOnClickListener { onVideoClick(video) }
    }

    override fun getItemCount() = videos.size
} 