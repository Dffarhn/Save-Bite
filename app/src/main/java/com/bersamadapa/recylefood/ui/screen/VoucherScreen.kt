package com.bersamadapa.recylefood.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun VoucherScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Voucher") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle text input */ },
                placeholder = { Text("Masukkan kode voucher kamu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    IconButton(onClick = { /* Clear text */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Clear"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Voucher List Header
            Text(
                text = "Cashback/Diskon",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Voucher List
            val vouchers = listOf(
                "Diskon 100% s/d Rp20RB",
                "Diskon 100% s/d Rp20RB",
                "Diskon 100% s/d Rp20RB",
                "Diskon 100% s/d Rp20RB",
                "Diskon 100% s/d Rp20RB"
            )
            val selectedVoucher = remember { mutableIntStateOf(-1) }

            LazyColumn {
                itemsIndexed(vouchers) { index, voucher ->
                    VoucherItem(
                        voucher = voucher,
                        isSelected = selectedVoucher.value == index,
                        onSelect = { selectedVoucher.value = index }
                    )
                    if (index < vouchers.size - 1) {
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherItem(
    voucher: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onSelect() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_voucher), // Replace with your voucher icon resource
            contentDescription = "Voucher Icon",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = voucher,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.brown)
            )
            Text(
                text = "Metode pembayaran Save Wallet",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(id = R.color.brown), // Brown color
                unselectedColor = colorResource(id = R.color.brown)
            )
        )
    }
}

@Composable
fun BottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.brown)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_money),
                contentDescription = "Voucher",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column() {
                Text(
                    text = "Total",
                    fontSize = 12.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Rp20.000", // Replace with dynamic value
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Button(
            onClick = { /* Handle payment */ },
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.cream)), // Yellow color
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                text = "Bayar",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

