package com.bersamadapa.recylefood.data.repository


import android.util.Log
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.OrderResponse
import com.bersamadapa.recylefood.network.api.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class OrderRepository(private val firestoreInstance: FirebaseFirestore, private val apiService: ApiService) {

    suspend fun createOrder(
        userId: String,
        orderRequest: OrderRequest
    ): Result<OrderResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert order data into RequestBody or use Multipart for files if needed

                // Make the API call to create an order using Retrofit's suspend function
                val response = apiService.createOrder(
                    userId = userId,
                    orderRequest = orderRequest
                )

                // Assuming `ApiResponse<Order>` is the return type and it includes a `statusCode` and `data`
                if (response.statusCode == 201) {
                    Log.d("OrderRepository", "Order created successfully: ${response.data}")
                    Result.success(response.data)
                } else {
                    Log.e("OrderRepository", "Failed to create order: ${response.message}")
                    Result.failure(Exception("Failed to create order: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error creating order", e)
                Result.failure(e)
            }
        }
    }

}
