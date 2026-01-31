plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    // Parsing
    implementation(libs.jsoup)

    // Network
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.okhttp)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test-junit"))
}
