import java.io.*
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

val changelog = File(rootProject.projectDir, "changelog.txt").readText()
val (appVersion, recentChanges) = Regex("""^v(\d\..+)[\n\r]+([\s\S]+?)[\n\r]+(?:[\n\r]v\d\..+|${'$'})""")
    .find(changelog)!!
    .destructured
val appVersionCode = changelog.lines().size + 20

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "ds.photosight.compose"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        //resValue("string", "app_changelog", "\"$changelog\"")
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
        freeCompilerArgs = listOf(
            //"-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-XXLanguage:+InlineClasses"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
    val navVersion: String by rootProject.extra
    val compose_version:String by rootProject.extra
    val accompanist = "0.24.13-rc"

    implementation(project(":parser"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // compose
    implementation("androidx.compose.ui:ui:$compose_version")
    //implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")
    implementation("androidx.navigation:navigation-compose:2.5.0")

    //implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanist")

    // androidx
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")              // https://developer.android.com/jetpack/androidx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")

    // di
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // widgets
    implementation("com.github.nesyou01:LazyStaggeredGrid:1.2.0")

    // kotlin
    //implementation(kotlin("stdlib-jdk8"))
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
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_version"]}")
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