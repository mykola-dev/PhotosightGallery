import com.android.build.gradle.api.ApplicationVariant
import java.util.*
import java.io.*
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "ds.photosight"
        minSdkVersion(21)
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
        //coreLibraryDesugaringEnabled = true
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

// for hilt
kapt {
    correctErrorTypes = true
}

androidExtensions {
    isExperimental = true
}

dependencies {
    val androidX = "1.3.0"
    val lifecycleVersion = "2.2.0"
    val koinVersion = "2.1.6"
    val archVersion = "2.1.0"
    val composeVersion = "0.1.0-dev13"
    val hiltJetpackVersion = "1.0.0-alpha01"
    val pagingVersion = "3.0.0-alpha02"
    val coroutinesVersion = "1.3.6"
    val hiltVersion: String by rootProject.extra
    val navVersion: String by rootProject.extra

    implementation("androidx.multidex:multidex:2.0.1")
    //coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.6")
    implementation(project(":parser"))

    // androidx
    implementation("androidx.core:core-ktx:$androidX")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.paging:paging-runtime:$pagingVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")              // https://developer.android.com/jetpack/androidx
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.preference:preference:1.1.1")

    // ui
    implementation("com.google.android.material:material:1.3.0-alpha01")
    implementation("com.github.chrisbanes.photoview:library:+")
    implementation("androidx.slidingpanelayout:slidingpanelayout:1.0.0")
    implementation("androidx.ui:ui-tooling:$composeVersion")
    implementation("androidx.ui:ui-layout:$composeVersion")
    implementation("androidx.ui:ui-material:$composeVersion")

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")          // https://kotlinlang.org/docs/reference/coroutines/basics.html
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation(kotlin("reflect"))

    // di
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$hiltJetpackVersion")
    kapt("androidx.hilt:hilt-compiler:$hiltJetpackVersion")


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

    // tests
    testImplementation("junit:junit:4.13")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutinesVersion")
    testImplementation("androidx.arch.core:core-testing:$archVersion")
    testImplementation(kotlin("test-junit"))
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
}

