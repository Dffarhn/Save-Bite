package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.OrderData
import com.bersamadapa.recylefood.ui.component.orderhistory.OrderItem

@Composable
fun OrderHistory(modifier: Modifier = Modifier) {
    val orders = listOf(
        OrderData("McDonald's", 20000, R.drawable.logo_save_bite),
        OrderData("Hokben", 20000, R.drawable.logo_save_bite)
    )

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "History Order", modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn {
            items(orders.size) { index ->
                OrderItem(order = orders[index])
            }
        }
    }
}
