plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.bbonllo.mealmonkey"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bbonllo.mealmonkey"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Play Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Color Picker
    implementation("com.github.skydoves:colorpickerview:2.3.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Room components
    implementation("androidx.room:room-runtime:2.2.5")
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    androidTestImplementation("androidx.room:room-testing:2.2.5")

    // OpenStreetMap components
    implementation("org.osmdroid:osmdroid-android:6.1.17")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Kotlin components
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    // JUnit test
    testImplementation(libs.junit)
    implementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}