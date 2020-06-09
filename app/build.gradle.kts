import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import java.util.*
import java.io.*
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    //kotlin("kapt")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "ds.photosight"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 26
        versionName = "0.10"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    val coroutinesVersion = "1.3.6"

    implementation("com.jakewharton.timber:timber:4.7.1")                                       // https://github.com/JakeWharton/timber

    implementation("androidx.core:core:$androidX")
    implementation("com.google.android.material:material:1.1.0")
    implementation("org.jsoup:jsoup:1.8.2")
    implementation("com.github.castorflex.smoothprogressbar:library-circular:+")
    implementation("com.googlecode.android-query:android-query:0.25.9")
    implementation("de.greenrobot:eventbus:2.4.0")
    implementation("com.github.chrisbanes.photoview:library:+")

// kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")          // https://kotlinlang.org/docs/reference/coroutines/basics.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

}

