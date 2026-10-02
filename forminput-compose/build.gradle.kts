plugins {
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "com.omarshehe.forminput.compose"
        compileSdk = libs.versions.sdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        // Off by default in the AGP-KMP library plugin; without it the Compose resources (strings) never reach the AAR.
        androidResources { enable = true }
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            implementation(libs.compose.material.icons.extended)
            // StringResource is part of the public API (labelRes and friends), so consumers need it on their classpath.
            api(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.kotlinx.datetime)
            // Only used by the image upload field and the image viewer.
            implementation(libs.coil.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.compose.ui.test)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.omarshehe.forminput.compose.resources"
}

mavenPublishing {
    coordinates(libs.versions.groupId.get(), libs.versions.artifactIdCompose.get(), libs.versions.composeVersion.get())
    pom {
        name.set("FormInputs Compose")
        description.set("Compose Multiplatform form inputs: validated text field, dropdown and button (Android, desktop, iOS).")
        url.set("https://github.com/OmarShehe/FormInputs")
        licenses { license { name.set("MIT"); url.set("https://opensource.org/licenses/MIT") } }
        developers { developer { id.set("OmarShehe") } }
        scm { url.set("https://github.com/OmarShehe/FormInputs"); connection.set("scm:git:https://github.com/OmarShehe/FormInputs.git") }
    }
    publishToMavenCentral()
    if (providers.gradleProperty("signingInMemoryKey").isPresent) signAllPublications()
}
