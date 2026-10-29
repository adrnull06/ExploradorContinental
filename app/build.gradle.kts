import java.util.Properties

//Cargar secretos desde local.properties
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun prop(name: String) = "\"${localProps.getProperty(name, "")}\""

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.castillo.exploradorcontinental"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.castillo.exploradorcontinental"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        //API KEYS
        buildConfigField("String", "RC_TOKEN", prop("RC_TOKEN"))
        buildConfigField("String", "GEONAMES_USER", prop("GEONAMES_USER"))
        buildConfigField("String", "WEATHER_KEY", prop("WEATHER_KEY"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    //Dependencias principales del proyecto
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}