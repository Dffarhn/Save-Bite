plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Google Services plugin
    kotlin("android") // Kotlin plugin for Android
}

android {
    namespace = "com.bersamadapa.recylefood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bersamadapa.recylefood"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    //Retrofit
    // Retrofit for networking
    implementation (libs.retrofit)
    // Gson converter for Retrofit to parse JSON responses
    implementation (libs.converter.gson)
    // OkHttp for logging (optional but useful for debugging)
    implementation (libs.logging.interceptor)

    // Other necessary libraries
    implementation (libs.androidx.datastore.preferences)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg) // For SVG support

    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.lifecycle)

// ML Kit Barcode Scanning

    implementation (libs.com.journeyapps.zxing.android.embedded)






    implementation(libs.jbcrypt)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.generativeai)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.location)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Apply the Google Services plugin (Make sure this is at the end)
apply(plugin = "com.google.gms.google-services")
