plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.novacartografia.kanbanprojectmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.novacartografia.kanbanprojectmanagement"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    packaging {
        resources {
            // Exclusiones existentes
            excludes.addAll(listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/io.netty.versions.properties",
                "META-INF/INDEX.LIST",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/*.kotlin_module",
                "META-INF/DEPENDENCIES"
            ))

            // Modificar la regla genérica para ser más inclusiva
            excludes.addAll(listOf(
                "kotlin/**/*.kotlin_builtins",
                "kotlin/*.kotlin_builtins"  // Añadir esta línea para capturar archivos en la raíz
            ))

            // Añadir explícitamente el nuevo archivo problemático
            excludes.add("kotlin/kotlin.kotlin_builtins")

            // pickFirsts para los archivos que necesitas
            pickFirsts.addAll(listOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "kotlin/kotlin.kotlin_builtins"  // Añadir esta línea
            ))
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.livedata)

    implementation ("androidx.compose.ui:ui:1.8.2")
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation (libs.androidx.runtime.livedata.v154)
    // ViewModel

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ViewModel y LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Retrofit para comunicación API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines para operaciones asíncronas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Mapas (para la funcionalidad de ubicación)
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    // Mapbox SDK
    implementation ("com.mapbox.maps:android:11.12.1")
    implementation ("com.mapbox.extension:maps-compose:11.12.1")

    // Glide para cargar imágenes
    implementation(libs.glide)

    //Exclusiones
    configurations.all {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }

    implementation(libs.appcrawler.platform) {
        exclude(group = "org.jetbrains.kotlin")  // Excluir todo el grupo org.jetbrains.kotlin
    }



}