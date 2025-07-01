---
layout: default 
title:
---

#### index
- [Git Limitations for Binary Files](#git-limitations-for-binary-files)
- [Git LFS](#git-lfs)
- [Usage](#usage)
- [Git LFS Limitations](#git-lfs-limitations)


## Git Limitations for Binary Files

git은 분산 버전 관리 시스템으로 텍스트 기반 소스 코드 관리에 최적화되어 있다 

애초에 리누스가 git을 만들었을 때 리눅스 커널 개발을 위해 가볍고 빠른 버전 관리 도구로써 사용하려는 목적이었으니 이미지나 비디오 등의 바이너리 파일을 염두에 두지 않았을 것이다

텍스트 파일은 변경된 부분만 델타로 저장할 수 있지만 바이너리 파일은 작은 변경에도 전체 파일을 새로 저장해야 한다

모든 커밋에서 파일의 전체 스냅샷을 저장하는 git의 특징으로 인해 대용량 파일을 추가/수정할 때마다 새 버전으로 
업데이트해야 하므로 리포지토리의 크기가 급격히 커진다

e.g 100MB 파일의 이미지 픽셀을 10번 수정(커밋)하면 리포지토리에 1GB가 추가된다 (압축 효율에 따라 달라짐)

git은 파일 내용을 메모리에 로드해 델타 압축(xdelta)을 수행하는데, 대용량 파일은 메모리 사용량과 처리 시간을 크게 늘린다 

거기에다가 원격 리포지토리에 push/fetch 작업을 할 때마다 대용량 파일로 인해 많은 네트워크 리소스를 잡아먹는다


## Git LFS

Git LFS (Large File System)는 Git의 기본 구조가 대용량 파일 처리에 적합하지 않은 문제를 해결하기 위한 오픈 소스 확장 도구이다

2015년에 개발이 시작됐으며 GitHub, GitLab, Bitbucket 등 주요 Git 호스팅 플랫폼에서 지원된다

대용량 파일(이미지, 비디오, 데이터셋, 바이너리 파일 등)을 Git처럼 버전 관리를 할 수 있으며 LFS 서버로 분리하여 Git 리포지토리의 크기를 작게 유지한다

fetch, push 등의 작업 시 Git 리포지토리에 기록된 포인터만 다운로드하고 필요한 바이너리 파일만 체크아웃 시 LFS 서버로부터 가져온다

추가 명령어 없이 기존 Git의 워크플로우를 유지하며 LFS를 사용할 수 있다

### Use Case

비디오, 고해상도 이미지를 사용하는 미디어 프로젝트

대용량 데이터셋(CSV, HDF5 등)을 사용하는 데이터 과학

수많은 리소스 파일(텍스처, 3D 모델, 사운드 파일 등)을 사용하는 게임 개발

### Workflow

#### Text Pointer File

대용량 파일을 Git 저장소에 직접 저장하는 대신 작은 텍스트 파일(포인터)을 저장한다

이 포인터는 파일의 메타데이터(해시, 크기 등)와 실제 파일이 저장된 LFS 서버의 위치를 가리킨다

#### LFS Repository

실제 대용량 파일은 Git 저장소와 분리된 별도의 LFS 서버에 업로드된다

사용자가 커밋을 체크아웃할 때 Git LFS가 필요한 파일을 LFS 서버에서 자동으로 다운로드하여 워킹 디렉토리에 배치한다 (lazy loading)


## Usage

#### git lfs installation

git lfs는 git에 내장되어 있지 않고 별도로 설치해야 한다

[installation guide](https://github.com/git-lfs/git-lfs/wiki/Installation)

```shell
# homebrew formula 다운로드
$ brew install git-lfs

# git lfs 로컬 활성화
$ git lfs install
```

#### configure track file

```shell
# 특정 파일 유형을 LFS로 관리하도록 지정 (.gitattributes 파일에 기록됨)
git lfs track "*.mp4"
```

이후 커밋하고 원격 리포지토리에 푸시하면 LFS가 자동으로 대용량 파일을 처리한다

LFS는 내부적으로 Git의 clean과 smudge 필터를 활용한다

Clean: 커밋 시 대용량 파일을 LFS 서버에 업로드하고 포인터로 대체한다

Smudge: 체크아웃 시 포인터를 읽어 실제 파일을 다운로드한다

#### commit

```shell
# 파일 추적 설정으로 인해 변경된 git 속성 파일 추가
$ git add .gitattributes
$ git add video.mp4

# Git 리포지토리에는 포인터만 저장되고 실제 파일은 LFS 서버에 저장된다
$ git commit -m "add video with lfs"
$ git push
```


## Git LFS Limitations

Git LFS를 설치하고 추적할 파일을 `git lfs track`으로 지정해야 한다

팀원 모두가 LFS를 설치하지 않으면 포인터만 보인다

기존 저장소에 LFS를 적용하려면 `git lfs migrate`로 히스토리를 재작성해야 한다 -> 커밋 해시가 바뀌어 협업에 영향을 줄 수 있음

LFS 다운로드 속도가 네트워크에 의존하여 대용량 파일이 많을 경우 여전히 지연 발생될 수 있음

또한 LFS 서버가 다운되거나 접근 불가 시 파일을 가져올 수 없다 -> Git의 분산 특성 약화

Github의 경우 일정 크기의 저장 공간을 무료로 제공하지만 그 이상의 공간과 대역폭은 추가 요금이 발생한다

바이너리 파일의 병합 충돌은 Git처럼 자동 해결할 수 없다

LFS로 해결되지 않는 경우 S3, Dropbox와 같은 외부 저장소와 결합한다