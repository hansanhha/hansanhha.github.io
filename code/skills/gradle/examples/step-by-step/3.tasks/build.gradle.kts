import org.apache.tools.ant.filters.ReplaceTokens

group = "com.hansanhha"
description = "demo task project"

val os = System.getProperty("os.name");

tasks.register<Copy>("generateOSFileInWindows") {
    group = "OS"
    description = "print own os name when windows environment"
    enabled = true

    from(fileTree(layout.projectDirectory) {
        include("resources/your-os.txt")
    })
    into(layout.buildDirectory)

    filter<ReplaceTokens>("tokens" to mapOf("MESSAGE" to os))

    doFirst {
        println("generating file printed with OS")
    }

    doLast {
        println("done")
    }

    onlyIf {
        os.equals("Windows")
    }
}

tasks.register<Copy>("generateOSFileInMac") {
    group = "OS"
    description = "print own os name when mac environment"
    enabled = true

    from(fileTree(layout.projectDirectory) {
        include("resources/your-os.txt")
    })
    into(layout.buildDirectory)

    filter<ReplaceTokens>("tokens" to mapOf("MESSAGE" to os))

    doFirst {
        println("generating file printed with OS")
    }

    doLast {
        println("done")
    }

    onlyIf {
        os.equals("Mac OS X")
    }
}