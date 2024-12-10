package com.bersamadapa.recylefood.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.OrderData
import com.bersamadapa.recylefood.ui.component.orderhistory.OngoingOrderItem

@Composable
fun OngoingOrders(modifier: Modifier = Modifier) {
    val ongoingOrders = listOf(
        OrderData("McDonald's", 20000, R.drawable.logo_save_bite),
        OrderData("Hokben", 15000, R.drawable.logo_save_bite)
    )

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Pesanan Sedang Diproses",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(ongoingOrders.size) { index ->
                OngoingOrderItem(order = ongoingOrders[index])
            }
        }
    }
}
