rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://packages.jetbrains.team/maven/p/skiko/maven")
    }
}

rootProject.name = "Photosight"

include(":parser")
include(":shared")
include(":app")
include(":desktop")
