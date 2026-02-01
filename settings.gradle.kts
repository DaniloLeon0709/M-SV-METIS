pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VitaAlert"

include(":app")
include(":designsystem")
include(":domain")
include(":data")
include(":auth")
include(":ble")
include(":core")
include(":reports")
