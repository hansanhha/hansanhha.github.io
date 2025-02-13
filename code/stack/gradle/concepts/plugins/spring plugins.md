## Spring Plugins

spring boot provides two useful gradle plugins:
- Spring Boot plugin
- Spring Dependency Management plugin

**Spring Boot plugin**
- plugin id : `org.springframework.boot`
- registers tasks
  - `bootRun` task : run spring boot application, which detects and runs main Spring Boot class
  - `bootJar` task : generate an executable jar file

**Spring Dependency Management plugin**
- plugin id : `io.spring.dependency-management`
- automatically applies version to spring boot dependencies(`springboot-starter-*`)