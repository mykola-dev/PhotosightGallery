@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FilenameFilter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    //id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

val changelog = File(rootProject.projectDir, "changelog.txt").readText()
val (appVersion, recentChanges) = Regex("""^v(\d\..+)[\n\r]+([\s\S]+?)[\n\r]+(?:[\n\r]v\d\..+|${'$'})""")
    .find(changelog)!!
    .destructured
val appVersionCode = changelog.lines().size + 20

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "ds.photosight.compose"
        minSdk = 26
        targetSdk = 33
        versionCode = 3
        versionName = "2.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resValue("string", "app_changelog", "\"$changelog\"")
    }

    signingConfigs {
        val props = gradleLocalProperties(rootDir)
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            //"-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
            "-opt-in=coil.annotation.ExperimentalCoilApi",
            "-XXLanguage:+InlineClasses"
        )

        // compose metrics
        if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
            val path = project.buildDir.absolutePath + "/compose_metrics"
            freeCompilerArgs += listOf(
                "-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$path"
            )
            freeCompilerArgs += listOf(
                "-P", "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$path"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "ds.photosight.compose"

    // ksp bug
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

// for hilt
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

githubRelease {
    token { gradleLocalProperties(rootDir).getProperty("github.token") }
}

dependencies {
    val hiltVersion: String by rootProject.extra
    //val navVersion: String by rootProject.extra
    val composeVersion = "1.3.0"
    val accompanist = "0.25.1"
    val composeDestinations = "1.7.23-beta"

    implementation(project(":parser"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")

    // compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    //implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.paging:paging-compose:1.0.0-alpha17")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")


    // accompanist https://github.com/google/accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanist")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanist")
    implementation("com.google.accompanist:accompanist-pager:$accompanist")

    // navigation https://github.com/raamcosta/compose-destinations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$composeDestinations")
    ksp("io.github.raamcosta.compose-destinations:ksp:$composeDestinations")

    // androidx
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")              // https://developer.android.com/jetpack/androidx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    // di
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // widgets
    implementation("com.github.nesyou01:LazyStaggeredGrid:1.2.0")

    // kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")          // https://kotlinlang.org/docs/reference/coroutines/basics.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")

    // network
    implementation("io.coil-kt:coil-compose:2.1.0")

    // prefs
    implementation("com.chibatching.kotpref:kotpref:2.11.0")                                    // https://github.com/chibatching/Kotpref

    // misc
    implementation("com.jakewharton.timber:timber:4.7.1")                                       // https://github.com/JakeWharton/timber

    // tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

tasks {
    val copyRelease by registering(Copy::class) {
        dependsOn(getByName("assembleRelease"))
        from("${project.buildDir}/outputs/apk/release") {
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

        // postpone asset preparation
        doFirst {
            val filter = FilenameFilter { dir, filename -> appVersion in filename }
            val releaseFile = File(rootProject.rootDir, "bin").listFiles(filter)
            setReleaseAssets(releaseFile)

        }

    }

}