buildscript {
    val kotlinVersion by extra("1.3.72")
    //val coroutinesVersion by extra("1.3.6")
    val navVersion by extra("2.3.0")
    val hiltVersion by extra ("2.28-alpha")

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}
