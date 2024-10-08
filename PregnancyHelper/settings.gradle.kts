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
        maven { url = uri("https://maven.pkg.jetbrains.space/data2viz/p/maven/dev") }
        maven { url = uri("https://maven.pkg.jetbrains.space/data2viz/p/maven/public") }
    }
}

rootProject.name = "PregnancyHelper"
include(":app")
 