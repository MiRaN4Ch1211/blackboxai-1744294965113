package com.musicplayer.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.musicplayer.R
import com.musicplayer.adapters.SongAdapter
import com.musicplayer.databinding.FragmentLibraryBinding
import com.musicplayer.models.Song
import com.musicplayer.viewmodels.MusicViewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MusicViewModel by viewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchInput()
        setupTabLayout()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(
            onSongClick = { song ->
                viewModel.playSong(song)
            },
            onMoreClick = { song, view ->
                showSongOptionsMenu(song, view)
            }
        )

        binding.songsRecyclerView.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            val filteredSongs = viewModel.searchSongs(query)
            updateSongsList(filteredSongs)
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.songs.value?.let { updateSongsList(it) }
                    1 -> viewModel.recentlyPlayed.value?.let { updateSongsList(it) }
                    2 -> viewModel.mostPlayed.value?.let { updateSongsList(it) }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            updateSongsList(songs)
        }

        viewModel.currentSong.observe(viewLifecycleOwner) { song ->
            songAdapter.setCurrentSong(song)
        }
    }

    private fun updateSongsList(songs: List<Song>) {
        songAdapter.submitList(songs)
        binding.emptyView.visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showSongOptionsMenu(song: Song, anchorView: View) {
        // Implement popup menu for song options (add to playlist, share, delete, etc.)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
