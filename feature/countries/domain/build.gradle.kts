plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // --- Solo para @Inject en los UseCases ---
    implementation(libs.javax.inject)

    // --- Coroutines (suspend functions) ---
    implementation(libs.kotlinx.coroutines.core)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}