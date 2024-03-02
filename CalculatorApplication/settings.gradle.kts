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

<<<<<<<< HEAD:CalculatorApplication/settings.gradle.kts
rootProject.name = "Calculator Application"
========
rootProject.name = "Greeting App"
>>>>>>>> 75864eede69b56cfe6bec5af2c4b33871f68bc51:GreetingApp/settings.gradle.kts
include(":app")
 