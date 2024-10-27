plugins {
    alias(mylibs.plugins.application)
    alias(mylibs.plugins.kotlin.android)
    alias(mylibs.plugins.ksp)
    alias(mylibs.plugins.hilt)
    alias(mylibs.plugins.google.services)
}

android {
    namespace = "com.example.gotrack"
    compileSdk = mylibs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.gotrack"
        minSdk = mylibs.versions.minSdk.get().toInt()
        targetSdk = mylibs.versions.targetSdk.get().toInt()
        versionCode = mylibs.versions.versionCode.get().toInt()
        versionName = mylibs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = mylibs.versions.jvmTarget.get()
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions{
        resources.excludes.add("META-INF/*")
    }
}

dependencies {

    //Core
    implementation(mylibs.androidx.core.ktx)
    implementation(mylibs.androidx.appcompat)

    //View
    implementation(mylibs.material)

    //Test
    testImplementation(mylibs.junit)
    androidTestImplementation(mylibs.androidx.junit)
    androidTestImplementation(mylibs.androidx.espresso.core)

    //Firebase
    implementation(mylibs.firebase.auth)
    implementation(mylibs.firebase.auth.ktx)

    //Hilt
    implementation(mylibs.hilt.android)
    ksp(mylibs.hilt.compiler)

    //WorkManager
    implementation(mylibs.hilt.work)
    ksp(mylibs.hilt.work.compiler)

    //Fragment
    implementation(mylibs.fragment.ktx)

    //Navigation
    implementation(mylibs.navigation.fragment)
    implementation(mylibs.navigation.ui)

    //Coroutine
    implementation(mylibs.kotlinx.coroutines)

    //Modules
    implementation(project(":features:auth"))
    implementation(project(":features:map"))
    implementation(project(":features:tracker"))
    implementation(project(":features:intermediate"))
    implementation(project(":features:settingsApplication"))
    implementation(project(":core:navigation"))
    implementation(project(":network"))
}