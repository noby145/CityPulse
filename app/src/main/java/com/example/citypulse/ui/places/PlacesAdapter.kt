package com.example.citypulse.ui.places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.citypulse.R
import com.example.citypulse.data.remote.model.Place
import com.example.citypulse.databinding.ItemPlaceCardBinding
import kotlin.math.roundToInt

/**
 * RecyclerView adapter for displaying nearby places using Material 3 cards.
 * Supports in-memory search and category filtering without re-querying the API.
 */
class PlacesAdapter(
    private val onPlaceClick: (Place) -> Unit,
) : ListAdapter<Place, PlacesViewHolder>(DiffCallback) {
    private var allPlaces: List<Place> = emptyList()
    private var currentQuery: String = ""
    private var currentCategory: String = ""

    fun submitPlaces(places: List<Place>) {
        allPlaces = places
        submitList(applyFilter(places, currentQuery, currentCategory))
    }

    fun updateFilters(query: String, category: String) {
        currentQuery = query
        currentCategory = category
        submitList(applyFilter(allPlaces, currentQuery, currentCategory))
    }

    private fun applyFilter(places: List<Place>, query: String, category: String): List<Place> {
        val trimmedQuery = query.trim()
        val trimmedCategory = category.trim()
        if (trimmedQuery.isEmpty() && trimmedCategory.isEmpty()) return places

        return places.filter { place ->
            // SearchView query filters by place name only.
            val matchesQuery = trimmedQuery.isEmpty() ||
                place.name.contains(trimmedQuery, ignoreCase = true)

            val matchesCategory = trimmedCategory.isEmpty() ||
                place.kinds.contains(trimmedCategory, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding = ItemPlaceCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding, onPlaceClick)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem == newItem
    }
}

/**
 * ViewHolder for a place card showing name, category, and distance.
 */
class PlacesViewHolder(
    private val binding: ItemPlaceCardBinding,
    private val onPlaceClick: (Place) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(place: Place) {
        val context = binding.root.context
        val category = place.kinds.ifBlank { context.getString(R.string.unknown_category) }
        val safeDistanceMeters = place.distanceMeters.coerceAtLeast(0.0)

        binding.placeName.text = place.name.ifBlank { context.getString(R.string.unknown_category) }
        binding.placeCategory.text = context.getString(R.string.place_category_format, category)
        binding.placeDistance.text = if (safeDistanceMeters >= 1000.0) {
            context.getString(R.string.place_distance_format, safeDistanceMeters / 1000.0)
        } else {
            context.getString(R.string.place_distance_meters_format, safeDistanceMeters.roundToInt())
        }

        binding.root.setOnClickListener {
            onPlaceClick(place)
        }
    }
}

