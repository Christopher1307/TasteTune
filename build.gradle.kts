// build.gradle.kts en la raíz del proyecto (Project: TasteTune)
plugins {
    id("com.android.application") version "8.8.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
    id("org.jetbrains.compose") version "1.5.0" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.0" apply false
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}
