---
layout: default
title: 파인더, 웹 브라우저, AI를 내장한 터미널이 있다?! (v0.11.2)
---

#### index
- [wave](#wave)
- [core concepts](#core-concepts)
- [key bindings](#key-bindings)
  - [global keybindings](#global-keybindings)
  - [web keybindings](#web-keybindings)
  - [terminal keybindings](#terminal-keybindings)




## wave

wave는 기존의 터미널 기능에 그래픽 기능(파일 프리뷰, 웹 브라우저, AI, 시스템 자원 사용률)을 추가한 오픈소스 터미널이다 (go, typescript로 구현됨)

맥, 리눅스, 윈도우를 모두 지원한다

```shell
$ brew insatll --cask wave
```


## core concepts

tabs: 브라우저 탭, tmux의 윈도우처럼 워크스페이스를 이루는 하나의 작업 단위 (`cmd + t`로 생성할 수 있음)

blocks: 터미널, 웹 브라우저, 파일 프리뷰 등 탭을 이루는 구성 단위 (하나의 탭에 여러 블록을 만들 수 있음)

layout: 블록을 끌어서 놓고, 크기를 조절할 수 있는 레이아웃을 만들 수 있다

터미널 기능
- 표준 쉘 기능 지원 (readline 등)
- wave gui 기능과 상호작용할 수 있는 `wsh` 명령 포함
- gpu 가속

그래픽 위젯
- 파일 프리뷰
- 웹 브라우저
- AI
- 기본 시스템 모니터링 그래프

원격 연결
- ssh 연결 버튼 지원
- 윈도우 wsl 통합
- 로컬과 원격 세션에 대한 일관된 경험


## key bindings

아래는 맥 기준 키 바인딩이다

[리눅스 또는 윈도우 키 바인딩 레퍼런스](https://docs.waveterm.dev/keybindings)

키 사이에 `+`가 표시되면 첫 키를 누른 후에 두번 째에 위치한 키를 눌러야 한다

### global keybindings

워크스페이스 관련
- `cmd ctrl 1-9`: 해당 번호의 워크스페이스로 이동

윈도우 관련
- `cmd shift n`: 새 윈도우 생성

탭 관련
- `cmd t`: 새 탭 생성
- `cmd 1-9`: 해당 번호의 탭으로 이동
- `cmd [`: 왼쪽 탭으로 이동
- `cmd ]`: 오른쪽 탭으로 이동

블록 관련
- `cmd n`: 새 블록 생성 (기본값: 현재 연결과 워킹 디렉토리를 기반으로 한 터미널)
- `cmd d`: 오른쪽에 새 블록 생성
- `cmd shift d`: 아래에 새 블록 생성
- `ctrl shift s + 방향키`: 방향키의 방향으로 새 블록 생성
- `cmd m`: 현재 블록 확대/확대 취소
- `ctrl shift`: 블록 숫자 확인
- `ctrl shift 1-9`: 지정한 숫자의 블록으로 이동
- `ctrl shitf 방향키`: 방향키의 방향으로 이동

기타
- `cmd g`: 연결 스위처 열기
- `cmd ctrl r`: ui 새로고침

### web keybindings

`cmd l`: url input 포커싱

`esc`: input 포커싱 아웃

`cmd r`: 웹 페이지 리로드

`cmd <-`: 이전 (트랙패드 미인식)

`cmd ->` 이후 (트랙패드 미인식)

`cmd f`: 찾기

`cmd o`: 북마크 열기


### terminal keybindings

`ctrl shift c`: 복사

`ctrl shift v`: 붙여넣기

`ctrl f`: 찾기