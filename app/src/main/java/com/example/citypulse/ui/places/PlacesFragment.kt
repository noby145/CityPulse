package com.example.citypulse.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citypulse.databinding.FragmentPlacesBinding
import com.example.citypulse.data.remote.model.Place

/**
 * Example Fragment demonstrating how to use the PlacesViewModel to fetch and display nearby places.
 * This is a reference implementation - customize the UI layout and adapter as needed.
 */
class PlacesFragment : Fragment() {
    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlacesViewModel by viewModels()
    private lateinit var placesAdapter: PlacesAdapter

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
        placesAdapter = PlacesAdapter(emptyList())
        binding.placesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (state.places.isNotEmpty()) {
                placesAdapter.updatePlaces(state.places)
            }

            if (state.errorMessage.isNotEmpty()) {
                showError(state.errorMessage)
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Simple RecyclerView adapter for displaying places.
 */
class PlacesAdapter(private var places: List<Place>) : RecyclerView.Adapter<PlacesViewHolder>() {
    fun updatePlaces(newPlaces: List<Place>) {
        places = newPlaces
        notifyItemRangeChanged(0, places.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PlacesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size
}

/**
 * ViewHolder for a place item showing name and location.
 */
class PlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(place: Place) {
        // This uses Android's built-in simple_list_item_2 layout with text1 and text2
        // Customize with your own layout file as needed
        val text1: android.widget.TextView? = itemView.findViewById(android.R.id.text1)
        val text2: android.widget.TextView? = itemView.findViewById(android.R.id.text2)

        text1?.text = place.name
        text2?.text = itemView.context.getString(
            com.example.citypulse.R.string.place_location_format,
            place.latitude,
            place.longitude,
            place.kinds,
        )
    }
}

