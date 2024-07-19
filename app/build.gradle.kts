import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}


val versionPropsFile = file("../version.properties")
val versionProps = Properties()

if (versionPropsFile.canRead()) {
    versionProps.load(FileInputStream(versionPropsFile))
} else {
    throw GradleException("Could not read version.properties!")
}

val versionMajor = versionProps["VERSION_MAJOR"].toString().toInt()
val versionMinor = versionProps["VERSION_MINOR"].toString().toInt()
val versionPatch = versionProps["VERSION_PATCH"].toString().toInt()
val versionBuild = versionProps["VERSION_BUILD"].toString().toInt()

android {
    namespace = "com.t27.continental"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.t27.continental"
        minSdk = 24
        targetSdk = 34
        versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
        versionName = "$versionMajor.$versionMinor.$versionPatch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        create("nightly") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "@nightly"
        }
        getByName("release") {
            isMinifyEnabled = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.register("incrementVersionCodeForRelease") {
    doLast {
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps["VERSION_BUILD"] =
            (versionProps["VERSION_BUILD"].toString().toInt() + 1).toString()
        versionProps.store(versionPropsFile.writer(), null)
    }
}

tasks.register("incrementVersionCodeForNightly") {
    doLast {
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps["VERSION_BUILD_NIGHTLY"] =
            (versionProps["VERSION_BUILD_NIGHTLY"].toString().toInt() + 1).toString()
        versionProps.store(versionPropsFile.writer(), null)
    }
}

tasks.register("incrementMajor") {
    doLast {
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps["VERSION_MAJOR"] =
            (versionProps["VERSION_MAJOR"].toString().toInt() + 1).toString()
        versionProps["VERSION_MINOR"] = "0"
        versionProps["VERSION_PATCH"] = "0"
        versionProps["VERSION_BUILD"] = "0"
        versionProps.store(versionPropsFile.writer(), null)
    }
}

tasks.register("incrementMinor") {
    doLast {
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps["VERSION_MINOR"] =
            (versionProps["VERSION_MINOR"].toString().toInt() + 1).toString()
        versionProps["VERSION_PATCH"] = "0"
        versionProps["VERSION_BUILD"] = "0"
        versionProps.store(versionPropsFile.writer(), null)
    }
}

tasks.register("incrementPatch") {
    doLast {
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps["VERSION_PATCH"] =
            (versionProps["VERSION_PATCH"].toString().toInt() + 1).toString()
        versionProps["VERSION_BUILD"] = "0"
        versionProps.store(versionPropsFile.writer(), null)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

}