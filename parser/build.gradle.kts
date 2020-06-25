import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
}

/*tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}*/

dependencies {
    val coroutinesVersion:String by rootProject.extra
    implementation(kotlin("stdlib"))
    implementation("org.jsoup:jsoup:1.13.1")

    //implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")

    // tests
    testImplementation("junit:junit:4.13")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation(kotlin("test-junit"))
}