import com.android.build.gradle.api.ApplicationVariant
import java.util.*
import java.io.*
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "ds.photosight"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 26
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        val propsFile = rootProject.file("keystore.properties")
        val props = Properties()
        props.load(FileInputStream(propsFile))
        create("release") {
            storeFile = file("../../" + props.getProperty("storeFile"))
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
            storePassword = props.getProperty("storePassword")
        }
    }

    buildTypes {
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
    }

    compileOptions {
        coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    applicationVariants.all(object : Action<ApplicationVariant> {
        override fun execute(variant: ApplicationVariant) {
            variant
                .outputs
                .map { it as BaseVariantOutputImpl }
                .forEach {
                    it.outputFileName = "photosight${defaultConfig.versionName}-${variant.buildType.name}.apk"
                }
        }
    })

}

dependencies {
    val androidX = "1.3.0"
    val lifecycleVersion = "2.2.0"
    val koinVersion = "2.1.6"
    val coroutinesVersion: String by rootProject.extra

    implementation("androidx.multidex:multidex:2.0.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.5")
    implementation(project(":parser"))

    // androidx
    implementation("androidx.core:core-ktx:$androidX")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.fragment:fragment-ktx:1.2.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")              // https://developer.android.com/jetpack/androidx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.preference:preference:1.1.1")

    // ui
    implementation("com.google.android.material:material:1.1.0")
    implementation("com.github.chrisbanes.photoview:library:+")

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")          // https://kotlinlang.org/docs/reference/coroutines/basics.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // di
    implementation("org.koin:koin-core:$koinVersion")                                           // https://github.com/InsertKoinIO/koin/
    // Koin Extended & experimental features
    implementation("org.koin:koin-core-ext:$koinVersion")
    // Koin for Android
    implementation("org.koin:koin-android:$koinVersion")
    // Koin Android Scope features
    implementation("org.koin:koin-android-scope:$koinVersion")
    // Koin Android ViewModel features
    implementation("org.koin:koin-android-viewmodel:$koinVersion")
    // Koin Android Experimental features
    implementation("org.koin:koin-android-ext:$koinVersion")
    // Koin AndroidX Scope features
    implementation("org.koin:koin-androidx-scope:$koinVersion")
    // Koin AndroidX ViewModel features
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")
    // Koin AndroidX Fragment features
    implementation("org.koin:koin-androidx-fragment:$koinVersion")
    // Koin AndroidX Experimental features
    implementation("org.koin:koin-androidx-ext:$koinVersion")
    // Koin for Unit tests
    testImplementation("org.koin:koin-test:$koinVersion")

    // prefs
    implementation("com.chibatching.kotpref:kotpref:2.11.0")                                    // https://github.com/chibatching/Kotpref

    // misc
    implementation("com.jakewharton.timber:timber:4.7.1")                                       // https://github.com/JakeWharton/timber
    implementation("com.github.hadilq.liveevent:liveevent:1.2.0")                               // https://github.com/hadilq/LiveEvent
    implementation("com.github.bumptech.glide:glide:4.11.0")                                    // https://github.com/bumptech/glide
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // legacy todo remove
    implementation("com.github.castorflex.smoothprogressbar:library-circular:+")
    implementation("com.googlecode.android-query:android-query:0.25.9")
    implementation("de.greenrobot:eventbus:2.4.0")
    implementation("org.jsoup:jsoup:1.13.1")

}

