package com.bersamadapa.recylefood.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.CategoryOrder
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.viewmodel.GetOrderByIdState
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryDetailScreen(
    navController: NavController? = null,
    orderId: String?
){

    val orderRepository = RepositoryProvider.orderRepository
    val factoryOrder = ViewModelFactory { OrderViewModel(orderRepository) }
    val viewModelOrder: OrderViewModel = viewModel(factory = factoryOrder)

    // Observe the state of fetching the order by ID
    val orderState by viewModelOrder.getOrderByIdState.collectAsState()


    // Trigger fetching the order when the screen is loaded
    LaunchedEffect(orderId) {
        if (!orderId.isNullOrEmpty()) {
            viewModelOrder.getOrderById(orderId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (orderState) {
                is GetOrderByIdState.Loading -> {
                    // Show loading UI
                    Text(text = "Loading order details...", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is GetOrderByIdState.Success -> {
                    // Show the order details
                    val order = (orderState as GetOrderByIdState.Success).order

                    // List of Items
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        item {
                            order.mysteryBoxsData?.map { it ->
                                it.restaurantData?.let { restaurant ->
                                    it.price?.let { price ->
                                        formatCurrency(price.toDouble())?.let { formattedPrice ->
                                            ItemRow(
                                                imageRes = R.drawable.mystery_box,
                                                title = restaurant.name,
                                                price = formattedPrice
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment Details Section
                    order.totalAmount?.toDouble()?.let { total ->
                        formatCurrency(total)?.let {
                            DetailsSectionOnGoingOrder(
                                createdAt = order.createdAt,
                                total = it
                            )
                        }
                    }

                    // Center QR Code or Order Status Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (order.categoryOrder == CategoryOrder.Personal) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_save_bite),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .height(80.dp)
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Pembelian Anda Sudah DiSerahkan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Pengantaran Donasi Anda Sudah Selesai ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = order.proveDeliveryDonasi?.url,
                                        placeholder = painterResource(R.drawable.loading_placeholder),
                                        error = painterResource(R.drawable.logo_save_bite)
                                    ),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
                is GetOrderByIdState.Error -> {
                    // Show error UI
                    val errorMessage = (orderState as GetOrderByIdState.Error).message
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                GetOrderByIdState.Idle -> {
                    // Show a placeholder or default state if needed
                    Text(text = "No order data available", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

// Details Section

