plugins {
    id ("com.android.application")
}

android {
    namespace = "com.example.stockapi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stockapi"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // תלויות אפליקציה
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ספרייה חיצונית (stocklib)
    implementation(project(":stocklib"))
    implementation(libs.activity)


    // תלויות בדיקה
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    implementation ("com.github.TomCo2210:25A-10221-L02:1.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.android.tools.build") {
            useVersion("8.7.3")
        }
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if ((requested.group == "org.jetbrains.kotlin") && (requested.name.startsWith("kotlin-stdlib"))) {
                    useVersion("1.8.0")
                }
            }
        }
    }
}


