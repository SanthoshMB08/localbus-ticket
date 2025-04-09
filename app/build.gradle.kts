plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bmtc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bmtc"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core)
    implementation(libs.zxing) // For QR Scanner
    implementation(libs.zxing.android.embedded)
    implementation(libs.volley) // For API Requests
    implementation(libs.navigation.fragment)
    implementation (libs.navigation.ui)
    implementation(libs.support.annotations)
    implementation(libs.razorpay)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2con)
    implementation(libs.okhttp3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}