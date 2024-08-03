plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val tapSDKVersion = "3.29.2"
val tapADVersion = "3.16.3.31"

android {
    namespace = "cc.zhtsu.godot_tds_plugin"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.godot)
    implementation(libs.lc.storage.android)
    implementation(libs.lc.realtime.android)
    implementation(libs.okhttp)
    compileOnly(files("libs/AntiAddiction_${tapSDKVersion}.aar"))
    compileOnly(files("libs/AntiAddictionUI_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapAD_${tapADVersion}.aar"))
    compileOnly(files("libs/TapBootstrap_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapCommon_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapConnect_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapDB_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapLogin_${tapSDKVersion}.aar"))
    compileOnly(files("libs/TapMoment_${tapSDKVersion}.aar"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}