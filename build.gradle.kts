buildscript {
    val kotlinVersion by extra("2.3.0")
    val navVersion by extra("2.8.3")


    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
