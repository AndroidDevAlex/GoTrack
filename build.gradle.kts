buildscript {
    dependencies {
        classpath(mylibs.google.services)
        classpath(mylibs.android.gradle.plugin)
    }
}

plugins {
    alias(mylibs.plugins.application) apply false
    alias(mylibs.plugins.kotlin.android) apply false
    alias(mylibs.plugins.library) apply false
    alias(mylibs.plugins.google.services) apply false
    alias(mylibs.plugins.hilt) apply false
    alias(mylibs.plugins.ksp) apply false
}