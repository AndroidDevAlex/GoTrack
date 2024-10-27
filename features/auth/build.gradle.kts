plugins {
    alias(mylibs.plugins.library)
    alias(mylibs.plugins.kotlin.android)
    alias(mylibs.plugins.ksp)
    alias(mylibs.plugins.hilt)
    alias(mylibs.plugins.google.services)
}

android {
    namespace = "com.example.auth"
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
    buildFeatures {
        viewBinding = true
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

    //ViewModel
    implementation(mylibs.viewmodel.ktx)
    implementation(mylibs.livedata.ktx)

    //Navigation
    implementation(mylibs.navigation.fragment)
    implementation(mylibs.navigation.ui)

    //Hilt
    implementation(mylibs.hilt.android)
    ksp(mylibs.hilt.compiler)

    //Room
    implementation(mylibs.room.runtime)

    //Fragment
    implementation(mylibs.fragment.ktx)

    //Modules
    implementation(project(":core:dataBase"))
    implementation(project(":core:data"))
    implementation(project(":core:networking"))
    implementation(project(":core:navigation"))
    implementation(project(":network"))
    implementation(project(":features:settingsApplication"))
    implementation(project(":utils"))
}