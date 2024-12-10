package com.bersamadapa.recylefood.ui.component.orderhistory


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bersamadapa.recylefood.data.model.OrderData

@Composable
fun OngoingOrderItem(order: OrderData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = order.iconRes),
            contentDescription = "${order.restaurant} logo",
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.restaurant,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rp${order.price}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Text(
            text = "Sedang Diproses",
            fontSize = 14.sp,
            color = Color(0xFF007BFF),
            fontWeight = FontWeight.SemiBold
        )
    }
}
