[Library](#library)

[Consumer And Producer with micro project](#consumer-and-producer-with-micro-project)

[ABI](#abi)

[The java-library plugin](#the-java-library-plugin)

[Maven .pom XML file](#maven-pom-xml-file)

## Library

Library is producer that it generates an artifact to be consumed by others

So that, as a producer of a library you need to think about how library transitive dependencies affect your consumer that is standalone application

By default, **any implementation dependencies of the library you're creating will end up on the runtime classpath of any consumer, but not on the compile classpath**

## Consumer And Producer with micro project

[theme-park-status of playground project](../playground/theme-park-status) is a producer of a library that is consumed by [theme-park-api application](../playground/theme-park-api)

This library has dependencies as follows:

```kotlin
dependencies {
    implementation(libs.guava)
    implementation(libs.apache.commons.lang3)
    implementation(libs.jackson.databind)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

The theme-park-status library then respond with the corresponding status according to the given parameter using the dependencies

```java
// theme-park-status code

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

public static ObjectNode getRideStatus(String ride) {
        List<String> rideStatuses = readFile(String.format("%s.txt",
                StringUtils.trim(ride)));
        String rideStatus = rideStatuses.get(new Random().nextInt(rideStatuses.size()));
        
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("status", rideStatus);
        
        return node;
    }
```

A "ObjectNode" is a class from Jackson library, and be used in method signature

And a "StringUtils" of Apache Commons Lang library is used in the method body

The important thing that "ObjectNode" in on the **application binary interface** or **ABI** of the class since it contains in method signature, 

whereas "StringUtils" is not on the ABI since it is used in the method body

**publish to local maven repository and pull it**
1. `./gradlew :theme-park-status:publishToMavenLocal`
2. "theme-park-api" application add the dependency to the "theme-park-status" library, and add repository to the "mavenLocal()" in the build script

```kotlin
// theme-park-api build.gradle.kts

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
//    implementation(libs.springboot.web) // not used for a while
    implementation("com.gradle.theme-park:theme-park-status:1.0.0")
}
```

**Run the application or `./gradlew :theme-park-api:assemble`**
- The application will be execution failed for `compileJava` task
- Since ObjectNode is on the ABI of the library, so that it is required at compile time by consuming application

```java
// theme-park-api code

import com.gradle.theme_park.status.RideStatusService;

pubilc class ThemeParkApiApplication {
    public static void main(String[] args) {
        System.out.println(RideStatus.getRideStatus("rollercoaster"));
    }
}
```

## ABI

Application Binary Interface, ABI is that any types used within it need to be available on the compile classpath of the consuming application

So that, ABI types of a library need to be on the compile and runtime classpaths of any consumer

**ABI types include:**
- return types
- public method parameters 
- types used in parent classes or interfaces

**Non-ABI types include:**
- types used in method bodies
- types defined in private method declarations

### Classpath of Consuming Application

The classpath of the "theme-park-api" as a consumer application as follows:

**compile classpath:**
- theme-park-status
- **jackson-databind**
  - as RideStatusService of theme-park-status exposes ObjectNode of jackson-databind on its method signature

**runtime classpath**
- theme-park-status
- jackson-databind
- **commons-lang3**
  - since StingUtils of commons-lang3 is used internally in RideStatusService

If we could build up these classpath selectively above, the benefits would be:
- cleaner classpaths and potential faster compilation
- won't accidentally use a library that we haven't depended on explicitly
- no recompilation when artifacts defined only on the runtime classpath change

## The java-library plugin

It's gradle's core plugin and is make fine-grained classpath control possible

It allows to you as creator of a library to define which dependencies should be included in the compile or runtime classpaths of whatever application is consuming the library

Since this plugin adds additional dependency configuration that is called `api`,

"api dependencies" are part of the library's ABI, and will therefore **appear on the compile and runtime classpaths of any consumer**

"implementation dependencies" aren't part of the ABI, and will **only appear on the runtime classpath of consumers**

**Retouching micro project**
1. Swap the java plugin for the java-library plugin in the "theme-park-status library"
2. Change the jackson-databind dependency declaration from `implementation` to `api` to make it part of the library's ABI
3. Run `publishToMavenLocal`
4. Back in "theme-park-status application"
5. Pick up the latest version of the library and figured out that jackson-databind now also belongs on the compile classpath
6. Run the application or `./gradlew :theme-park-api:assemble`

## Maven .pom XML file

