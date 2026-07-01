plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.atomchallenge.feature.countries.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 👇 depende de domain — necesita Country, CountryRepository, etc.
    implementation(project(":feature:countries:domain"))

    // 👇 depende de los core — usa Retrofit y Room
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // --- Retrofit (para el ApiService) ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)

    // --- Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Coroutines + Flow ---
    implementation(libs.coroutines.android)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}