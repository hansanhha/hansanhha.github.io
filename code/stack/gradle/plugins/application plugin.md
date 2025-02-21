## Application Plugin

It's core plugin that give you the follow useful features:
- Adds a `run` task to a project 
  - run task automatically executes the configured main class
- Automatically applies the `java` plugin
- Doesn't create a jar file

Note that, application plugin classpath it uses is generated from the resolvable `runtimeClasspath` dependency configuration 

runtimeClasspath is based on any implementation and runtimeOnly dependencies

## Running Application

To automatically run application, it needs to apply the application plugin by replacing java plugin with application

Then will see `run` task through `./gradlew tasks`

before run task, it needs to configure the main class to be executed

In [my playground's build script](../../examples/app/build.gradle.kts), I configured it as follows: 

```kotlin
application {
    mainClass.set("com.gradle.theme_park.status.RideStatusService")
}
```

Now, run application by passing program arguments

```
./gradlew app:run --args logflume
```

### JavaExec Task Class

`run` task is based on `JavaExec` task class which handles running a java process

so it can make custom run task such as that run jar file with gradle

my custom run task:

```kotlin
tasks.register<JavaExec>("runJar") {
    group = "application"
    description = "Run the jar file itself by gradle"

    classpath(tasks.named("jar").map { it.outputs })
    classpath(configurations.runtimeClasspath)
    args("    teacups    ")

    mainClass.set("com.gradle.theme_park.status.RideStatusService")
}
```

JavaExec task class provide relevant configuration
- `classpath()` : The classpath for the main class
- `args()` : The arguments passed to the main class to be executed
- `mainClass` : configure execution main class which runs the main method in runtime

The first classpath() is references to get the jar file created by `Jar` task

map() is returned by `TaskProvider`. This takes a lambda from which we can return anything we want from the underlying task - `it.outputs`

The outputs of jar task are the jar file. So the jar file get added to the classpath of the `runJar` task

And second is add runtimeClasspath to classpath because our application reference 3rd-party libraries

The jar file only contains our application code, so we need add other dependencies to classpath



