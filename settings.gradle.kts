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
    versionCatalogs {
        create("mylibs") {
            from(files("gradle/mylibs.versions.toml"))
        }
    }
}

rootProject.name = "GoTrack"
include(":app")
include(":features:auth")
include(":features:map")
include(":features:tracker")
include(":features:intermediate")
include(":core:data")
include(":core:dataBase")
include(":core:networking")
include(":core:navigation")
include(":network")
include(":utils")
include(":features:settingsApplication")
