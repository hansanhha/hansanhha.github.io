---
layout: default
title: 컴퓨터의 데이터를 모두 뒤져보자 (v0.60.3)
---

#### index
- [fzf](#fzf)
- [installation](#installation)
- [environment variables](#environment-variables)
- [finder](#finder)
- [search syntax](#search-syntax)
- [fuzzy completion](#fuzzy-completion)
- [usage](#usage)


## fzf

fzf는 "fuzzy finder"의 약자로 파일, 명령어 히스토리, 프로세스, git 커밋 메시지, 디렉토리 등 터미널에서 다루는 온갖 리스트를 빠르게 검색하고 선택할 수 있게 해준다


주요 특징
- 퍼지 매칭(fuzzy matching): 정확한 문자열 일치가 아닌 패턴 매칭, 부분 문자열로 검색한다
- TUI(Text User Interface): 터미널 안에서 바로 동작하는 TUI를 제공하여 마우스 없이 키보드 조작만으로 간편하게 검색할 수 있다
- 커스터마이징: 키 바인딩, 색상 테마, 프리뷰 창 등 원하는 형태로 설정 가능

fzf 없이 터미널에서 검색하고 선택하려면 아래와 같이 기본 쉘 도구와 명령어를 조합해서 비슷한 기능을 구현할 수 있다 (대신 퍼지 매칭, TUI 따위는 없음)
- 디렉토리 파일 검색: `ls <path> | grep <pattern>` 또는 `find <path> -type <type> -name <pattern>`
- 명령어 검색: `history | grep <pattern>` 또는 `Ctrl+R`
- 필터링: `awk` 또는 `sed`


## installation

### installation

```shell
# fzf 설치
$ brew install fzf


# fzf 키 바인딩 및 fuzzy 자동완성 설정 (~/.zshrc)
$ source <(fzf --zsh)
```

## environment variables

[fzf theme generator](https://vitormv.github.io/fzf-themes/)

`FZF_DEFAULT_COMMAND`
- fzf가 기본적으로 검색할 항목 목록을 생성하는 데 사용할 명령을 지정하는 환경 변수
- 기본값: `find * -type f` (인자없이 fzf를 실행하는 경우)
- 이 변수를 설정하면 fzf가 기본적으로 사용하는 검색 명령어를 원하는 대로 커스터마이징할 수 있음
- 별도의 입력이 없을 때만 적용된다 (`ls | fzf` 처럼 파이프로 입력을 주면 이 변수는 무시됨)
  
```shell
# fd를 사용해서 파일을 검색(숨김 파일 및 .git 디렉토리 제외)
$ export FZF_DEFAULT_COMMAND='fd --type f --hidden --exclude .git'
```

`FZF_DEFAULT_OPTS`
- fzf 실행 시 기본적으로 적용되는 옵션을 설정하는 환경 변수
- 설정하지 않으면 아무 옵션 없이 기본 동작(전체 화면, 기본 레이아웃 등)으로 실행된다
- 자주 사용하는 옵션을 미리 지정하기 위해 사용된다 (창 크키, 레이아웃, 프리뷰 등)

```shell
# --preview "cat {}": 선택한 항목을 오른쪽에 cat으로 미리보기
export FZF_DEFAULT_OPTS='--height 40% --layout=reverse --border --preview "cat {}"'
```

`FZF_DEFALUT_OPTS_FILE`
- 설정 파일 경로를 지정하는 환경 변수
- 설정되지 않으면 사용하지 않으며 `FZF_DEFAULT_OPTS` 값이 우선 적용된다
- 주로 `~/.fzfrc` 파일에 옵션 값을 저장하고 이 변수로 경로를 지정한다

```shell
export FZF_DEFAULT_OPTS_FILE=~/.fzfrc
```

키 바인딩 전용 옵션 및 명령 커스텀 환경 변수
- `FZF_CTRL_R_OPTS`: CTRL-R 단축키 옵션
- `FZF_CTRL_T_OPTS`: CTRL-T 단축키 옵션
- `FZF_ALT_C_OPTS`: ALT-C 단축키 옵션
- `FZF_CTRL_T_COMMAND`: CTRL-T 명령 커스텀
- `FZF_ALT_C_COMMAND`: ALT-C 명령 커스텀

### .fzfrc

```plaintext
--tmux right,50%
--border
--multi
--preview 'cat --style=numbers --color=always {}'
--bind 'ctrl-y:execute(echo {} | pbcopy)+abort'
--bind 'ctrl-d:preview-page-down'
--bind 'ctrl-u:preview-page-up'
--bind 'ctrl-a:select-all'
--bind 'ctrl-x:deselect-all'
```


## finder

### finder shortcut

키 바인딩 단축키
- `CTRL-R`: 명령어 히스토리 검색 및 붙여넣기
- `CTRL-T`: 현재 디렉토리와 그 하위 디렉토리의 파일 및 디렉토리 퍼지 검색, 붙여넣기
- `ALT-C` 또는 `ESC-C`: 현재 디렉토리와 하위 디렉토리 퍼지 검색 및 이동

`CTRL-K`: 커서 위로 이동

`CTRL-J`: 커서 아래로 이동

`Enter`: 아이템 선택

`CTRL-C` `CTRL-G` `ESC`: finder 종료

마우스 스크롤, 클릭, 더블 클릭 지원

### finder display mode

옵션을 통해 finder 표시 형식을 지정할 수 있다

`--height mode`
- `--height HEIGHT[%]` (finder height 크기 조정)
- `fzf --height 40%` `fzf --height -3` (음수도 가능)

`--layout` `--border`
- 레이아웃 선택 및 테두리 표시 `fzf --layout reverse --border`

`--tmux mode`
- tmux 팝업 안에 finder를 띄운다
- `fzf --tmux center` `fzf --tmux 40%` `fzf --tmux right, 40%`


## search syntax

별다른 옵션을 지정하지 않고 fzf를 시작하면 `extended-search mode`로 동작한다

이 모드는 여러 검색어를 공백으로 구분하여 검색할 수 있다

[fzf github 참고](https://github.com/junegunn/fzf?tab=readme-ov-file#search-syntax)

|Token|Match type|Description|
|----|---|---|
|sbtrkt|fuzzy-match|Items that match sbtrkt|
|'wild|exact-match (quoted)|Items that include wild|
|'wild'|exact-boundary-match (quoted both ends)|Items that include |wild at word boundaries|
|^music|prefix-exact-match|Items that start with music|
|.mp3$|suffix-exact-match|Items that end with .mp3|
|!fire|inverse-exact-match|Items that do not include fire|
|!^music|inverse-prefix-exact-match	Items that do not start with music|
|!.mp3$|inverse-suffix-exact-match|Items that do not end with .mp3|

`|`: or 연산자로 동작한다

```shell
# core로 시작하면서 go, rb, py로 끝나는 엔트리 매칭
$ ^core go$ | rb$ | py$
```


## fuzzy completion

퍼지 자동완성 기능은 fzf를 사용하여 특정 디렉토리나 파일을 선택하고 바로 명령을 실행한다

형식: `COMMAND [DIRECTORY/][FUZZY_PATTERN]**<TAB>`

```shell
# 현재 디렉토리를 기준으로 퍼지 검색을 하고 vim으로 연다
$ vim **<TAB>

# 현재 디렉토리를 기준으로 퍼지 검색을 하고 경로를 이동한다
cd **<TAB>
```

## usage 


```shell
# fzf 명령은 현재 디렉토리 하위에 있는 모든 파일 시스템을 조회한다
# 명령 치환을 통해 선택한 파일을 vim으로 연다
vim $(fzf)
```