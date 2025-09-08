rootProject.name="convention-plugin-using-project"

pluginManagement {
    includeBuild("../java-convention-plugin")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("app-1")
include("app-2")