plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val pluginName = "GodotTdsPlugin"
val pluginPackageName = "cc.zhtsu.godot_tds_plugin"

val tapAdVersion = "3.16.3.45"

android {
    namespace = pluginPackageName
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

    compileOnly(files("libs/TapAD_${tapAdVersion}.aar"))

    implementation(libs.tap.core)
    implementation(libs.tap.compliance)
    implementation(libs.tap.login)
    implementation(libs.tap.moment)
    implementation(libs.tap.achievement)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}