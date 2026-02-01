plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Parsing - Ksoup (Multiplatform) - Fleeksoft version with JSoup-like API
                implementation(libs.ksoup)

                // Network - Ktor (Multiplatform)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // DateTime (Multiplatform)
                implementation(libs.datetime)

                // Coroutines (Multiplatform)
                implementation(libs.coroutines.core)

                // Serialization
                implementation(libs.serialization.json)
            }
        }

        val androidMain by getting {
            dependencies {
                // Ktor Android engine
                implementation(libs.ktor.client.android)
            }
        }

        val jvmMain by getting {
            dependencies {
                // Ktor JVM engine
                implementation(libs.ktor.client.java)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }
    }
}

android {
    namespace = "ds.photosight.parser"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}
