package com.musicplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.musicplayer.R
import com.musicplayer.databinding.ItemPlaylistBinding
import com.musicplayer.models.Playlist

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
    private val onMoreClick: (Playlist, View) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlaylistClick(getItem(position))
                }
            }

            binding.moreButton.setOnClickListener { view ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClick(getItem(position), view)
                }
            }
        }

        fun bind(playlist: Playlist) {
            binding.apply {
                playlistName.text = playlist.name
                songCount.text = playlist.getSongCountText()
                totalDuration.text = playlist.formatDuration()

                // Load playlist cover
                Glide.with(playlistCover)
                    .load(playlist.coverUri)
                    .placeholder(R.drawable.ic_playlist)
                    .error(R.drawable.ic_playlist)
                    .centerCrop()
                    .into(playlistCover)
            }
        }
    }

    private class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}
