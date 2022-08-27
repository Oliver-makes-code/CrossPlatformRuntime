import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "dev.proxyfox.library"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.graalvm.js:js:22.2.0")
    implementation(kotlin("reflect"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}