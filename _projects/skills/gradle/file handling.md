## File and Directory Configuring Ways

- a simple string representing a file

```kotlin
tasks.register<Copy>("apiDocs") {
    from("docs/api.adoc")
    into(layout.buildDirectory.dir("build/docs"))
}
```

- getting a file object using **Project.file**

```kotlin
tasks.register<Copy>("apiDocs") {
    from(project.file("docs/api.adoc"))
    into(layout.buildDirectory.dir("build/docs"))
}
```

- getting a directory object using the **layout** object such as `layout.buildDirectory.dir`, `layout.projectDirectory.dir`

```kotlin
tasks.register<Copy>("apiDocs") {
    from(layout.projectDirectory.dir("docs"))
    into(layout.buildDirectory.dir("docs"))
}
```

- using a **FileTree**, which preserves subdirectory structure when copying

```kotlin
tasks.register<Copy>("apiDocs") {
    from(fileTree(layout.projectDirectory) {
        include("docs/*.adoc")
    })
    into(layout.buildDirectory)
}

// or 

tasks.register<Copy>("apiDocs") {
    from(layout.projectDirectory)
    include("docs/*.adoc")
    into(layout.buildDirectory)
}
```

- using a "FileCollection", which doesn't preserve subdirectory

## Relative Path

When you use Gradle file APIs, Gradle handles making sure they work relative to the project your build script code is related to

That means if you're in the build script of multi-project build, any files you reference are relative to that subproject's directory

```kotlin
tasks.register<Copy>("apiDocsHtml") {
    from(layout.projectDirectory.file("/src/main/resources/static/api.html"))
    into(layout.buildDirectory)
}
```

When you run task, Gradle copies the file, using the subproject directory as root of the relative path

This means that you should always use the file APIs and never create a file object directly 

If you were to do that, the file would be created relative to the current working directory, which Gradle makes no guarantees about



