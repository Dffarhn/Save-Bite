package com.bersamadapa.recylefood.data.repository

import android.util.Log
import com.bersamadapa.recylefood.data.model.CartItem
import com.bersamadapa.recylefood.data.model.CartRequest
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.Restaurant
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CartRepository(private val firestore: FirebaseFirestore) {

    companion object {
        private const val TAG = "CartRepository"
        private const val USERS_COLLECTION = "users"
        private const val CART_COLLECTION = "cart"
    }

    // Add or update an item in the user's cart
    suspend fun addItemToCart(userId: String, cartRequest: CartRequest): Result<Unit> {
        Log.d(TAG, "Attempting to add/update cart item for user $userId with request: $cartRequest")
        return try {
            val cartCollectionRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)

            Log.d(TAG, "Checking if a cart document exists for restaurantId: ${cartRequest.restaurantId}")
            val querySnapshot = cartCollectionRef
                .whereEqualTo("restaurantId", cartRequest.restaurantId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val existingDoc = querySnapshot.documents.first()
                Log.d(TAG, "Found existing cart document: ${existingDoc.id}")

                val currentMysteryBox = existingDoc["mysteryBox"] as? List<String> ?: emptyList()
                val updatedMysteryBox = (currentMysteryBox + (cartRequest.mysteryBox?.get(0) ?: "N/A"))


                Log.d(TAG, "Updating mysteryBox for document: ${existingDoc.id} with new items: $updatedMysteryBox")
                cartCollectionRef.document(existingDoc.id).update("mysteryBox", updatedMysteryBox)
                    .await()

                Log.d(TAG, "Successfully updated cart item for restaurant ${cartRequest.restaurantId}.")
            } else {
                Log.d(TAG, "No existing document found. Adding a new cart document.")
                cartCollectionRef.add(cartRequest).await()
                Log.d(TAG, "Successfully added new cart item for restaurant ${cartRequest.restaurantId}.")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error adding/updating cart item for restaurant ${cartRequest.restaurantId}: ${e.message}",
                e
            )
            Result.failure(e)
        }
    }

    // Fetch all cart items for the user
    suspend fun getCartItems(userId: String): Result<List<CartItem>> {
        Log.d(TAG, "Fetching cart items for user $userId")
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .get()
                .await()

            Log.d(TAG, "Found ${snapshot.documents.size} cart documents for user $userId")

            val cartItems = snapshot.documents.mapNotNull { document ->
                try {
                    Log.d(TAG, "Processing document: ${document.id}")

                    val cartItem = document.toObject(CartItem::class.java)?.apply {
                        id = document.id // Assign Firestore document ID

                        // Fetch and map the restaurant data
                        val restaurantPath = document.getString("restaurantId")
                        restaurant = restaurantPath?.let { path ->
                            val restaurantId = path.split("/").lastOrNull()
                            restaurantId?.let { id ->
                                firestore.collection("restaurants").document(id).get().await()
                                    .toObject(Restaurant::class.java)?.apply { this.id = id }
                            }
                        }

                        // Fetch and map the mystery box data
                        val mysteryBoxIds = document.get("mysteryBox") as? List<*>
                        mysteryBoxData = mysteryBoxIds?.filterIsInstance<String>()?.mapNotNull { id ->
                            firestore.collection("mysterybox").document(id).get().await()
                                .toObject(MysteryBox::class.java)?.apply { this.id = id }
                        }
                    }

                    Log.d(TAG, "Mapped cart item: $cartItem")
                    cartItem
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing document ${document.id}: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "Successfully fetched ${cartItems.size} cart items for user $userId.")
            Result.success(cartItems)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cart items for user $userId: ${e.message}", e)
            Result.failure(e)
        }
    }

}
