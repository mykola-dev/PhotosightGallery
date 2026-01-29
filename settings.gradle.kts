rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":parser")
include(":compose")

