import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.nevaxr.foundation.car.demo"
    compileSdk = 36

    useLibrary("android.car")

    defaultConfig {
        applicationId = "com.nevaxr.foundation.car.demo"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        val keystoreProperties = Properties().apply {
            load(rootProject.file("keystore.properties").inputStream())
        }

        create("keystore") {
            keyAlias = keystoreProperties.getProperty("keystore.alias")
            keyPassword = keystoreProperties.getProperty("keystore.password")
            storeFile = rootProject.file("platform.keystore")
            storePassword = keystoreProperties.getProperty("keystore.password")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".unsigned"
            addManifestPlaceholders(mapOf(
                "appLabel" to "NCarDemo.Unsigned",
            ))
        }

        release {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("keystore")
            applicationIdSuffix = ".signed"
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            addManifestPlaceholders(mapOf(
                "appLabel" to "NCarDemo.Signed",
            ))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.timber)
    implementation(project(":foundation-car"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
