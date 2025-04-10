package com.musicplayer.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.musicplayer.R
import com.musicplayer.databinding.FragmentPlayerBinding
import com.musicplayer.viewmodels.MusicViewModel

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MusicViewModel by viewModels()
    private var isUserSeeking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            playPauseButton.setOnClickListener {
                if (viewModel.isPlaying.value == true) {
                    viewModel.pausePlayback()
                } else {
                    viewModel.resumePlayback()
                }
            }

            nextButton.setOnClickListener {
                // Handle next song
            }

            previousButton.setOnClickListener {
                // Handle previous song
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        binding.currentTime.text = formatDuration(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = false
                    seekBar?.progress?.let { progress ->
                        // Handle seeking to position
                    }
                }
            })

            backButton.setOnClickListener {
                requireActivity().onBackPressed()
            }

            moreButton.setOnClickListener { view ->
                viewModel.currentSong.value?.let { song ->
                    showSongOptionsMenu(song, view)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                binding.apply {
                    songTitle.text = it.title
                    artistName.text = it.artist
                    albumName.text = it.album

                    Glide.with(this@PlayerFragment)
                        .load(it.albumArtUri)
                        .placeholder(R.drawable.ic_music_note)
                        .error(R.drawable.ic_music_note)
                        .centerCrop()
                        .into(albumArt)
                }
            }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playPauseButton.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        // Observe playback position and duration
        // Update seekBar and time displays
    }

    private fun showSongOptionsMenu(song: com.musicplayer.models.Song, anchorView: View) {
        val popup = android.widget.PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.song_options_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_to_playlist -> {
                    // Handle add to playlist
                    true
                }
                R.id.action_share -> {
                    // Handle share
                    true
                }
                R.id.action_delete -> {
                    // Handle delete
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }

    private fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
