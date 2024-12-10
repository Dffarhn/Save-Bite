package com.bersamadapa.recylefood.data.repository

import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.bersamadapa.recylefood.data.model.Product
import com.bersamadapa.recylefood.data.model.Restaurant
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class RestaurantRepository(private val firestore: FirebaseFirestore) {


    companion object {
        private const val TAG = "RestaurantRepository"
        private const val RESTAURANTS_COLLECTION = "restaurants"
        private const val PRODUCTS_SUBCOLLECTION = "products"
    }

    // Fetch all restaurants with products
//    suspend fun getRestaurantsWithProducts(): Result<List<Restaurant>> = try {
//        val restaurantSnapshot = fetchCollection(RESTAURANTS_COLLECTION)
//
//        val restaurants = restaurantSnapshot?.documents?.mapNotNull { document ->
//            document.toObject(Restaurant::class.java)?.apply {
//                id = document.id // Assign the document ID
//                products = fetchSubCollection(document.id, PRODUCTS_SUBCOLLECTION) // Fetch products for each restaurant
//            }
//        }.orEmpty()
//
//        Result.success(restaurants)
//    } catch (e: Exception) {
//        Log.e(TAG, "Error fetching restaurants with products: ${e.message}", e)
//        Result.failure(e)
//    }

    // Fetch restaurant details with products
    suspend fun getRestaurantDetailsWithProducts(idRestaurant: String): Result<Restaurant?> {
        return try {
            val restaurant = fetchDocument(RESTAURANTS_COLLECTION, idRestaurant)?.toObject(Restaurant::class.java)
            val products = fetchSubCollection(idRestaurant, PRODUCTS_SUBCOLLECTION)

            // Map the documents to Product objects
            val productList = products.mapNotNull { document ->
                val product = document.toObject(Product::class.java)
                // Log if the product mapping fails
                if (product == null) {
                    Log.w(TAG, "Product mapping failed for document: ${document.id}")
                }
                product
            }
            if (restaurant != null) {
                restaurant.products = productList
            }

            if (restaurant == null) {
                val message = "Restaurant not found: $idRestaurant"
                Log.w(TAG, message)
                return Result.failure(Exception(message))
            }

            Log.d("isi restaurant", restaurant.toString())

            Result.success(restaurant)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching restaurant details with products: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Fetch restaurants ordered by rating
    suspend fun getRestaurantsOrderByRating(): Result<List<Restaurant>> = try {
        val restaurantSnapshot = firestore.collection(RESTAURANTS_COLLECTION)
            .orderBy("rating", Query.Direction.DESCENDING)
            .get()
            .await()

        val restaurants = restaurantSnapshot.documents.mapNotNull { document ->
            document.toObject(Restaurant::class.java)?.apply { id = document.id
            }

        }

        Result.success(restaurants)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching restaurants ordered by rating: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getRestaurantsWithDistance(userLocation: Location): Result<List<Restaurant>> = try {
        // Fetch the restaurant data
        val restaurantSnapshot = firestore.collection(RESTAURANTS_COLLECTION).get().await()

        // Map the documents to Restaurant objects and calculate the distance from user
        val restaurants = restaurantSnapshot.documents.mapNotNull { document ->
            val restaurant = document.toObject(Restaurant::class.java)?.apply {
                id = document.id
                products = fetchSubCollection(id, PRODUCTS_SUBCOLLECTION).mapNotNull {
                    product -> product.toObject(Product::class.java)
                } // Fetch products for each restaurant

            }

            if (restaurant != null) {

                Log.d("User Location", "Lat: ${userLocation.latitude}, Lon: ${userLocation.longitude}")
                Log.d("Restaurant Location", "Lat: ${restaurant.location?.latitude}, Lon: ${restaurant.location?.longitude}")




                val distance = restaurant.location?.longitude?.let {
                    restaurant.location?.latitude?.let { it1 ->
                        calculateDistance(
                            userLocation.latitude,
                            userLocation.longitude,
                            it1,
                            it,)
                    }
                }
                Log.d("distance restaurant", distance.toString())

                restaurant.distance = distance
            }
            restaurant
        }.orEmpty()

        Result.success(restaurants)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching restaurants with distance: ${e.message}", e)
        Result.failure(e)
    }

    private fun calculateDistance(userLatitude: Double, userLongitude: Double, restaurantLatitude:Double, restaurantLongitude:Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude, results)
        return results[0] // Distance in meters
    }

    // Utility function to fetch a collection
    private suspend fun fetchCollection(collectionName: String) = try {
        firestore.collection(collectionName).get().await()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching collection $collectionName: ${e.message}", e)
        null
    }

    // Utility function to fetch a single document
    private suspend fun fetchDocument(collectionName: String, documentId: String) = try {
        firestore.collection(collectionName).document(documentId).get().await()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching document $documentId in $collectionName: ${e.message}", e)
        null
    }

    // Utility function to fetch a sub-collection
    private suspend fun fetchSubCollection(idRestaurant: String, subCollection: String): List<DocumentSnapshot> {
        return try {
            val subCollectionRef = FirebaseFirestore.getInstance()
                .collection(RESTAURANTS_COLLECTION)
                .document(idRestaurant)
                .collection(subCollection)
            val querySnapshot = subCollectionRef.get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching subcollection for restaurant: $idRestaurant", e)
            emptyList()
        }
    }

}