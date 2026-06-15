rootProject.name = "LUMODisplay"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":controller:app:androidApp")
include(":controller:app:desktopApp")
include(":controller:shared")
include(":screen:app:androidApp")
include(":screen:app:desktopApp")
include(":screen:shared")
include(":shared")
include(":data")
include(":controller:compose")
include(":screen:compose")
include(":compose")
include(":controller:app:composeApp")
include(":screen:app:composeApp")
