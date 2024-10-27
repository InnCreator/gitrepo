// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false    // Добавляем плагин Hilt, чтобы он был доступен для всех модулей
    alias(libs.plugins.kotlin.ksp) apply false // Добавляем плагин KSP, чтобы он был доступен для всех модулей
}

buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}