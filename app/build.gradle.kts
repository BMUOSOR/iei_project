// C:/Users/Pc/StudioProjects/iei_project/app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // <-- ADD THIS LINE
}



android {
    namespace = "com.example.iei_project"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.iei_project"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}


dependencies {
    // Use the Compose BOM to manage Jetpack Compose library versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Supabase - Use the BOM to manage Supabase library versions
    implementation(platform(libs.supabase.bom))

    // Now, declare the supabase libraries you need without versions
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)

    // ... other dependencies

    // Kotlinx Serialization - This should align with your Kotlin version
    implementation(libs.kotlinx.serialization.json)

    // CSV Library
    implementation(libs.opencsv)

    // Retrofit (ensure you need this alongside Supabase)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Debug implementations
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("io.ktor:ktor-client-android:3.3.2")



}

