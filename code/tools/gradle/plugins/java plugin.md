[Java plugin](#java-plugin)

[Java plugin task graph](#java-plugin-task-graph)

[Compile classes - compileJava, compileTestJava](#compile-classes---compilejava-compiletestjava)
- [JavaCompile Task Class](#javacompile-task-class)

[Manage Resources - processResources, processTestResources](#manage-resources---processresources-processTestResources)
- [Copy task Class](#copy-task-class)

[Define Dependencies](#define-dependencies)

[Package - jar](#package---jar)
- [Jar Task Class](#jar-task-class)

[Run Tests - test](#run-tests---test)

[Source Set](#source-set)

## Java plugin

It's gradle core plugin that give you the follow useful features:
- compiling code
- processing resources
- producing a jar file
- running tests

The java plugin initialises project as a java project 

It also adds several dependency configuration, which we can declare dependencies against to control the generation of the classpath

## Java plugin task graph

build
- assemble
    - **jar**
        - classes
            - **compileJava**
            - **processResources**
- check
    - **test**
        - classes
            - **compileJava**
            - **processResources**
        - testClasses
            - compileTestJava
                - testClasses
                    - **compileJava**
                    - **processResources**
            - **processTestResources**

Tasks that bolded are task which actually perform an action when they run

The rest of tasks are operate as aggregate tasks

important tasks:

**Assemble task**
- aggregate task that compileJava, processResources, jar task are depends on this task
- thus then you run this task, it will run all of those tasks sequentially and build a jar file
- assemble and jar task almost the same in that assembling a project without running testing it

**Test task**
- it's ensures to run tasks which classes, compileJava, processResources tasks
- besides it also run testClasses, compileTestJava, processTestResources tasks
- that's ensures that both the main code and test code are built, before running the tests themselves
- test task has dependency on check task
- check and test task almost the same in that testing your code without assembling it

**Build task**
- top level task of the task graph
- does both assemble and check tasks

if you run the tests without creating the jar file? -> run the `check` task

if you need to build your project, include creating jar file, without testing your changes? -> run the `assemble` task

## Compile classes - compileJava, compileTestJava

`compileJava` and `compileTestJava` are tasks added by java plugin to compile production and test code

These task use whatever java version you're using to run gradle to compile .java file into .class files

These .class files get output into the build directory

`src/main/java/....java` -> --> compileJava --> `build/classes/java/main/....class`

They both type of `JavaCompile` task class

### JavaCompile Task Class

It's task that compiles java source files, it used by compileJava and compileTestJava task of java plugin

This provides compileOption that many ways to configure the compilation

One of them is `setVorbose` option which print the compilation verbose

It can configure as follows:

```kotlin
 tasks.named<JavaCompile>("compileJava") {
      options.isVerbose = true
}
```

if you need to configure both tasks: 

```kotlin
tasks.withType<JavaCompile> {
    options.isVerbose = true
}
```

Like this configuration, we can configure compilation through `JavaCompile` task class against compileJava and compileTestJava 

## Manage Resources - processResources, processTestResources

The java plugin's `processResources` and `processTestResources` tasks let us manage resources 

When you execute the processResources task, it scans specific directories within your project, and copies their contents into the build directory 

`src/main/java/resources/hello.txt` --> processResources --> `build/resources/main/hello.txt`

The reason it's called "processResources" is that it can do additional processing along the way 

In fact, these task type of the `Copy` task class, as in addition to compileJava and compileTestJava tasks they also type of `JavaCompile` task class 

### Copy Task Class

To configure processResources and processTestResources task, it should to configure `Copy` task class

```kotlin
tasks.named<Copy>("processResources") {
    include("**/*.txt")
}
```

## Define Dependencies

The java plugin provides dependency configurations, which determine dependencies are added to the classpath  

`implementation`, `compileOnly`, `runtimeOnly`, `testImplementation`, `testCompileOnly`, `testRuntimeOnly`

## Package - jar

The java plugin provides a `jar` task that packages the compiled classes and resources into a single jar file in the build directory

The name of jar file is `projectName-projectVersion.jar` by default, using the project version in the build script

jar --> `build/libs/projectName-projectVersion.jar`

### Jar Task Class

Like that compileJava and processResources, jar task of java plugin is type of Jar task class

Jar task class can configure related project metadata 

## Run Tests - test

To run easily your test, the java plugin provides a `test` task that compiles your test code, process any resources, and then run the tests

It also produces a pretty test report in the build directory

The report tells us what's passed and what's failed

## Source Set

The java plugin also adds a source set to your project

**Source set** is a structure of directories that gradle uses to compile and package your code

`src/main/java` is for java source files and packages

`src/main/java/resources` is for java resources

`src/test/java` is for test java source files and packages

`src/test/resources` is for test java resources

**Build directory** contains all the gradle build outputs(generated classes, processed resources, test reports, jar files)

and importantly doesn't get committed into version control

`build/classes/java/main` is for compiled classes

`build/resources/main` is for processed resources

`build/classes/java/test` is for compiled test classes

`build/resources/test` is for processed test resources