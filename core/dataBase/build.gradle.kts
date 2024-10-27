plugins {
    alias(mylibs.plugins.library)
    alias(mylibs.plugins.kotlin.android)
    alias(mylibs.plugins.ksp)
    alias(mylibs.plugins.hilt)
    alias(mylibs.plugins.google.services)
}

android {
    namespace = "com.example.database"
    compileSdk = mylibs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = mylibs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    //Core
    implementation(mylibs.androidx.core.ktx)
    implementation(mylibs.androidx.appcompat)

    //View
    implementation(mylibs.material)

    //Firebase
    implementation(mylibs.firebase.auth)

    //Test
    testImplementation(mylibs.junit)
    androidTestImplementation(mylibs.androidx.junit)
    androidTestImplementation(mylibs.androidx.espresso.core)

    //Room
    ksp(mylibs.androidx.room)
    implementation(mylibs.room.ktx)

    //Hilt
    implementation(mylibs.hilt.android)
    ksp(mylibs.hilt.compiler)

    //Module
    implementation(project(":core:data"))
}