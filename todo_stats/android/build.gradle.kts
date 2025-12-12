// File: android/build.gradle.kts

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Ensure these versions are compatible        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    // FIX 1: Add .asFile to convert Directory to File
    project.buildDir = newBuildDir.dir(project.name).asFile
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    // FIX 2: Use rootProject.layout.buildDirectory instead of deprecated rootProject.buildDir
    delete(rootProject.layout.buildDirectory)
}