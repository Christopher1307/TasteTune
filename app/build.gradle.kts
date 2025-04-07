plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // ✅ Añadir esto para soporte de Kapt
}

android {
    namespace = "com.example.tastetune"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tastetune"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

kapt { // ✅ Configuración necesaria para Room
    correctErrorTypes = true
}

dependencies {

    // Room Database (Base de datos local)
    implementation("androidx.room:room-runtime:2.5.0")
    implementation(libs.androidx.recyclerview)
    kapt("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")

    // ML Kit para Reconocimiento de Imágenes
    implementation("com.google.mlkit:image-labeling:17.0.9")

    // OkHttp para peticiones HTTP
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Reconocimiento de imágenes Clarifai
    implementation("org.json:json:20210307")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Dependencias de AndroidX y Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.image.labeling.custom.common)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
