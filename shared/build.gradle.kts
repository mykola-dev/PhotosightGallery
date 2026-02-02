plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.animation)  // Required for shared element transitions
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Multiplatform libraries
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.serialization.json)
                implementation(libs.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.napier)
                implementation(libs.ksoup)
                implementation(libs.paging.common)
                implementation(libs.paging.compose.common)
                implementation(libs.navigation.compose)
                implementation(libs.navigation3.runtime)
                implementation(libs.navigation3.ui)
                implementation(libs.multiplatform.settings)
                implementation(libs.datetime)

                // Images - Coil 3 (Multiplatform)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.network.ktor)

                // Paging - Cash App (Multiplatform)
                implementation(libs.paging.common)
                implementation(libs.paging.compose.common)

                // Our parser module
                implementation(project(":parser"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.lifecycle.runtime)
                implementation(libs.multiplatform.settings.noarg)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.java)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "ds.photosight.shared"
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
