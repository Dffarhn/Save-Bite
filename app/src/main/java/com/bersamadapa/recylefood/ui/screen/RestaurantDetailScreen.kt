package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.product.ProductCardDetail
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.viewmodel.RestaurantDetailState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@Composable
fun RestaurantDetailScreen(
    idRestaurant: String, // Restaurant ID to pass the data
    navController: NavController
) {

    // Provide the repository and create the ViewModel using the factory
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factory = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModel: RestaurantViewModel = viewModel(factory = factory)

    // Collect the current state of restaurant data
    val restaurantDataState by viewModel.restaurantDetailState.collectAsState()

    // Trigger the data fetching only when the state is Idle
    if (restaurantDataState is RestaurantDetailState.Idle) {
        viewModel.fetchRestaurantDetails(idRestaurant)
    }

    // Scrollable state for the content
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF6EA))
            .verticalScroll(scrollState)
    ) {

        Row(
            modifier = Modifier.padding(16.dp, top = 32.dp)
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically, // Align icon and text vertically
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between the icon and text
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with your desired icon
                contentDescription = "Back Icon",
                modifier = Modifier.size(24.dp) // Adjust icon size if needed
            )
            Text(
                "Kembali",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        when (val state = restaurantDataState) {
            is RestaurantDetailState.Loading -> {
                LoadingScreen(message = "Fetching restaurant details...", Modifier.align(Alignment.CenterHorizontally))
            }
            is RestaurantDetailState.Error -> {
                // Show error message
                Text(
                    text = "Error: ${state.message}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is RestaurantDetailState.Success -> {
                val restaurant = state.restaurant

                Spacer(modifier = Modifier.height(16.dp))


                // Restaurant Picture (Placeholder for now)
                // Restaurant Picture
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val imageUrl = restaurant.profilePicture?.url

                    if (imageUrl != null) { // Only load image if URL is available
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = imageUrl,
                                placeholder = painterResource(R.drawable.loading_placeholder),
                                error = painterResource(R.drawable.mystrey_box)
                            ),
                            contentDescription = "Image of ${restaurant.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Display placeholder if image URL is null
                        Image(
                            painter = painterResource(R.drawable.baked_goods_3),
                            contentDescription = "Placeholder for restaurant image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Restaurant Address
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                )

                // Restaurant Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 16.dp,horizontal = 16.dp)
                ) {
                    Text(
                        text = "Rating:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Adds spacing between the label and the rating
                    Text(
                        text = restaurant.rating?.toString() ?: "Unknown rating",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (restaurant.rating != null) { // Only show the star icon if rating is available
                        Spacer(modifier = Modifier.width(4.dp)) // Adds spacing between the rating and the icon
                        Icon(
                            imageVector = Icons.Default.Star,
                            tint = Color.Yellow,
                            contentDescription = "Star Icon",
                            modifier = Modifier.size(16.dp) // Adjust icon size
                        )
                    }
                }



                // Products Section
                if (restaurant.products.isNullOrEmpty()) {
                    Text(
                        text = "No products available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            tint = Color.Black,
                            contentDescription = "Star Icon",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(bottom = 8.dp) // Adjust icon size
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Menu Items",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                    }


                    // Loop through the products list and display each product card
                    restaurant.products?.forEach { product ->
                        ProductCardDetail(product = product)
                    }
                }
            }
            else -> {
                // Handle other states, if necessary (like Idle state)
            }
        }
    }
}

