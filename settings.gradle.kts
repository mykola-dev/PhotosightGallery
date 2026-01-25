rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":parser")
// Temporarily disabled app module for Kotlin 2.3.0 migration
//include(":app")
include(":compose")

