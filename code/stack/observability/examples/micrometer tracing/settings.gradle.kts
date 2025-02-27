rootProject.name="micrometer-tracing-example-app"

include(
    "cart",
    "order",
    "payment"
)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}