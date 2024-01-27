plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    namespace = "com.omarshehe.forminputs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.omarshehe.forminputs"
        minSdk = 17
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":forminputkotlin"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
}
