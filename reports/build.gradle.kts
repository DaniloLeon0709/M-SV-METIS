plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.vitaalert.reports"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

/**
 * Stub module for future reports features.
 */
