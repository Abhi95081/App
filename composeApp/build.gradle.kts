import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Compose additions
            implementation("androidx.compose.material:material-icons-extended:1.6.0")
            implementation("androidx.navigation:navigation-compose:2.7.7")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

            // Accompanist (for system UI controller)
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

            // DataStore for preferences
            implementation("androidx.datastore:datastore-preferences:1.0.0")

            // Room Database (optional, for local storage)
            implementation("androidx.room:room-runtime:2.6.1")
            implementation("androidx.room:room-ktx:2.6.1")

            // WorkManager (for scheduling)
            implementation("androidx.work:work-runtime-ktx:2.9.0")

            // Charts library (for statistics)
            implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

