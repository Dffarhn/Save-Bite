package com.bersamadapa.recylefood.ui.component.orderhistory


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bersamadapa.recylefood.data.model.OrderData

@Composable
fun OrderItem(order: OrderData) {
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

        Button(
            onClick = { /* Action */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDED9D9))
        ) {
            Text(
                text = "Beli Lagi",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
