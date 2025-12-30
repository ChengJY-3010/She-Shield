plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // This applies the Firebase plugin to your app module
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.grpassignment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.grpassignment"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ... keep your existing build types and compile options ...
}

dependencies {
    // Direct library references to bypass version catalog issues
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Firebase configuration
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
}