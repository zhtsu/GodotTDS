plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val tapSdkVersion = "3.29.2"
val tapAdVersion = "3.16.3.31"

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
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.appcompat.v7)
    implementation(libs.support.annotations)
    implementation(libs.support.v4)
    implementation(libs.glide)
    implementation(libs.recyclerview.v7)
    compileOnly(files("libs/AntiAddiction_${tapSdkVersion}.aar"))
    compileOnly(files("libs/AntiAddictionUI_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapAD_${tapAdVersion}.aar"))
    compileOnly(files("libs/TapBootstrap_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapCommon_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapConnect_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapDB_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapLogin_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapMoment_${tapSdkVersion}.aar"))
    compileOnly(files("libs/TapAchievement_${tapSdkVersion}.aar"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}