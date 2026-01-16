plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.vitaalert.mobile.dev"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vitaalert.mobile.dev"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":auth"))
    implementation(project(":ble"))
    implementation(project(":designsystem"))
    implementation(project(":reports"))
}
