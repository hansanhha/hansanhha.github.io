---
layout: default
title: 프롬프트를 통일시켜보자
---

#### index
- [starship](#starship)
- [components](#components)
- [format strings](#format-strings)
  - [variable](#variable)
  - [text group](#text-group)
  - [style strings](#style-strings)
- [starship.toml](#starshiptoml)


## starship

프롬프트를 커스터마이징할 수 있는 CLI 도구로 어떤 터미널이나 쉘을 사용하더라도 일관적인 프롬프트를 제공한다

설정 파일(starship.toml)에 각 언어 또는 상태(git 등)마다 표시할 정보와 스타일을 설정하고, 프롬프트에 포함할 정보를 선택하여 프롬프트 렌더링을 구성할 수 있다

기본 설정 파일 경로: `~/.config/starship.toml`

커스텀 설정 파일 경로: `STARSHIP_CONFIG` 환경 변수에 설정 파일 경로 정의


## components

모듈(module): 프롬프트에 표시할 컴포넌트 (호스트네임, 유저네임, 특정 언어, git, 클라우드 등) 

변수(variable): 특정 모듈에 포함되는 정보 (호스트네임 - 활성화 여부, 심볼, 별칭 등)

문자열
- toml 문법은 `'` `"` `'''` `"""`으로 문자열을 표시할 수 있다
- `'`: 리터럴 문자열로 취급됨
- `"`: 일반적인 문자열 표현 방식 (이스케이프 처리 필요)
- `'''`: 여러 줄을 포함(개행)하는 리터럴 문자열
- `"""`: 여러 줄을 포함(개행)하는 문자열 (이스케이프 처리 필요)


## format strings

모듈을 프롬프트에 표시하기 위해 포매팅한 문자열을 format strings라고 한다

포맷 스트링은 일반 텍스트, 텍스트 그룹, 변수 등으로 구성된다

### variable

포맷 스트링에 포함되는 변수는 "모듈"을 말한다 

`$` 심볼에 모듈의 이름을 붙이면 프롬프트에 표시할 모듈로 선택된다

변수가 아닌 문자열은 모두 텍스트로 취급된다 (공백 포함)
  
예시
- `$version`: `version` 모듈에 대한 포맷 스트링
- `$git_branch$git_commit`: `git_branch`와 `git_commit` 모듈로 이루어진 포맷 스트링
- `$git_branch $git_commit`: `git_branch`와 `git_commit` 모듈은 공백으로 분리된다

### text group

텍스트 그룹 형식: `[]()`
- `[]`: 텍스트, 변수, 내부 텍스트 그룹
- `()`: 해당 텍스트 그룹의 스타일 지정(문자열)

예시
- `'[on](red bold)'`: 굵은 빨간색 스타일의 `on` 문자열을 출력하는 텍스트 그룹
- `'[a [b](red) c](green)'`: a와 c는 초록색, b는 빨간색으로 출력하는 텍스트 그룹 (`a b c`)

### style strings

텍스트 그룹 두 번째 요소 `()`에 들어가는 문자열로, 텍스트 그룹의 텍스트를 스타일링한다

예시
- `'fg: green bg:blue'`: 텍스트 색깔을 초록색으로, 백그라운드는 빨간색으로 설정
- `'underline bg:#bf5700'`: 텍스트 언더라인, 백그라운드 색상 설정


## starship.toml

```toml
 format ='''
$git_branch$git_status$git_state
$directory>>
'''

[username]

format = '$user'
show_always = true


[hostname]

format = '$hostname'
ssh_only = false
aliases = {'MacBookAir' = 'local'}


[directory]

format = '[$path]($style) [$read_only]($read_only_style)'
style = 'white'
read_only = 'read_only'
home_symbol = 'home'
truncation_symbol='.../'


[git_branch]


format = 'branch:[$branch(:$remote_branch)]($style) '
symbol = ''
style = 'white'


[git_state]

format = '[$state( $progress_current/$progress_total)]($style)'
style = 'white'

[git_status]

format = '[$all_status$ahead_behind]($style)'
style = 'white'
conflicted = '/conflicted'
ahead = '/ahead'
behind = '/behind'
diverged = '/diverged'
up_to_date = '/up_to_date'
untracked = '/untracked'
stashed = '/stashed'
modified = '/modified'
staged = '/staged'
renamed = '/renamed'
deleted = '/deleted'
typechanged = '/typechanged'
```

