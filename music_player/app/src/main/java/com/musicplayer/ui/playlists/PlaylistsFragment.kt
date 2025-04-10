package com.musicplayer.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.musicplayer.R
import com.musicplayer.adapters.PlaylistAdapter
import com.musicplayer.databinding.FragmentPlaylistsBinding
import com.musicplayer.models.Playlist
import com.musicplayer.viewmodels.PlaylistsViewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PlaylistsViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(
            onPlaylistClick = { playlist ->
                // Navigate to playlist details
                findNavController().navigate(
                    PlaylistsFragmentDirections.actionPlaylistsToPlaylistDetails(playlist.id)
                )
            },
            onMoreClick = { playlist, view ->
                showPlaylistOptionsMenu(playlist, view)
            }
        )

        binding.playlistsRecyclerView.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupViews() {
        binding.createPlaylistButton.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
            updateEmptyView(playlists.isEmpty())
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.playlistsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showCreatePlaylistDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_playlist, null)
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.create_playlist)
            .setView(dialogView)
            .setPositiveButton(R.string.create) { _, _ ->
                val name = nameEditText.text?.toString()?.trim() ?: ""
                if (name.isNotEmpty()) {
                    viewModel.createPlaylist(name)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPlaylistOptionsMenu(playlist: Playlist, anchorView: View) {
        PopupMenu(requireContext(), anchorView).apply {
            menuInflater.inflate(R.menu.playlist_options_menu, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        showEditPlaylistDialog(playlist)
                        true
                    }
                    R.id.action_delete -> {
                        showDeletePlaylistDialog(playlist)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun showEditPlaylistDialog(playlist: Playlist) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_playlist, null)
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)
        nameEditText.setText(playlist.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_playlist)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = nameEditText.text?.toString()?.trim() ?: ""
                if (newName.isNotEmpty()) {
                    viewModel.updatePlaylist(playlist.copy(
                        name = newName,
                        updatedAt = java.util.Date()
                    ))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.delete_playlist_confirmation, playlist.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deletePlaylist(playlist)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
