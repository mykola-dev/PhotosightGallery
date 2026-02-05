@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val changelog = File(rootProject.projectDir, "shared/src/commonMain/composeResources/files/changelog.txt").readText().replace("\r\n", "\n")
val matchResult = Regex("""^v(\d+\.[^\n]+)\n([\s\S]+?)(?:\nv\d+\.|$)""").find(changelog)
val appVersion = matchResult?.groupValues?.get(1) ?: "1.0.0"
val recentChanges = matchResult?.groupValues?.get(2)?.trim() ?: ""
val appVersionCode = changelog.lines().size + 20

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "ds.photosight.compose"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Inject changelog content into BuildConfig
        buildConfigField("String", "APP_CHANGELOG", "\"${recentChanges.replace("\"", "\\\"")}\"")
    }

    signingConfigs {
        create("release") {
            val localProps = gradleLocalProperties(rootDir, providers)
            keyAlias = localProps.getProperty("keyAlias") ?: System.getenv("KEY_ALIAS")
            keyPassword = localProps.getProperty("keyPassword") ?: System.getenv("KEY_PASSWORD")
            storeFile = file(localProps.getProperty("storeFile") ?: System.getenv("KEYSTORE_PATH") ?: "dummy.jks")
            storePassword = localProps.getProperty("storePassword") ?: System.getenv("KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        resValues = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    namespace = "ds.photosight.compose"

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            res.srcDirs("src/main/res")
            java.srcDirs("src/main/java")
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=coil.annotation.ExperimentalCoilApi"
        )
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared"))
            implementation(project(":parser"))

            // AndroidX Core
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)

            // AndroidX Lifecycle
            implementation(libs.bundles.lifecycle)

            // Compose
            implementation(libs.bundles.compose)

            // Navigation
            implementation(libs.navigation.compose)

            // Paging
            implementation(libs.bundles.paging)

            // Dependency Injection - Koin
            implementation(libs.bundles.koin)

            // Kotlinx
            implementation(libs.bundles.coroutines)
            implementation(libs.serialization.json)

            // Network - Coil for Android
            implementation(libs.coil.compose)

            // Logging
            implementation(libs.timber)
            implementation(libs.napier)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.compose.testing)
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.espresso.core)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
    debugImplementation(libs.bundles.compose.debug)
}
