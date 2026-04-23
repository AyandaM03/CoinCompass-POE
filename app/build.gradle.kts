plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.opsc_code"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.opsc_code"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
        // Core Android
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.recyclerview)
        implementation(libs.androidx.cardview)

        // Lifecycle
        implementation(libs.androidx.lifecycle.runtime.ktx.v2100)

        // Compose (REQUIRED for your theme files)
        implementation(libs.androidx.activity.compose.v1130)
        implementation(platform(libs.androidx.compose.bom.v20240600))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)

        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        // Firebase (NO DUPLICATES)
        implementation(platform(libs.firebase.bom.v3312))
        implementation(libs.google.firebase.auth)
        implementation(libs.google.firebase.firestore)
        implementation(libs.google.firebase.storage)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit.v130)
        androidTestImplementation(libs.androidx.espresso.core.v370)
    }
