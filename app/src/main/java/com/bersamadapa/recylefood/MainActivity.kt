package com.bersamadapa.recylefood

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.ui.navigation.AppNavGraph
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.ui.screen.LoadingScreen
import com.bersamadapa.recylefood.ui.theme.RecyleFoodTheme
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.utils.QrScannerHelper
import android.Manifest
import android.os.Build
import com.bersamadapa.recylefood.network.socket.SocketManager
import com.bersamadapa.recylefood.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController
    private lateinit var locationHelper: LocationHelper
    private var locationPermissionGranted = false
    private var phoneStatePermissionGranted = false
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var socketManager: SocketManager
    private lateinit var notificationHelper: NotificationHelper


    // Register for location permission result
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            locationPermissionGranted = isGranted
            if (!isGranted) {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    // Register for phone state permission result
    private val requestPhoneStatePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            phoneStatePermissionGranted = isGranted
            if (!isGranted) {
                Toast.makeText(this, "Phone state permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPostNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with notifications
            } else {
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)
        notificationHelper = NotificationHelper(this)  // Initialize notificationHelper here
        setContent {
            RecyleFoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()

                    // Loading state to wait until the userId is fetched
                    val isUserChecked = remember { mutableStateOf(false) }
                    val userId = remember { mutableStateOf<String?>(null) }

                    // Check user login status
                    LaunchedEffect(Unit) {
                        dataStoreManager.userId.collect { id ->
                            userId.value = id
                            isUserChecked.value = true
                        }
                    }

                    // Initialize SocketManager with user ID
                    socketManager = SocketManager(userId.value)

                    // Connect to the server
                    socketManager.connect()

                    // Listen for notifications
                    socketManager.listenForNotifications { title, message ->
                        // Handle incoming notification

                        // Show a custom notification using NotificationHelper (you can also use title and message)
                        notificationHelper.showNotification(title, message)
                    }


                    // Only show navigation after userId is checked
                    if (isUserChecked.value) {
                        val navigateTo = intent.getStringExtra("navigateTo") // Get the intent extra

                        // Set up the NavGraph
                        AppNavGraph(
                            navController = navController as NavHostController,
                            userId = userId.value
                        )

                        // Navigate to OrderHistoryScreen if the intent requests it
                        LaunchedEffect(navigateTo) {
                            if (navigateTo == "orderHistory") {
                                (navController as NavHostController).navigate("orderHistory")
                            }
                        }
                    } else {
                        // Show a loading screen or just a blank screen while checking
                        LoadingScreen()
                    }
                }
            }
        }

        // Initialize the LocationHelper and FusedLocationProviderClient
        locationHelper = LocationHelper(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPostNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Request location permission on launch if not already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Request phone state permission on launch if not already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            phoneStatePermissionGranted = true
        } else {
            requestPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    // Function to launch the QR scanner
    fun launchQrScanner() {
        QrScannerHelper.launchQrScanner(this) // Call the helper to launch the scanner
    }

    // Handle QR scanner result (delegate to the helper)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        QrScannerHelper.handleQrResult(this, requestCode, resultCode, data, navController)
    }
}
