plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "theme-park-project"
include("theme-park-status", "theme-park-api")
