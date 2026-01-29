@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
    id("com.github.breadmoirai.github-release") version "2.5.2"
}

val changelog = File(rootProject.projectDir, "changelog.txt").readText()
val (appVersion, recentChanges) = Regex("""^v(\d\..+)[\n\r]+([\s\S]+?)[\n\r]+(?:[\n\r]v\d\..+|${'$'})""")
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
        debug {

        }
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
    namespace = "ds.photosight.compose"
}

// Koin configuration - no special config needed, pure Kotlin DSL

githubRelease {
    token { gradleLocalProperties(rootDir, providers).getProperty("github.token") }
}

dependencies {
    val koinVersion = "4.1.1"
    val composeVersion = "1.10.2"
    val materialIconsVersion = "1.7.8"
    val coilVersion = "2.7.0"
    val navigation3Version = "1.1.0-alpha03"

    implementation(project(":parser"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("androidx.paging:paging-runtime-ktx:3.4.0")
    implementation("androidx.paging:paging-compose:3.4.0")
    implementation("androidx.navigation3:navigation3-runtime:$navigation3Version")
    implementation("androidx.navigation3:navigation3-ui:$navigation3Version")
    implementation("androidx.compose.material:material-icons-extended:$materialIconsVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // androidx
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    // di - Koin (no KSP/KAPT needed)
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("io.insert-koin:koin-compose:$koinVersion")

    // widgets - using built-in Compose LazyStaggeredGrid (Compose 1.4+)

    // kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // network
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // prefs
    implementation("com.chibatching.kotpref:kotpref:2.13.2")

    // misc
    implementation("com.jakewharton.timber:timber:5.0.1")

    // tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

tasks {
    val copyRelease by registering(Copy::class) {
        dependsOn(getByName("assembleRelease"))
        from("${project.layout.buildDirectory.get()}/outputs/apk/release") {
            include("*.apk")
            rename { "photosight-v${appVersion}-release.apk" }
        }
        into(File(rootProject.rootDir, "bin"))
    }

    githubRelease.configure {
        dependsOn(copyRelease)

        owner.set("deviant-studio")
        repo.set("PhotosightGallery")
        tagName.set("v$appVersion")
        releaseName.set("v$appVersion")
        body.set(recentChanges)
        draft.set(false)
        prerelease.set(false)
        overwrite.set(true)
        dryRun.set(false)

        val apkFile = File(rootProject.rootDir, "bin/photosight-v${appVersion}-release.apk")
        if (apkFile.exists()) {
            releaseAssets.setFrom(apkFile)
        }
    }
}
