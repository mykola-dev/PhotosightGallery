@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val changelog = File(rootProject.projectDir, "changelog.txt").readText()
val (appVersion, recentChanges) = Regex("""^v(\d+\..+)[\n\r]+([\s\S]+?)[\n\r]+(?:[\n\r]v\d+\..+|$)""")
    .find(changelog)!!
    .destructured
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

        resValue("string", "app_changelog", "\"$changelog\"")
    }

    signingConfigs {
        val props = gradleLocalProperties(rootDir, providers)
        create("release") {
            storeFile = file("../../${props.getProperty("storeFile")}")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
            storePassword = props.getProperty("storePassword")
        }
    }

    buildTypes {
        debug { }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(17)
        compilerOptions {
            freeCompilerArgs.addAll(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
                "-opt-in=coil.annotation.ExperimentalCoilApi"
            )

            // compose metrics
            if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
                val path = layout.buildDirectory.get().asFile.absolutePath + "/compose_metrics"
                freeCompilerArgs.addAll(
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$path"
                )
                freeCompilerArgs.addAll(
                    "-P", "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$path"
                )
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        lintConfig = file("lint.xml")
    }

    namespace = "ds.photosight.compose"
}

dependencies {
    implementation(project(":parser"))

    coreLibraryDesugaring(libs.desugar)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // AndroidX Lifecycle
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    // Navigation
    implementation(libs.bundles.navigation3)

    // Paging
    implementation(libs.bundles.paging)

    // Dependency Injection - Koin
    implementation(libs.bundles.koin)

    // Kotlinx
    implementation(libs.bundles.coroutines)
    implementation(libs.serialization.json)

    // Network
    implementation(libs.coil.compose)

    // Storage/Preferences
    implementation(libs.kotpref)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.bundles.compose.testing)
}
