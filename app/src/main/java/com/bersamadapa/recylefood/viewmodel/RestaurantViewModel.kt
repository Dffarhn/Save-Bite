package com.bersamadapa.recylefood.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.Restaurant
import com.bersamadapa.recylefood.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestaurantViewModel(private val restaurantRepository: RestaurantRepository) : ViewModel() {

    // State to manage the restaurant list
    private val _restaurantListState = MutableStateFlow<RestaurantDataState>(RestaurantDataState.Idle)
    val restaurantListState: StateFlow<RestaurantDataState> get() = _restaurantListState

    // State to manage the restaurant detail
    private val _restaurantDetailState = MutableStateFlow<RestaurantDetailState>(RestaurantDetailState.Idle)
    val restaurantDetailState: StateFlow<RestaurantDetailState> get() = _restaurantDetailState

    // State to manage the current filter type (Rating or Distance)
    private val _restaurantFilterState = MutableStateFlow<RestaurantFilter>(RestaurantFilter.Rating)
    val restaurantFilterState: StateFlow<RestaurantFilter> get() = _restaurantFilterState

    // Function to fetch the list of restaurants ordered by rating or distance based on the selected filter
    fun fetchRestaurants(selectedButton: String, userLocation: Location? = null) {
        viewModelScope.launch {
            _restaurantListState.value = RestaurantDataState.Loading

            when (selectedButton) {
                "Terdekat" -> {
                    // Fetch and filter restaurants by distance
                    userLocation?.let { location ->
                        val result = restaurantRepository.getRestaurantsWithDistance(location)
                        result.fold(
                            onSuccess = { restaurants ->

                                val sortedRestaurants = restaurants.sortedBy { it.distance }
                                // Filter by distance < 10 km, or apply other criteria
                                _restaurantListState.value = RestaurantDataState.Success(sortedRestaurants)
                            },
                            onFailure = { exception ->
                                _restaurantListState.value = RestaurantDataState.Error(exception.message ?: "Unknown error")
                            }
                        )
                    }
                }
                "Terbaik" -> {

                    userLocation?.let { location ->
                        val result = restaurantRepository.getRestaurantsWithDistance(location)
                        result.fold(
                            onSuccess = { restaurants ->

                                val sortedRestaurants = restaurants.sortedByDescending { it.rating }
                                // Filter by distance < 10 km, or apply other criteria
                                _restaurantListState.value = RestaurantDataState.Success(sortedRestaurants)
                            },
                            onFailure = { exception ->
                                _restaurantListState.value = RestaurantDataState.Error(exception.message ?: "Unknown error")
                            }
                        )
                    }
                }
                else -> {
                    // Default state, you can decide if you need a fallback or show all restaurants
                    _restaurantListState.value = RestaurantDataState.Error("Invalid filter selection")
                }
            }
        }
    }

    // Function to fetch restaurant details with their products
    fun fetchRestaurantDetails(idRestaurant: String) {
        viewModelScope.launch {
            _restaurantDetailState.value = RestaurantDetailState.Loading
            val result = restaurantRepository.getRestaurantDetailsWithProducts(idRestaurant)

            result.fold(
                onSuccess = { restaurant ->
                    _restaurantDetailState.value = restaurant?.let {
                        RestaurantDetailState.Success(it)
                    } ?: RestaurantDetailState.Error("Restaurant not found")
                },
                onFailure = { exception ->
                    _restaurantDetailState.value = RestaurantDetailState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }
}

// Sealed class to manage different states for the restaurant list
sealed class RestaurantDataState {
    object Idle : RestaurantDataState() // No action taken yet
    object Loading : RestaurantDataState() // Fetching data from the repository
    data class Success(val restaurants: List<Restaurant>) : RestaurantDataState() // Successfully fetched data
    data class Error(val message: String) : RestaurantDataState() // Error during the data fetch
}

// Sealed class to manage different states for the restaurant detail
sealed class RestaurantDetailState {
    object Idle : RestaurantDetailState() // No action taken yet
    object Loading : RestaurantDetailState() // Fetching data from the repository
    data class Success(val restaurant: Restaurant) : RestaurantDetailState() // Successfully fetched restaurant detail
    data class Error(val message: String) : RestaurantDetailState() // Error during the data fetch
}

// Enum class to represent the type of filter being applied
enum class RestaurantFilter {
    Rating, Distance
}
