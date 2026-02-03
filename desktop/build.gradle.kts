import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(project(":parser"))

                implementation(compose.desktop.currentOs)
                implementation(compose.components.resources)
                implementation(libs.compose.multiplatform.ui)
                implementation(libs.compose.multiplatform.foundation)
                implementation(libs.compose.multiplatform.material3)
                implementation(libs.compose.multiplatform.runtime)
                implementation(libs.compose.multiplatform.animation)

                implementation(libs.ktor.client.java)
                implementation(libs.coroutines.core)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
                implementation(libs.serialization.json)
                implementation(libs.napier)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "ds.photosight.desktop.DesktopAppKt"

        nativeDistributions {
            packageName = "PhotosightGallery"
            packageVersion = "1.0.0"
            
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/desktopMain/resources"))
            
            windows {
                iconFile.set(project.file("src/desktopMain/resources/photosight.ico"))
            }
        }
    }
}

tasks.register<Copy>("createPortableApp") {
    dependsOn("packageDistributionForCurrentOS")

    val sourceDir = layout.buildDirectory.dir("compose/binaries/main/app/PhotosightGallery")
    val destDir = layout.buildDirectory.dir("PhotosightGallery-portable")

    from(sourceDir)
    into(destDir)

    doLast {
        println("\n=== PORTABLE APP READY ===")
        println("Location: ${destDir.get().asFile.absolutePath}")
        println("Copy the PhotosightGallery folder to any location")
        println("Run PhotosightGallery.exe - no installation required!")
        println("Note: For best results, use ./gradlew :desktop:run for development\n")
    }
}
