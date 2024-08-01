plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val tapSDKVersion = "3.92.2"
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
    compileOnly(files("libs/AntiAddiction_${tapSDKVersion}.arr"))
    compileOnly(files("libs/AntiAddictionUI_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapAD_${tapADVersion}.arr"))
    compileOnly(files("libs/TapBootstrap_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapCommon_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapConnect_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapDB_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapLogin_${tapSDKVersion}.arr"))
    compileOnly(files("libs/TapMoment_${tapSDKVersion}.arr"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}