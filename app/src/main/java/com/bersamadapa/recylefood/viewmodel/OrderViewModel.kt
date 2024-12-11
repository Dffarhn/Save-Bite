package com.bersamadapa.recylefood.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.OrderResponse
import com.bersamadapa.recylefood.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    val TAG = "OrderViewModel"

    // State to manage the status of order creation
    private val _createOrderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val createOrderState: StateFlow<OrderState> get() = _createOrderState

    // Function to create an order
    fun createOrder(userId: String, orderRequest: OrderRequest) {
        viewModelScope.launch {
            _createOrderState.value = OrderState.Loading
            Log.d(TAG, "Creating order for userId: $userId with order request: $orderRequest")

            val result = orderRepository.createOrder(userId, orderRequest)
            result.fold(
                onSuccess = { orderResponse ->
                    Log.d(TAG, "Successfully created order: $orderResponse")
                    _createOrderState.value = OrderState.Success(orderResponse)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to create order: ${exception.message}")
                    _createOrderState.value = OrderState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }
}

// Sealed class to represent the state of order creation
sealed class OrderState {
    object Idle : OrderState() // No action taken yet
    object Loading : OrderState() // Creating order in progress
    data class Success(val orderResponse: OrderResponse) : OrderState() // Successfully created the order
    data class Error(val message: String) : OrderState() // Error during order creation
}
