// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    //val hiltVersion by extra("2.28-alpha")
    val kotlinVersion by extra("1.9.0")
    val hiltVersion by extra("2.44")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

}