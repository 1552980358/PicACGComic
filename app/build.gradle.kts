@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.text.SimpleDateFormat

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}

android {
    namespace = "projekt.cloud.piece.pic"
    compileSdk = 33

    defaultConfig {
        applicationId = namespace
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    
    applicationVariants.all {
        outputs.all {
            if (this is BaseVariantOutputImpl) {
                outputFileName = defaultConfig.applicationId + "-" + defaultConfig.versionName + "-" + defaultConfig.versionCode + "-" +
                    SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis()) + "-" + buildType.name + ".apk"
            }
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    // https://kotlinlang.org/docs/gradle-configure-project.html#gradle-java-toolchains-support
    kotlin {
        jvmToolchain(11)
    }
    
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    
    ndkVersion = "25.1.8937393"
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.window:window:1.0.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.0.1")
    implementation("com.google.android.material:material:1.9.0-alpha01")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")
    kapt("androidx.databinding:databinding-compiler-common:7.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}