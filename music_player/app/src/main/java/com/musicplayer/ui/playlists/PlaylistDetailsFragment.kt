package com.musicplayer.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.musicplayer.R
import com.musicplayer.adapters.SongAdapter
import com.musicplayer.databinding.FragmentPlaylistDetailsBinding
import com.musicplayer.viewmodels.PlaylistsViewModel

class PlaylistDetailsFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PlaylistsViewModel by viewModels()
    private val args: PlaylistDetailsFragmentArgs by navArgs()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
        observeViewModel()
        loadPlaylist()
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(
            onSongClick = { song ->
                findNavController().navigate(
                    PlaylistDetailsFragmentDirections.actionPlaylistDetailsToPlayer(song.id)
                )
            },
            onMoreClick = { song, view ->
                showSongOptionsMenu(song, view)
            }
        )

        binding.songsRecyclerView.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener {
            viewModel.playPlaylist(args.playlistId)
        }

        binding.shuffleButton.setOnClickListener {
            viewModel.shufflePlaylist(args.playlistId)
        }
    }

    private fun observeViewModel() {
        viewModel.getPlaylistById(args.playlistId).observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                binding.toolbar.title = it.name
                binding.playlistName.text = it.name
                binding.songCount.text = it.getSongCountText()
                binding.totalDuration.text = it.formatDuration()

                // Load playlist cover
                Glide.with(this)
                    .load(it.coverUri)
                    .placeholder(R.drawable.ic_playlist)
                    .error(R.drawable.ic_playlist)
                    .into(binding.playlistCover)
            }
        }

        viewModel.getPlaylistSongs(args.playlistId).observe(viewLifecycleOwner) { songs ->
            songAdapter.submitList(songs)
            updateEmptyView(songs.isEmpty())
        }
    }

    private fun loadPlaylist() {
        viewModel.loadPlaylist(args.playlistId)
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.songsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.playButton.isEnabled = !isEmpty
        binding.shuffleButton.isEnabled = !isEmpty
    }

    private fun showSongOptionsMenu(song: Song, anchorView: View) {
        PopupMenu(requireContext(), anchorView).apply {
            menuInflater.inflate(R.menu.playlist_song_options_menu, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_remove -> {
                        showRemoveSongDialog(song)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun showRemoveSongDialog(song: Song) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.remove_from_playlist)
            .setMessage(getString(R.string.remove_song_confirmation, song.title))
            .setPositiveButton(R.string.remove) { _, _ ->
                viewModel.removeSongFromPlaylist(args.playlistId, song.id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
