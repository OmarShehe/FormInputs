pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { setUrl ("https://jitpack.io") }
    }
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FormInputs"
include (":app")
include (":forminputkotlin")
include(":forminput-compose")
