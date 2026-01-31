buildscript {
    val kotlinVersion by extra("2.3.0")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}
