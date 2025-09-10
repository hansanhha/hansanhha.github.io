## Three-phase of Build lifecycle

1. 초기화(initialization)
2. 구성(configuration)
3. 실행(execution)

## Initialization phase

gradle이 settings.gradle.kts 파일을 실행해서 build를 수행할 proejct들을 골라내는 단계

settings.gradle.kts에 나열된 subproject를 빌드에 포함시킴

멀티 프로젝트인 경우 빌드할 프로젝트를 선택할 수 있음

## Configuration phase

gradle이 각 project의 build.gradle.kts를 실행해서 각 project에 대한 model(빌드 구성)을 만드는 단계

project 별로 존재하는 모든 task 등록 등

## Execution phase

gradle이 command line을 통해 전달받은 값을 기반으로 task들을 수행하는 단계

실제 task들이 모두 실행되면 build를 마침

task를 수행한 후 변경 사항이 없는 상태에서 다시 task 실행을 요청하면 수행하지 않음 - Incremental Build 기능

## Build Process

### Typical build

```text
Initialization(settings.gradle)
1. Create Project
2. Select which projects to build

Configuration(build.gradle)
1. Create Tasks
2. Configure all tasks

Execution
1. Decide which tasks to execute
2. Execute task(based on the values passed from the command)

Complete

Execute task again without any changes -> Incremental Build 
```

### Configuration avoidance build(up-to-date)

```text
Initialization(settings.gradle)
1. Create Project
2. Select which projects to build

Configuration(build.gradle)
1. Register Tasks

Execution
1. Decide which tasks to execute
2. Configure relevant tasks
3. Execute task(based on the values passed from the command)
```

일반적인 빌드 프로세스와의 차이점
- configuration 단계에서 task들을 등록함 : 실제로 task들을 생성하진 않고 gradle에 등록한 상태
- execution 단계에서 실제 실행할 task들만 생성, 구성(configuration lambda)을 한 뒤 실행함
- 실행하지 않는 task들에 대한 불필요한 구성 과정을 생략할 수 있는 빌드 처리임