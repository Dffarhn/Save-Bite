package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val cartItems = listOf(
        CartItemData("Ayam Pak Gembus", "Mystery Box Paket 1", "Rp20.000", "Jalan Kaliurang KM 12"),
        CartItemData("Parsley", "Mystery Box Roti Kering", "Rp40.000", "Jalan Kaliurang KM 13"),
        CartItemData("Parsley", "Mystery Box Roti Tawar", "Rp20.000", "Jalan Kaliurang KM 13")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Keranjang") },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomCartBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            var currentRestaurant = ""
            cartItems.forEachIndexed { index, cartItem ->
                val isFirstItemOfRestaurant = cartItem.restaurantName != currentRestaurant
                if (isFirstItemOfRestaurant) {
                    if (index != 0) {
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    }
                    currentRestaurant = cartItem.restaurantName
                }

                CartItem(
                    restaurantName = if (isFirstItemOfRestaurant) cartItem.restaurantName else null,
                    itemName = cartItem.itemName,
                    price = cartItem.price,
                    location = cartItem.location
                )
            }
        }
    }
}

@Composable
fun CartItem(
    restaurantName: String?,
    itemName: String,
    price: String,
    location: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Restaurant Name Section
        restaurantName?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = { /* Handle checkbox state */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.brown)
                )
            }
        }

        // Item Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = false,
                onCheckedChange = { /* Handle checkbox state */ }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.mystery_box),
                contentDescription = "Item Image",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.brown)
                )
                Text(
                    text = price,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.brown)
                )
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Item Count Section
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Decrease quantity */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "Decrease",
                        tint = colorResource(id = R.color.brown),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "1", // Replace "1" with dynamic quantity if needed
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.brown)
                )
                IconButton(onClick = { /* Increase quantity */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Increase",
                        tint = colorResource(id = R.color.brown)
                    )
                }
            }
        }
    }
}

data class CartItemData(
    val restaurantName: String,
    val itemName: String,
    val price: String,
    val location: String
)


@Composable
fun BottomCartBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.brown))
            .padding(top = 8.dp)
    ) {
        // Top Section: Voucher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_voucher),
                contentDescription = "Voucher",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Voucher SaveBite",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Masukkan atau pilih voucher mu",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Section: Total Hemat and Payment Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_money),
                contentDescription = "Voucher",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Total Hemat Section
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rp0",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

            }
            // Bayar Button Section
            Button(
                onClick = { /* Handle payment */ },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.cream)),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text(text = "Bayar (0)", color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}


