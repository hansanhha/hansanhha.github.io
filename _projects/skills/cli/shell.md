---
layout: default
title:
---

#### index
- [overview](#overview)
- [change default shell](#change-default-shell)
- [subshell](#subshell)
- [interactive/non-interactive shell](#interactivenon-interactive-shell)
- [job: foreground, background](#job-foreground-background)
- [login config files, environment variables](#login-config-files-environment-variables)
- [shell variables](#shell-variables)
- [command](#command)
- [$PATH](#$path)
- [basic commands](#basic-commands)
- [command substitution](#command-substitution)
- [process substitution](#process-substitution)
- [redirection, pipeiine](#redirection-pipeline)
- [shell script](#shell-script)


## overview

쉘이란 사용자와 운영체제 커널 사이에서 명령을 해석하고 실행하는 프로그램으로 bash, zsh, tcsh, fish, nu 등이 있다

사용자가 키보드로 명령을 입력하면 쉘이 이를 해석하고 실행하는 역할을 하며 이러한 쉘을 "대화형 쉘"이라고 한다

대화형 쉘은 가상 콘솔 터미널에 로그인하거나 gui의 터미널 에뮬레이터를 실행할 때마다 실행된다

대화형 쉘의 명령 처리 흐름

사용자 명령어 입력 -> 터미널이 쉘에게 전달 -> 쉘의 명령어 해석 및 실행할 프로세스 생성 -> os 커널의 명령 실행 -> 쉘의 실행 결과 수신 및 터미널 전달 -> 실행 결과 출력 

```text

		  ---------------------------
		  |			    |
		  |  ----terminal(tty)----  |
		  |  |	       ^	 |  |
		  |  |	       |	 |  |
		  |  |	       V	 |  |		
		  |  |	   ---------	 |  |
user input <-------->|     | shell |     |  |
		  |  |	   ---------	 |  |
		  |  |	       ^	 |  |
		  |  ----------|----------  |
		  |	       |	    |
		  |	       V 	    |
		  |    operating system	    |
		  |			    |
		  ---------------------------
```

터미널(gui 터미널 에뮬레이터)은 사용자와 쉘이 상호작용할 수 있도록 입출력을 제공하는 인터페이스지만, 다음의 경우 터미널없이 쉘 자체로 실행될 수 있다

```text

		  ---------------------------
		  |			    |
		  |  	   ---------	    |
		  |   	   | shell |        |
		  |  	   ---------	    |
		  |  	       ^	    |
		  |  	       |	    |
		  |	       |	    |
		  |	       V 	    |
		  |    operating system	    |
		  |			    |
		  ---------------------------
```

시스템 쉘
- 시스템 쉘이란 사용자의 입력을 받는 대화형 쉘과 달리, 시스템 부팅 시 실행되는 쉘이나 스크립트 실행 시 사용되는 쉘을 말하며 터미널없이 실행된다
- 환경 변수를 로드하지 않는다
- 일반적으로 /bin/sh로 설정되어 있다

스크립트 파일(.sh, .zsh 등)
- 쉘 스크립트는 터미널 없이 실행할 수 있다
- cron, systemd 같은 시스템 프로세스가 쉘을 실행할 때는 터미널이 필요없다

원격 실행(pty)
- ssh 등을 통해 원격 컴퓨터에 접속하여 명령을 실행하는 경우 터미널없이 실행된다

gui 환경에서 실행되는 쉘
- gui 환경에서 터미널을 띄우지 않고도 특정 쉘을 실행할 수 있다
- macos의 .command 파일 실행 등


## change default shell

`echo $SHELL` 명령어로 현재 로컬 호스트의 기본 쉘이 무엇인지 확인할 수 있다

```shell
$ echo $SHELL
/bin/zsh
```

`cat /etc/shells` 명령어로 사용 가능한 쉘의 목록을 확인할 수 있다 (다른 경로에 설치된 쉘은 제외됨)

```shell
$ cat /etc/shells

/bin/bash
/bin/csh
/bin/dash
/bin/ksh
/bin/sh
/bin/tcsh
/bin/zsh
```

리눅스의 경우 bash, 맥os의 경우 zsh를 기본 쉘로 사용한다

기본 쉘을 다른 쉘로 변경하려면 `chsh` 명령어를 활용한다

```shell
# nu 쉘을 표준 쉘에 추가
$ echo /opt/homebrew/bin/nu | sudo tee -a /etc/shells

# 기본 쉘을 nu로 변경
$ chsh -s /opt/homebrew/bin/nu
```


## subshell 

쉘도 결국 사용자의 명령을 처리하는 프로세스이다

즉, 사용자가 명령을 입력하면 그에 맞는 프로세스를 생성한다

```text
user input -> shell (parent process) -> execution command -> child process creation
```

새로 생성된 프로세스는 부모 프로세스인 쉘에 종속되며, 부모인 쉘이 종료되면 자식 프로세스도 종료될 가능성이 높다

쉘에서 또 다시 자식 쉘을 생성할 수 있는데 이러한 쉘을 서브쉘이라고 한다

아래의 명령어는 현재 쉘에 zsh 자식 쉘을 생성한다

```shell
$ zsh
```

어떤 텍스트 출력없이 프롬프트를 반환하여 아무 일도 일어나지 않은 것 같지만 현재 쉘에 서브 쉘이 생성된 것을 pstree 명령을 통해 계층적으로 확인할 수 있다 (macos의 경우 homebrew를 통해 pstree를 설치할 수 있음) 

25888: 사용 중인 ghostty 터미널 에뮬레이터

34812: 부모 zsh 프로세스

37763: 위의 명령어로 인해 생성된 자식 zsh 프로세스

```shell
$ pstree -p 34812
-+= 00001 root /sbin/launchd
 \-+= 25888 hansanhha /Applications/Ghostty.app/Contents/MacOS/ghostty
   \-+= 34811 root /usr/bin/login -flp hansanhha /bin/bash --noprofile --norc -c exec -l /bin/zsh
     \-+= 34812 hansanhha -/bin/zsh
       \-+= 37763 hansanhha zsh   
         \-+= 37823 hansanhha pstree -p 34812
           \--- 37824 root ps -axwwo user,pid,ppid,pgid,command
```

사실 쉘 프로세스에만 특별히 있는 규칙이라기보단 운영 체제의 전체적인 프로세스 관리 메커니즘을 적용한 것이다

운영체제에서 모든 프로세스는 다른 프로세스의 자식으로 생성된다

컴퓨터가 부팅될 때 최초의 루트 프로세스가 생성되며 이를 기반으로 다른 하위 프로세스가 계층적인 구조를 형성한다

프로세스는 부모 프로세스가 `fork()` 시스템 콜을 실행하면서 생성되는데, fork()는 부모 프로세스를 복제하여 새로운 자식 프로세스를 생성한다

서브쉘은 부모와 다른 프로세스 id를 가지며 부모 쉘과 독립적인 입출력/상태를 가지는 `()`, bash, zsh 등으로 생성된 별도의 환경이다

```shell
$ (echo "Hello")
$ bash
```

서브쉘은 새로운 프로세스를 생성하지만 job은 새로운 프로세스를 만들지 않을 수 있다

`ls` 같은 내장 명령어는 쉘 내부에서 처리되므로 별도로 프로세스가 생성되지 않지만 `()` `bash` 를 사용하면 새 프로세스가 생성된다


## interactive/non-interactive shell

대화형 쉘은 사용자가 쉘에게 입력(stdin)을 전달할 수 있는 cli 프롬프트를 제공하는 쉘을 말한다

만약 사용자가 시스템에 로그인하지 않고 쉘을 시작하면 비로그인 대화형 쉘로 `/etc/profile`은 처리하지 않고 `$HOME/.bashrc` 시동 파일만 처리한다 ([자세한 내용](#login-config-files-environment-variables))

비대화형 쉘은 사용자에게 cli 프롬프트를 제공하지 않는다는 점이 대화형 쉘과 다르다

일반적으로 쉘 스크립트 파일을 실행할 때 비대화형 쉘로 실행된다

참고로 쉘 스크립트는 경우에 따라 서브 쉘로 실행될 수 있는데, 서브 쉘은 부모 쉘이 가지는 전역 설정만 상속한다

부모 쉘에서 변수를 설정하고 export하지 않는다면 해당 변수는 지역 변수일 뿐이므로 쉘 스크립트가 실행되는 쉘에서는 접근할 수 없다

쉘 스크립트가 현재 쉘에서 실행된다면 이와 상관없이 현재 쉘의 지역/전역 변수에 모두 접근할 수 있다


## job: foreground, background

job은 쉘이 관리하는 실행 단위로써 쉘에서 실행한 명령어(프로세스) 그룹을 의미한다

foreground, background mode로 동작할 수 있으며 두 모드의 주요 차이점은 다음과 같다

foreground: 기본적으로 실행되는 모드, 쉘이 해당 프로세스가 끝날 때까지 기다린다. 실행 중인 동안 사용자는 다른 명령을 입력할 수 없으며 프로세스가 종료되거나 강제 종료(ctrl + c)등을 통해 새로운 명령을 실행할 수 있다

background: 프로세스를 백그라운드에서 실행하는 모드. 명령 실행 후 프롬프트가 바로 반환되어 해당 프로세스의 종료 여부와 상관없이 다른 명령을 입력할 수 있다

jobs 명령어로 현재 쉘에서 수행 중인 job 목록을 확인할 수 있으며 `fg` `bg` 명령어를 통해 특정 job의 모드를 변경할 수 있다

```shell
$ jobs
[1]    running    sleep 100
[2]  - running    sleep 50
[3]  + running    sleep 30
```

+는 fg를 입력했을 때 포그라운드로 전환될 작업을 의미한다

-는 fg 실행 시 +job이 끝난 후 다음으로 포그라운드로 전환될 작업을 의미한다

`fg` `bg`를 입력하여 전환 대상의 job을 제어하거나 `fg %n` `bg %n` 과 같이 명시적으로 특정 job을 제어할 수도 있다
 
또한 job은 여러 개의 프로세스로 구성될 수 있다

아래의 명령어는 2개의 프로세스로 이루어진 하나의 job이다

```shell
$ sleep 100 | ps aux
```

참고로 서브쉘도 하나의 job으로 관리될 수 있으나 모든 job이 서브쉘이 될 수는 없다

#### 1. background execution

```shell
$ sleep 100 &
[1] 40259
```

프로세스를 백그라운드로 실행하면 백그라운드 작업이 되며 쉘이 작업 번호 및 프로세스 id와 함께 프롬프트를 바로 반환한다

쉘에서 작업을 관리하지만 프로세스는 운영체제가 관리한다

다만 부모 쉘이 종료되면 자식 프로세스가 SIGHUP 신호를 받기 때문에 종료될 가능성이 있다 (부모 쉘과 완벽히 분리되지 않는다)

참고로 시스템에서 실행되는 cron, systemd 프로세스는 특정 쉘의 작업이 아니다

#### 2. nohup + background execution

```shell
$ nohup sleep 100 &
```

프로세스를 백그라운드로 실행하면서 부모 쉘 프로세스의 SIGHUP 신호를 무시한다

표준 출력과 에러를 현재 디렉토리의 nohup.out 파일에 추가한다

부모 쉘이 종료돼도 프로세스가 살아있게 된다

#### 3. disown

```shell
$ sleep 100 &
$ disown
```

현재 쉘에서 job을 분리하여 쉘이 종료되더라도 job 프로세스가 영향을 받지 않는다

#### 4. setsid

```shell
$ setsid sleep 100
```

setsid 명령어는 리눅스에서만 지원되며 쉘과 완전히 독립된 세션에서 job을 실행한다(쉘 종료와 관계없이 계속 실행된다)


## login config files, environment variables

쉘은 크게 세 가지 방법으로 실행된다
- 기본 로그인 쉘
- 서브쉘 (대화형 쉘)
- 스크립트를 실행시키기 위한 비대화형 쉘

쉘은 자신이 실행될 때 쉘의 세션 환경에 대한 정보들을 구축하기 위해 특정 파일들을 실행시켜 값을 설정하는 일련의 과정을 거치는데 이 파일들을 시동 파일이라고 한다

로그인 쉘/비로그인 쉘마다 실행되는 시동 파일의 목록과 순서가 다르며 그 구성은 아래와 같다

로그인 쉘
- 시스템에 처음 로그인할 때 실행되는 쉘
- ssh 접속(pty), tty 접속, gui 터미널 에뮬레이터를 통한 터미널 실행 시 사용된다
- 사용자 인증 후 실행되며 시스템 환경 변수 및 사용자 설정을 불러온다

비로그인 쉘
- 현재 실행 중인 쉘에서 새로운 쉘을 실행하거나 스크립트 실행 시 사용되는 쉘
- `~/.bashrc` 파일만 실행하며 로그인 쉘의 환경변수를 그대로 물려받는다

로그인 쉘은 실행될 때 시스템 및 사용자의 설정 파일을 아래의 순서로 읽는다 (아래는 bash 쉘을 기준으로 하며 os 및 쉘의 종류에 따라 파일명이 다르거나 파일이 추가/생략될 수 있음)
- `/etc/profile`: 시스템의 기본 PATH를 설정한다 (macos의 경우 `/etc/profile`에서 `/usr/libexec/path_helper`를 통해 $PATH 및 $MANPATH를 설정한다)
- `/etc/bashrc`:  대화형 쉘에 대한 전역 설정
- `~/.bash_profile`: 사용자별 쉘 설정 (쉘마다 파일명이 다름) - 로그인 시 한 번만 실행되는 개별 사용자 환경 설정 파일로, 보통 `~/.bashrc` 파일을 로드하는 역할을 한다 -> 로그인 쉘과 비로그인 쉘의 동일한 환경을 유지하기 위해 이런 방식을 사용한다
- `~/.bashrc` 파일은 비로그인 쉘을 실행할 때 로드되는 파일로 일반적으로 쉘 환경을 설정할 때 이 파일을 사용한다 
- `~/.bash_login`: bash_profile이 없는 경우 사용
- `~/.profile`: bash_login이 없는 경우 사용(posix 표준 설정 파일로 bash 뿐만 아니라 다른 쉘에서도 사용할 수 있다)

`/etc/profile` `/etc/bashrc` 파일만 로그인 쉘에서 실행되는 특별한 시동 파일이고, 나머지 파일 (`$HOME/.bash_profile` `$HOME/.bashrc` 등)은 로그인/비로그인 쉘에서 공통적으로 실행된다

시동 파일을 변경한 후 즉시 쉘에 적용하려면 `source` 명령어를 사용하거나 새 터미널을 연다

```shell
$ source ~/.zshrc
```

이렇게 복잡하게 설정 파일을 구성해놓은 이유가 뭘까?

일단 os마다 지원되는 파일의 종류가 다른데 설명을 위해 위에서 여러 종류의 파일을 언급하여 그렇게 보이는 부분도 있긴 하다 

쉘은 쉘 세션과 작업 환경에 대한 정보를 저장하기 위해 환경 변수를 제공하는데, 이를 통해 쉘에서 실행 중인 프로그램이나 스크립트가 데이터를 저장하거나 사용할 수 있다

환경 변수의 종류는 전역 변수와 지역 변수로 구성되며 사용자가 필요에 따라 지역/전역 변수를 정의할 수 있도록 유연한 구성 파일 메커니즘을 쉘에서 지원하는 것이다

환경 변수에 대해서 좀 더 자세히 살펴보자

### local environment variables

지역 환경 변수란 변수를 정의한 지역 프로세스에서만 범위를 가지는 변수이며, 시스템 차원에서 제공하는 표준 지역 환경 변수와 사용자가 정의한 사용자 정의 지역 변수로 구성된다

set 명령을 통해 확인할 수 있지만 이외에도 다른 변수들이 포함되어 출력되기 때문에 가독성이 떨어진다

```shell
$ set
ZSH=/Users/hansanhha/.oh-my-zsh
COLORTERM=truecolor
LANG=en_US.UTF-8
```

커스텀 지역 변수는 대화형 쉘 또는 쉘 스크립트 파일에서 다음과 같은 형식으로 만들 수 있다

```shell
# 전부 대문자로 표현하는 전역 변수와 달리 커스텀 지역 변수는 소문자를 사용한다
# 변수 이름, 등호, 값 사이에 빈 칸을 절대로 사용하지 않는다 (대부분의 쉘은 빈 칸을 별개의 명령으로 해석한다)
$ my_name=hansanhha     

# 빈 칸이 포함된 문자열은 겹/홑따옴표를 사용한다
$ greet='hello world'   
$ greet2="hello world"

# 변수 앞에 $를 붙여서 쉘이 명령어가 아닌 변수임을 인식하도록 한다
$ echo $my_name
hansanhha

$ echo $greet
hello world
```

### global environment variables

전역 변수는 현재 쉘 세션뿐만 아니라 하위 쉘에서도 유지되는 변수로 export 키워드를 통해 특정 변수를 전역 환경으로 내보냄으로써 설정된다

```shell
$ my_name=hansanhha

# 전역 변수로 설정할 때는 달러 기호를 사용하지 않는다 
$ export my_name

$ echo my_name
hansanhha
```

전역 변수를 설정하면 서브쉘에서 이 변수에 접근할 수 있다

하지만 자식 쉘에서 전역 변수를 변경해도 해당 쉘 안에만 변경되고 부모의 쉘에는 영향을 주지 않으며 export 명령을 사용해도 적용되지 않는다

### control environment variables

전역 범위로 환경 변수를 설정해도 해당 쉘에서만 유효하고 다른 쉘에는 적용되지 않으며 쉘이 종료되면 그 변수가 사라진다

어느 쉘에서 실행하든지 영구하게 환경 변수를 유지하고 싶으면 시동 파일에 환경 변수를 배치하면 된다

가장 좋은 파일은 `$HOME/.bashrc`이며 이 파일은 모든 유형의 쉘 프로세스에 대해 적용된다

나는 zsh 쉘을 사용하고 있는데 `$HOME/.zshrc` 파일에 다음과 같이 자바에 대한 전역 변수를 설정하고 있다

```shell
export JAVA_HOME=$(asdf where java)
export PATH=$PATH:$JAVA_HOME/bin
```

지역/전역으로 설정된 환경 변수는 `unset` 명령어를 통해 삭제할 수 있다

```shell
# 변수 이름에 달러 기호를 붙이지 않는다
$ unset my_name
```


## shell variables

변수 사용에 대한 규칙을 정리해보자

```shell
# 해당 쉘에 지역 변수 설정
# 변수 이름, 등호, 값 사이에 공백을 허용하지 않는다
# 서브쉘에서 접근할 수 없다
$ name=value


# 전역 변수로 설정(현재 쉘과 서브쉘에서 접근 가능)
# 영구적으로 적용되지 않으며 현재 쉘이 종료되면 변수도 함께 사라진다
# 시동 파일에 값을 설정하면 모든 쉘에 적용할 수 있다
$ export name


# 변수에 접근할 때는 달러 기호를 붙여서 쉘에게 변수임을 알려준다
$ echo $name
value
$ echo "$name"
value

# 홑따옴표로 감싸면 문자열 자체를 출력한다
$ echo '$name' 
$name
```

쉘에는 지역/전역 환경 변수를 제외한 다른 특수한 변수들을 지원한다

#### positional parameters

위치 변수는 스크립트 실행 시 전달된 인자를 나타내는 변수이다

`$0`, `$1`, `$9`, `$@`, `$*` 등을 사용할 수 있다

```shell
# $0: greet.sh
# $1: hello
# $2: world
./greet.sh hello world
```

#### special variables

쉘에서 제공하는 예약된 변수들을 특수 변수라고 한다

`$?`: 마지막 실행한 명령어의 종료 상태 (정상이라면 0을 반환한다)

`$$`: 현재 쉘 프로세스의 pid

`$!`: 마지막으로 실행한 백그라운드 프로세스의 pid

`$@`: 모든 인자를 개별적으로 나열 (`$1` `$2` ...)

`$*`: 모든 인자를 하나의 문자열로 반환 (`$1 $2 ...`)

### variable substitution

`$name`이 name 변수에 대한 접근을 의미한다면 `${name}`은 변수의 값을 출력하여 다른 변수에 할당하거나 명령어의 인자로 사용될 수 있는 변수 치환을 의미한다

참고로 `$()` 표현식은 변수 치환이 아닌 명령어 치환을 의미한다

```shell
$ name=hansanhha
$ echo $name
hansanhha

# user 변수에 $name의 값을 할당한다
# 달러 기호를 뺀 user=name은 user 변수에 name 문자열 자체를 할당한다
$ user=${name}
$ echo $user
hansanhha
```

```shell
# hello가 없으면 기본 값을 출력한다
$ echo ${hello:-"default value"}
default value

# hello가 없으면 hello의 기본 값을 "default valut"로 설정한다
$ echo ${hello:="defalut value"}
$ echo hello
defalut value

# hello가 없으면 오류 메시지를 출력한다
$ echo ${hello:="require greeting message"}
zsh: hello: "require gretting message"
```


## command

쉘에서 실행되는 명령어는 하나의 프로그램이다

이 명령어들은 대게 쉘에서 자체적으로 제공하는 내장 명령어(built-in command)와 외부 명령어(external command)로 구분된다

#### built-in command

쉘 자체에서 제공하며 별도의 실행 파일이 아니라 쉘 프로세스 내부에서 실행되는 명령어를 말한다

일반적으로 시스템 관리, 환경 설정, 프로세스 관리 등에 사용된다

프로세스를 생성하지 않기 때문에 실행 속도가 빠르다

```shell
# bash built-in command
$ ls
```

`type` 명령어를 통해 해당 명령어가 내장 명령어인지, 어떤 약어인지 확인할 수 있다

```shell
$ type cd
cd is a shell builtin

$ type ls
ls is an alias for ls -G
```

#### external command

시스템에 설치된 독립적인 실행 파일로 보통 `/bin/` `/usr/bin/` 같은 디렉토리에 저장된다

실행할 때마다 새로운 프로세스를 생성한다

```shell
$ type -a ls
ls is an alias for ls -G
ls is /bin/ls
```

#### custom command

사용자가 직접 만든 스크립트 또는 프로그램을 쉘 명령어처럼 실행하는 것을 말한다

보통 쉘 스크립트(.sh), 실행 파일(chmod +x)이며 쉘 환경에서 명령어처럼 사용하려면 $PATH 환경변수 등록하거나 사용자의 로컬 bin 디렉토리에 실행 파일을 옮긴다

```shell
$ echo 'echo "hello, shell script"' > mycmd.sh
$ chmod +x mycmd.sh  # 실행 권한 부여
$ ./mycmd.sh         # 실행
```

```shell
$ sudo mv ./mycmd.sh /usr/local/bin/mycmd  # 로컬 bin 디렉토리로 이동
$ chmod +x /usr/local/bin/mycmd            # 실행 권한 부여
$ mycmd
```

다음과 같이 `alias` 명령어로 약어를 만들 수도 있다

프롬프트에서 설정한 약어는 해당 쉘 범위에서만 적용된다

모든 쉘에서 약어를 적용하려면 [설정 파일](#configprofile-environment-variables)에 명시해야 한다

```shell
$ alias ll='ls -lah'  # key=value 형식으로 공백을 허용하지 않는다
$ ll                  # 실행 시 ls -lah와 동일
```

### how command works

쉘은 사용자가 입력한 문자열을 공백을 기준으로 토큰으로 구분한 뒤 명령어와 옵션, 인자로 해석한다

```shell
$ ls -l ./code/project
```

첫 번째 토큰: `ls` (명령어)

두 번째 토큰: `-l` (옵션/플래그)

세 번째 토큰: `./code/project` (인자)

쉘은 첫 번째 토큰을 명령어로 가정하고 다음과 같은 순서로 해당 문자열을 실행할 수 있는 파일/약어를 탐색한다

- 내장 명령어인지 확인 -> 내장 명령어라면 쉘 프로세스 내에서 바로 실행
- 약어 확인 -> 약어의 명령어 종류에 따라 바로 실행 또는 다시 탐색
- $PATH 환경 변수에 정의된 디렉토리를 순차적으로 탐색하여 실행 파일 검색

실행 파일을 찾은 경우 새로운 프로세스(fork, exec)를 생성한 후 옵션과 인자를 전달하여 해당 파일을 실행한다

명령어의 실행이 끝나면 종료 상태 코드를 반환하는데 `$?`로 확인할 수 있다

만약 실행 파일을 찾지 못한다면 `command not found`라는 문구를 출력한다

참고로 쉘은 기본적으로 보안 문제로 인해 현재 디렉토리(.)를 $PATH에 포함하지 않기 때문에 $PATH에 추가하지 않고 현재 디렉토리의 실행 파일을 실행하려면 `./`을 명시적으로 붙여야 한다

```shell
$ ./hello.jar
```

### single hyphen flag vs double hyphen flag 

unix command에서 `-`와 `--`는 옵션(플래그) 형식을 나타낸다

`-`는 한 글짜리 플래그를 사용할 때 사용되며 여러 문자를 조합하여 사용할 수 있다

```shell
$ ls -l -a -h
$ ls -lah
```

`--`는 사람이 읽을 수 있는 긴 문자열의 옵션을 사용할 때 사용되며, 쉘이 각 문자로 인식하지 않고 문자열 전체로 인식한다

`=` 등호로 플래그에 값을 할당할 수 있다

```shell
$ ls --all         # a, l, l 대신 all로 파싱한다
$ ls --color=auto  # key=value 형태로 값을 할당할 수 있으며 공백을 허용하지 않는다
```


## $PATH

$PATH 환경 변수는 쉘이 실행 가능한 파일(명령어)을 찾을 때 검색하는 디렉토리 목록을 가지고 있다

쉘은 $PATH에 나열된 디렉토리를 순차적으로 탐색하여 실행 가능한 파일을 찾고, 파일을 찾으면 탐색을 중단한다 ($PATH의 순서를 조작하여 특정 프로그램을 먼저 실행하도록 설정할 수 있음)

기본적으로 포함되는 디렉토리 목록

`/bin` (basic system binaries)
- 일반 사용자와 시스템이 사용하는 기본 명령어 (`ls` `cp` `cat` `echo` 등)
- 시스템이 부팅될 때 반드시 필요한 명령어들이 포함된다 (최소한의 명령어로 구성됨)
- 모든 사용자가 실행할 수 있다

`/sbin` (system binaries)
- 시스템 관리자가 사용하는 시스템 관리 명령어 (`fsck` `reboot` `iptab`/`iptables` 등`)
- 일반 사용자는 실행할 수 없고 sudo를 사용해야 실행할 수 있다

`/usr/bin` (user system binaries)
- os의 패키지 관리자 또는 os에서 기본적으로 제공하는 일반 실행 파일 (`vim` `wget` `curl` `git` 등)
- 일반 사용자가 실행할 수 있는 대부분의 유틸리티 프로그램을 포함한다 

`/usr/sbin` (user system administrator binaries)
- os의 패키지 관리자 또는 os에서 제공하는 시스템 관리 도구 (`apachectl` `ipconfig` 등)
- 루트 사용자만 실행 가능

`/usr/local/bin` (locally installed binaries)
- 시스템 패키지 관리자가 아닌 사용자가 직접 설치한 파일을 저장하는 공간
- $PATH에서 /usr/bin보다 우선순위가 높다

`/usr/local/sbin` (locally installed system binaries)
- 사용자가 직접 설치한 시스템 관리 도구가 위치하는 디렉토리
- 수동 설치한 nginx, redis-server, apache2 등
- /usr/sbin과 같은 역할이지만, 시스템 패키지가 아닌 사용자가 직접 설치한 관리자 도구가 위치한다

`$HOME/bin` (user personal scripts)
- 개별 사용자가 만든 실행 파일이나 스크립트를 저장하는 공간
- 각 사용자마다 개별적인 실행 파일을 관리할 수 있다

macos의 경우 SIP(system integrity protection)라는 보안 기능을 통해 `/usr/bin`과 `/usr/sbin`을 일반 사용자가 수정하지 못하도록 한다

해당 디렉토리에 프로그램을 설치하거나 sudo 명령어로 삭제를 할 수 없으며 macos 업데이트를 통해서만 애플이 디렉토리를 관리한다


## basic commands

### 1. file/directory control commands

`ls`: 현재 디렉토리 목록 출력

`ls -l`: 상세 정보(권한 등)와 함께 파일 목록 출력

`ls -a`: 숨김 파일 포함 목록 출력

`cd directory`: 해당 디렉토리로 이동

`cd ..`: 상위 디렉토리로 이동

`cd -`: 이전 디렉토리로 이동

`pwd`: 현재 디렉토리 경로 출력

`mkdir directory`: 디렉토리 생성

`rmdir directory`: 디렉토리 삭제

`rm -r directory`: 디렉토리와 그 내부 파일 삭제

`touch file`: 빈 파일 이름 생성

`rm file`: 파일 삭제

`cp source target`: 파일/디렉토리 복사(디렉토리의 경우 -r 플래그 사용)

`mv source target`: 파일/디렉토리 이동(이름 변경으로도 사용 가능)

### file content control commands

`cat file`: 파일 내용 출력

`tac file`: 파일 내용을 거꾸로 출력

`less file`: 긴 파일을 스크롤하며 확인

`head -n 10 file`: 파일의 처음 10줄 출력

`tail -n 10 file`: 파일의 마지막 10줄 출력

`echo content": 문자열 출력

`echo content > file`: 파일에 내용 저장(덮어쓰기)

`echo content >> file`: 파일에 내용 추가

`grep <search term> file`: 파일에서 특정 문자열 검색

`find path -name filename`: 특정 파일 검색

### process management commands

`ps aux`: 현재 실행 중인 프로세스 목록 출력

`top` `gtop` : 실시간 프로세스 모니터링

`kill pid`: 프로세스 종료

`kill -signal_number pid`: 프로세스에 시그널을 전송하며 프로세스 종료

`pkill process_name`: 프로세스명을 기준으로 종료

`jobs`: 백그라운드 작업 확인

`fg [%job_id]`: 백그라운드 작업을 포그라운드로 전환

`bg [$job_id]`: 일시정지된 작업을 백그라운드로 전환

`set`: 특정 프로세스에 대한 모든 변수(전역/지역/사용자) 표시

`env`: 전역 환경 변수 표시

`printenv`: 전역 환경 변수 표시

### user and permission commands

`whoami`: 현재 로그인한 사용자 확인

`id`: 사용자 및 id 그룹 확인

`who`: 현재 로그인한 사용자 목록

`su username`: 다른 사용자로 전환

`sudo command`: 관리자 권한으로 명령어 실행

`chmod mode file`: 파일 권한 변경

`chown user:group file`: 파일 소유자 변경

### network commands

`ping domain`: 서버 응답 확인

`ifconfig` `ip a`: 네트워크 인터페이스 정보 확인

`netstat`: 네트워크 상태 확인

### utility commands

`history` `CTRL + r/s`: 이전에 실행한 명령어 목록 확인

`clear` `CTRL + l`: 터미널 화면 정리

`alias abbr=command`: 명령어 단축키 설정

`unalias abbr`: 설정한 alias 삭제

`df -h`: 디스크 공간 확인

`du -sh file`: 파일/디렉토리 크기 확인

`uptime`: 시스템 가동 시간 확인

`date`: 현재 날짜 및 시간 출력

`exit`: 쉘 종료

`which command`: 사용자가 실행하는 명령어 위치 확인 ($PATH 환경변수에서 첫 번째로 발견된 실행 파일 전체 경로 출력)

`where command`: 사용자가 실행하는 명령어 위치 확인 ($PATH 환경변수에서 찾은 모든 실행 파일 경로 출력)

`command command`: 명령어 실행 또는 명령어 속성 확인 (`-v` `-V` 옵션)

`type command`: 명령어 유형 확인

`source file`: 지정한 스크립트 파일을 현재 쉘에서 실행 (서브쉘을 생성하지 않기 때문에 해당 스크립트 파일의 변경사항이 현재 쉘에 적용된다)


## command substitution

명령어 치환이란 쉘에서 명령어의 실행 결과(출력값)를 변수에 저장하거나 다른 명령어의 인자로 사용할 때 쓰는 기능을 말한다

쉘에서 명령어를 치환하는 방법은 백틱 방식과 `$()` 방식이 있다

백틱 사용(중첩 사용이 어렵고 가독성이 떨어지는 방식)

```shell
# ls 명령어의 실행 결과를 output 변수에 저장
$ output=`ls `

$ echo $output
hello.md
myshell.sh
```

`$()` 사용(중첩 사용이 가능하고 가독성이 좋은 방식)

```shell
$ output=$(ls)
$ echo $output

$ echo $output
hello.md
myshell.sh
```

### how command substitution works

쉘이 입력받은 문자열 중 `$()` 또는 백틱을 인지하면 다음과 같은 처리 과정을 수행한다
- `$()` 내부의 명령어 실행한다 -> 서브쉘을 생성하여 명령을 실행한다
- 출력된 결과(stdin)를 문자열로 치환하거나 변수에 대입한다
- 해당 문자열 또는 변수는 다른 명령어의 인자로 사용될 수 있다

아래의 명령어를 실행해보면 치환되는 명령어가 서브쉘을 통해 실행되는 것을 쉽게 알 수 있다

```shell
# 현재 쉘 프로세스에 변수 할당
$ MY_VAR="hello" 

# 서브 쉘에서 같은 변수 재할당 및 출력
$ echo $(MY_VAR="world"; echo $MY_VAR)
world

# 쉘에서 설정한 변수의 값은 변경되지 않았다
$ echo $MY_VAR
hello
```

## process substitution

프로세스 치환은 명령어의 실행 결과를 파일로 다루는 기법을 말한다

[명령 치환](#command-substitution)은 명령의 실행 결과를 문자열로 출력하는 반면, 프로세스 치환은 명령어의 출력을 파일처럼 입력으로 제공하거나 다룬다

문자열이 아닌 파일을 입력으로 받는 명령어를 사용할 때 명령 치환 대신 프로세스 치환을 사용한다

`<()`: 입력 프로세스 치환, 명령어의 출력을 임시파일로 만들어 다른 명령의 입력으로 제공한다 (`<` 입력 리다이레션과 유사한 개념)

`>()`: 출력 프로세스 치환, 명령어의 출력을 다른 명령어로 보내기 위해 임시 파일로 만든다 `>`과 연결되어 다른 명령어에 전달된다

```shell
# 입력 프로세스 치환 사용
$ cat <(echo "hello process substitution")
hello process substitution

# 출력 프로세스 치환 사용
$ ls > >(cat)
file1.txt
file2.txt
```


## redirection, pipeline

리다렉션과 파이프라인은 쉘에서의 입력과 출력을 다루는 개념으로 명령어 실행 결과를 다른 파일에 저장하거나 여러 개의 명령어를 조합하여 작업을 실행할 때 사용된다

#### redirection

기본 입출력을 변경하는 기능으로 표준 출력을 파일에 저장하거나 파일에서 데이터를 읽어올 때 사용된다

리눅스의 입출력 스트림(파일 디스크립터, FD)은 다음과 같이 분류된다
- `0`: 표준 입력(stdin) - 키보드 입력
- `1`: 표준 출력(stdout) - 정상 종료 메시지를 터미널 화면에 출력
- `2`: 표준 오류(stderr) - 에러 메시지를 터미널 화면에 출력

리다이렉션의 종류는 다음과 같다
- `>`: 표준 출력을 파일로 저장(덮어쓰기) `ls > output.txt`
- `>>`: 표준 출력을 파일에 추가(누적) `ls >> output.txt`
- `<`: 표준 입력을 파일에서 읽음 `sort < input.txt`
- `2>`: 표준 오류를 파일로 저장(덮어쓰기) `ls /none 2> error.log`
- `2>>`: 표준 오류를 파일에 추가(누적) `ls /none 2>> error.log`
- `&>`: 표준 출력과 오류를 파일로 저장 `command &> output.log`
- `>&`: 파일 디스크립터 재지정 `ls > output.txt 2>&1` - 표준 오류를 출력으로 지정
- `<<`: 입력 블록(here document) `cat << EOF` (여러 줄의 문자열을 입력 스트림으로 전달할 때 사용, EOF를 입력하면 입력을 종료함)

#### pipeline

파이프 `|`는 명령어의 출력을 다른 명령어의 입력으로 연결하는 기능이다

이를 통해 여러 개의 명령어를 조합하여 쉘에서 데이터를 가공하고 처리할 수 있다

기본적으로 각 명령어는 표준 입력을 받아서 표준 출력으로 결과를 출력한다

아래처럼 명령어를 사용한 다음 파이프를 선언하면 뒤의 명령어의 입력으로 출력을 연결할 수 있다

```shell
# command1 output -> command2 input, command2 output -> command3 input
$ command1 | command2 | command3
```

아래와 같이 파이프를 사용해서 프로세스 목록에서 zsh가 포함된 프로세스의 pid를 출력할 수 있다

```shell
$ ps aux | grep "zsh" | awk '{print $2}'
87834
78378
78377
76661
76660
```


## shell script



