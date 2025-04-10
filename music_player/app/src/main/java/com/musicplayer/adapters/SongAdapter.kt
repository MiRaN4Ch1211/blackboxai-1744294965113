package com.musicplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.musicplayer.R
import com.musicplayer.databinding.ItemSongBinding
import com.musicplayer.models.Song

class SongAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onMoreClick: (Song, View) -> Unit
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    private var currentSong: Song? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }

    fun setCurrentSong(song: Song?) {
        val oldSong = currentSong
        currentSong = song
        
        oldSong?.let { old ->
            val oldPosition = currentList.indexOfFirst { it.id == old.id }
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
        }
        
        song?.let { new ->
            val newPosition = currentList.indexOfFirst { it.id == new.id }
            if (newPosition != -1) {
                notifyItemChanged(newPosition)
            }
        }
    }

    inner class SongViewHolder(
        private val binding: ItemSongBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSongClick(getItem(position))
                }
            }

            binding.moreButton.setOnClickListener { view ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClick(getItem(position), view)
                }
            }
        }

        fun bind(song: Song) {
            binding.apply {
                songTitle.text = song.title
                artistName.text = song.artist
                duration.text = song.formatDuration()

                // Load album art
                Glide.with(albumArt)
                    .load(song.albumArtUri)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .centerCrop()
                    .into(albumArt)

                // Highlight current song
                root.isSelected = song.id == currentSong?.id
                root.setBackgroundResource(
                    if (song.id == currentSong?.id) 
                        R.color.secondary 
                    else 
                        android.R.color.transparent
                )
            }
        }
    }

    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}
