plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)


}

android {
    namespace = "com.example.coincompass"
    compileSdk =34

    defaultConfig {
        applicationId = "com.example.coincompass"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

kotlin {
    jvmToolchain(17)
}


dependencies {

    dependencies {
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1") // <- DOWNGRADED from 1.7.0
        implementation("com.google.android.material:material:1.11.0") // <- DOWNGRADED from 1.12.0
        implementation("androidx.constraintlayout:constraintlayout:2.1.4") // <- DOWNGRADED from 2.2.1

        implementation("com.google.android.gms:play-services-wallet:19.4.0")
        testImplementation("junit:junit:4.13.2")

        androidTestImplementation("androidx.test.ext:junit:1.1.5") // <- DOWNGRADED from 1.2.1
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // <- DOWNGRADED from 3.6.1


        implementation(libs.room.runtime)
        implementation(libs.room.ktx)
        ksp(libs.room.compiler)

    }
}