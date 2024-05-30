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

rootProject.name = "DynamicViewApp"
include(":appxml")
include(":appcompose")
include(":design:provider:api")
include(":base:initializator")
include(":design:resources")
include(":preferences:api")
include(":preferences:impl")
include(":design:provider:impl")
include(":design:ui:xml")
include(":persistence:api")
include(":persistence:impl_room_db")
