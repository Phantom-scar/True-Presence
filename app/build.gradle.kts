plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services) // ✅ Keep only if using Firebase
}

android {
    namespace = "com.project.truepresence"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.truepresence"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // ✅ CameraX for Facial Recognition
    implementation(libs.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    // ✅ AndroidX Core Components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview) // ✅ Added RecyclerView

    // ✅ Firebase (Authentication & Firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // ✅ ML Kit Face Detection (For Facial Attendance)
    implementation(libs.face.detection)

    // ✅ Google Play Services (For Location)
    implementation(libs.play.services.location)

    // ✅ OpenStreetMap (OSMDroid) Instead of Google Maps
    implementation(libs.osmdroid.android)  // ✅ Corrected OSMDroid dependency

    // ✅ Guava (For ListenableFuture in CameraX)
    implementation(libs.guava)

    // ✅ Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
