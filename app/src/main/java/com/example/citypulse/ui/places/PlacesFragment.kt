package com.example.citypulse.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citypulse.data.remote.model.Place
import com.example.citypulse.databinding.FragmentPlacesBinding
import kotlinx.coroutines.launch

/**
 * Example Fragment demonstrating how to use the PlacesViewModel to fetch and display nearby places.
 * This is a reference implementation - customize the UI layout and adapter as needed.
 */
class PlacesFragment : Fragment() {
    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory(requireActivity().application)
    }
    private lateinit var placesAdapter: PlacesAdapter
    private var currentSearchQuery: String = ""
    private var currentCategoryFilter: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Example: Load nearby places for a specific location
        viewModel.getNearbyPlaces(
            latitude = 37.7749,      // San Francisco
            longitude = -122.4194,
            radius = 5000,
            kinds = "natural,historic,museum",
            limit = 30,
        )
    }

    private fun setupRecyclerView() {
        placesAdapter = PlacesAdapter { place ->
            navigateToPlaceDetails(place)
        }
        binding.placesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placesAdapter
            setHasFixedSize(true)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchQuery = query.orEmpty()
                placesAdapter.updateFilters(currentSearchQuery, currentCategoryFilter)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty()
                placesAdapter.updateFilters(currentSearchQuery, currentCategoryFilter)
                return true
            }
        })

        bindCategoryChip(binding.chipAll, "")
        bindCategoryChip(binding.chipRestaurants, "restaurants")
        bindCategoryChip(binding.chipMuseums, "museums")
        bindCategoryChip(binding.chipParks, "parks")
        bindCategoryChip(binding.chipShops, "shops")

        placesAdapter.updateFilters(currentSearchQuery, currentCategoryFilter)
    }

    private fun bindCategoryChip(chip: View, category: String) {
        chip.setOnClickListener {
            currentCategoryFilter = category
            placesAdapter.updateFilters(currentSearchQuery, currentCategoryFilter)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    placesAdapter.submitPlaces(state.places)

                    if (state.errorMessage.isNotEmpty()) {
                        showError(state.errorMessage)
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToPlaceDetails(place: Place) {
        val direction = PlacesFragmentDirections.actionPlacesFragmentToPlaceDetailFragment(
            placeId = place.id,
            placeName = place.name,
            placeCategory = place.kinds,
            address = place.address,
            latitude = place.latitude.toFloat(),
            longitude = place.longitude.toFloat(),
            distanceMeters = place.distanceMeters.toFloat(),
            wikidata = place.wikidata,
        )
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

