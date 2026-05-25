package com.example.citypulse.ui.places

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.citypulse.databinding.FragmentPlaceDetailBinding
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

class PlaceDetailFragment : Fragment() {
    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.placeNotesEditText.doAfterTextChanged { editable ->
            val notes = editable?.toString().orEmpty()
            if (notes != viewModel.currentNotes) {
                viewModel.updateNotes(notes)
            }
        }

        binding.placeFavoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.placeShareButton.setOnClickListener {
            sharePlace(viewModel.buildShareText())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderPlaceDetails(state)
                }
            }
        }
    }

    private fun renderPlaceDetails(state: PlaceDetailUiState) {
        val safeDistanceMeters = state.distanceMeters.coerceAtLeast(0.0)
        val addressText = state.address?.takeIf { it.isNotBlank() }
            ?: getString(com.example.citypulse.R.string.place_address_not_available)
        val categoryText = state.placeCategory.ifBlank {
            getString(com.example.citypulse.R.string.unknown_category)
        }

        binding.placeDetailName.text = state.placeName.ifBlank {
            getString(com.example.citypulse.R.string.unknown_category)
        }
        binding.placeDetailCategory.text = getString(
            com.example.citypulse.R.string.place_category_detail_label,
            categoryText,
        )
        binding.placeDetailAddress.text = getString(
            com.example.citypulse.R.string.place_address_label,
            addressText,
        )
        binding.placeDetailDistance.text = if (safeDistanceMeters >= 1000.0) {
            getString(com.example.citypulse.R.string.place_distance_format, safeDistanceMeters / 1000.0)
        } else {
            getString(com.example.citypulse.R.string.place_distance_meters_format, safeDistanceMeters.roundToInt())
        }

        binding.placeDetailCoordinates.text = getString(
            com.example.citypulse.R.string.place_coordinates_label,
            state.latitude,
            state.longitude,
        )

        binding.placeDetailId.text = getString(com.example.citypulse.R.string.place_id_label, state.placeId)
        binding.placeDetailWikidata.text = state.wikidata?.takeIf { it.isNotBlank() }?.let {
            getString(com.example.citypulse.R.string.place_wikidata_label, it)
        } ?: getString(com.example.citypulse.R.string.place_wikidata_not_available)

        binding.placeNotesInputLayout.helperText = getString(
            com.example.citypulse.R.string.place_notes_helper_text,
            state.notes.length,
        )

        if (binding.placeNotesEditText.text?.toString() != state.notes) {
            binding.placeNotesEditText.setText(state.notes)
            binding.placeNotesEditText.setSelection(state.notes.length)
        }

        setFavoriteButtonState(binding.placeFavoriteButton, state.isFavorite)
    }

    private fun setFavoriteButtonState(button: MaterialButton, isFavorite: Boolean) {
        button.text = getString(
            if (isFavorite) {
                com.example.citypulse.R.string.remove_from_favorites
            } else {
                com.example.citypulse.R.string.add_to_favorites
            },
        )
        button.setIconResource(
            if (isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            },
        )
    }

    private fun sharePlace(shareText: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(com.example.citypulse.R.string.place_details_title))
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(com.example.citypulse.R.string.share_place)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
