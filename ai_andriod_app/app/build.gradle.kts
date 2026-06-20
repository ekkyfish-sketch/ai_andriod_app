import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.secrets)
}

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    } else {
        val envExampleFile = rootProject.file(".env.example")
        if (envExampleFile.exists()) {
            envExampleFile.inputStream().use { load(it) }
        }
    }
}

fun getSecret(key: String, default: String = ""): String {
    val value = System.getenv(key) ?: envProperties.getProperty(key) ?: default
    return value.replace("\"", "")
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.ekkyfish.pkzpx"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val rawToken = getSecret("WHATSAPP_API_TOKEN")
        val apiToken = if (rawToken.isBlank() || rawToken == "YOUR_WHATSAPP_API_TOKEN") {
            val fallback = getSecret("WHATSAPP_ACCESS_TOKEN")
            if (fallback.isNotBlank() && fallback != "YOUR_WHATSAPP_API_TOKEN") {
                fallback
            } else {
                val fbToken = getSecret("ACCESS_TOKEN")
                if (fbToken.isNotBlank()) fbToken else rawToken
            }
        } else {
            rawToken
        }

        val rawPhoneId = getSecret("WHATSAPP_PHONE_NUMBER_ID")
        val phoneId = if (rawPhoneId.isBlank() || rawPhoneId == "YOUR_WHATSAPP_PHONE_NUMBER_ID") {
            val fallback = getSecret("phone_number_id")
            if (fallback.isNotBlank()) fallback else getSecret("PHONE_NUMBER_ID", rawPhoneId)
        } else {
            rawPhoneId
        }

        val wabaId = getSecret("WHATSAPP_WABA_ID", "YOUR_WHATSAPP_WABA_ID")
        val datasetId = getSecret("WHATSAPP_DATASET_ID", "YOUR_WHATSAPP_DATASET_ID")

        buildConfigField("String", "GEMINI_API_KEY", "\"${getSecret("GEMINI_API_KEY")}\"")
        buildConfigField("String", "WHATSAPP_API_TOKEN", "\"$apiToken\"")
        buildConfigField("String", "WHATSAPP_PHONE_NUMBER_ID", "\"$phoneId\"")
        buildConfigField("String", "WHATSAPP_WABA_ID", "\"$wabaId\"")
        buildConfigField("String", "WHATSAPP_DATASET_ID", "\"$datasetId\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Coil (Image loading)
    implementation(libs.coil.compose)

    // Retrofit & Moshi (Networking/JSON parser)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Preferences Datastore
    implementation(libs.androidx.datastore.preferences)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Unit testing
    testImplementation(libs.junit)
}
