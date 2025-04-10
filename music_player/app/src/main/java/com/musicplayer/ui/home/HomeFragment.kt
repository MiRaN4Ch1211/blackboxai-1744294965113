package com.musicplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.musicplayer.adapters.SongAdapter
import com.musicplayer.databinding.FragmentHomeBinding
import com.musicplayer.viewmodels.MusicViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MusicViewModel by viewModels()
    private lateinit var recentlyPlayedAdapter: SongAdapter
    private lateinit var mostPlayedAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        recentlyPlayedAdapter = SongAdapter(
            onSongClick = { song ->
                viewModel.playSong(song)
            },
            onMoreClick = { song, view ->
                showSongOptionsMenu(song, view)
            }
        )

        mostPlayedAdapter = SongAdapter(
            onSongClick = { song ->
                viewModel.playSong(song)
            },
            onMoreClick = { song, view ->
                showSongOptionsMenu(song, view)
            }
        )

        binding.recentlyPlayedRecyclerView.apply {
            adapter = recentlyPlayedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        binding.mostPlayedRecyclerView.apply {
            adapter = mostPlayedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.recentlyPlayed.observe(viewLifecycleOwner) { songs ->
            recentlyPlayedAdapter.submitList(songs)
            binding.recentlyPlayedEmpty.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.mostPlayed.observe(viewLifecycleOwner) { songs ->
            mostPlayedAdapter.submitList(songs)
            binding.mostPlayedEmpty.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            recentlyPlayedAdapter.setCurrentSong(song)
            mostPlayedAdapter.setCurrentSong(song)
        }
    }

    private fun showSongOptionsMenu(song: com.musicplayer.models.Song, anchorView: View) {
        val popup = android.widget.PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(com.musicplayer.R.menu.song_options_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                com.musicplayer.R.id.action_add_to_playlist -> {
                    // Handle add to playlist
                    true
                }
                com.musicplayer.R.id.action_share -> {
                    // Handle share
                    true
                }
                com.musicplayer.R.id.action_delete -> {
                    // Handle delete
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
