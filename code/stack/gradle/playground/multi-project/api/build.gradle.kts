plugins {
    id("common-conventions")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
}

dependencies {
    implementation(project(":message"))

    implementation(libs.springboot.web)
    implementation(libs.guava)
}