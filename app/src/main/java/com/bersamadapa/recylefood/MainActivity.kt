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
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bersamadapa.recylefood.ui.navigation.AppNavGraph
import com.bersamadapa.recylefood.ui.theme.RecyleFoodTheme
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.utils.QrScannerHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController
    private lateinit var locationHelper: LocationHelper
    private var locationPermissionGranted = false

    // Register for permission result
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationPermissionGranted = true
                // Permission granted, now location can be fetched when required
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyleFoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Initialize the navController here so it is accessible globally
                    navController = rememberNavController()

                    // Pass the navController to the AppNavGraph
                    AppNavGraph(navController = navController as NavHostController)
                }
            }
        }

        // Initialize the LocationHelper and FusedLocationProviderClient
        locationHelper = LocationHelper(this)

        // Request location permission on launch if not already granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true // Permission already granted
        } else {
            // Request permission
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    // Function to launch the QR scanner
    fun launchQrScanner() {
        QrScannerHelper.launchQrScanner(this) // Call the helper to launch the scanner
    }

    // Handle QR scanner result (delegate to the helper)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the initialized navController here
        QrScannerHelper.handleQrResult(this, requestCode, resultCode, data, navController)
    }
}
