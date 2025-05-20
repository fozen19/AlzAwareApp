plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.alzawaremobile"
    compileSdk = 35
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.alzawaremobile"
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
            buildConfigField("String", "MAPS_API_KEY", "\"${System.getenv("MAPS_API_KEY")}\"")
        }
        debug {
            buildConfigField("String", "MAPS_API_KEY", "\"${System.getenv("MAPS_API_KEY")}\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // Or the latest version
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Added Later

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.maps)
    implementation(libs.location)

    //
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    // Retrofit for network calls
    implementation(libs.retrofit)


// (Optional) OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("androidx.glance:glance:1.1.1") // Or the latest version
    implementation("androidx.glance:glance-appwidget:1.1.1") //
    implementation("androidx.compose.ui:ui:1.6.7") // Or the latest version
    implementation("androidx.compose.material:material:1.6.7") // Or the latest version
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7") // Or the latest version
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7") // Or the latest version
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.7") // Or the latest version
    implementation("androidx.activity:activity-compose:1.9.0") // Or the latest version
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.play.services.maps.v1820)

    implementation(libs.material.v1110) // TabLayout için
    implementation(libs.androidx.viewpager2)       // ViewPager2 için

}