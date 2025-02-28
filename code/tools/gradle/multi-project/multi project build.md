## Multi Project Build

Gradle supports multi project builds helps modularise your project for maintainability and performance

If each subproject depends on another, they need to be declared in its build script, otherwise they cannot be used as dependency

And if you change the code in the single project, and it will be recompiled and retested as a whole project, but if you change the code in the subproject, only the subproject will be recompiled and retested

### Multi Project Build Structure

The Multi project build is structured that placed gradle's files such as `settings.gradle.kts` in the top-level directory and each subproject in the subdirectory

Each subproject has its own directory and `build.gradle.kts` file

```text

root directory (rootproject)
- gradle
    - wrapper
    - libs.versions.toml
- settings.gradle.kts
- gradle.properties
- gradlew

- sub directory (subproject)
    - directory
    - build.gradle.kts
``` 

**settings.gradle.kts**

configure subprojects to take part in the build with "include" statement in root project's settings.gradle.kts file

```text
include("subproject1", "subproject2", "subproject3")
```


