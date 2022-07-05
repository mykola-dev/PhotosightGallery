import java.io.*
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.github.breadmoirai.github-release") version "2.2.12"
}

val changelog = File(rootProject.projectDir, "changelog.txt").readText()
val (appVersion, recentChanges) = Regex("""^v(1\..+)[\n\r]+([\s\S]+?)[\n\r]+(?:[\n\r]v1\..+|${'$'})""")
    .find(changelog)!!
    .destructured
val appVersionCode = changelog.lines().size + 20

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "ds.photosight"
        minSdk = 21
        targetSdk = 32
        versionCode = appVersionCode
        versionName = appVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

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
        getByName("debug") {

        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-XXLanguage:+InlineClasses"
        )
        //useIR = true
    }

    compileOptions {
        //coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "ds.photosight"

}

// for hilt
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

androidExtensions {
    isExperimental = true
}

githubRelease {
    token { gradleLocalProperties(rootDir).getProperty("github.token") }
}

dependencies {
    val androidX = "1.8.0"
    val lifecycleVersion = "2.5.0"
    val pagingVersion = "3.1.1"
    val coroutinesVersion = "1.6.3"
    val appCompatVersion = "1.4.2"
    val fragmentVersion = "1.5.0"
    val viewPagerVersion = "1.1.0-beta01"
    val constraintLayoutVersion = "2.1.4"
    val recyclerViewVersion = "1.2.1"
    val hiltVersion: String by rootProject.extra
    val navVersion: String by rootProject.extra

    implementation(project(":parser"))

    // androidx
    implementation("androidx.core:core-ktx:$androidX")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.paging:paging-runtime:$pagingVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.viewpager2:viewpager2:$viewPagerVersion")
    implementation("androidx.recyclerview:recyclerview:$recyclerViewVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")              // https://developer.android.com/jetpack/androidx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")

    // ui
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")          // https://kotlinlang.org/docs/reference/coroutines/basics.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation(kotlin("reflect"))

    // di
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // prefs
    implementation("com.chibatching.kotpref:kotpref:2.11.0")                                    // https://github.com/chibatching/Kotpref

    // misc
    implementation("com.jakewharton.timber:timber:4.7.1")                                       // https://github.com/JakeWharton/timber
    implementation("com.github.hadilq.liveevent:liveevent:1.2.0")                               // https://github.com/hadilq/LiveEvent
    implementation("com.github.bumptech.glide:glide:4.11.0")                                    // https://github.com/bumptech/glide
    kapt("com.github.bumptech.glide:compiler:4.11.0")
    //implementation("com.github.florent37:runtime-permission-kotlin:1.1.2")                      // https://github.com/florent37/RuntimePermission

    // tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutinesVersion")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation(kotlin("test-junit"))
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
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

        owner("deviant-studio")
        repo("PhotosightGallery")
        tagName("v$appVersion")
        releaseName("v$appVersion")
        body(recentChanges)
        draft(false)
        prerelease(false)
        overwrite(true)
        dryRun(false)

        // postpone asset preparation
        doFirst {
            val filter = FilenameFilter { dir, filename -> appVersion in filename }
            val releaseFile = File(rootProject.rootDir, "bin").listFiles(filter)
            setReleaseAssets(releaseFile)

        }

    }

}