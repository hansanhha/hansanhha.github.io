---
layout: default
title:
---


#### index
- [aws cli](#aws-cli)
- [aws cli version 2](#aws-cli-version-2)
- [installation](#installation)
- [configuration (credentials/config)](#configuration-credentialsconfig)
- [aws cli workflow](#aws-cli-workflow)
- [account/profile commands](#accountprofile-commands)
- [ec2 commands](#ec2-commands)

#### references
- [aws cli docs](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
- [aws cli blog posts](https://aws.amazon.com/blogs/developer/category/programing-language/aws-cli/)
- [aws cli reference](https://awscli.amazonaws.com/v2/documentation/api/latest/index.html)


## aws cli

aws cli(amazon web service command line interface)는 웹 브라우저 기반 aws 관리 콘솔 기능을 로컬 터미널에서 명령어를 통해 동일하게 사용할 수 있도록 하는 오픈 소스 커맨드 라인 툴이다

리눅스 또는 맥에서 동작하는 bash, zsh, tcsh나 윈도우의 command prompt, powershell 뿐만 아니라 putty, ssh 또는 aws system maanger를 통해 aws ec2 인스턴스에 접속한 원격 터미널에서 사용할 수 있다

aws cloud에서 제공하는 모든 iaas (infrastructure as a service) 관리/운영 기능은 아래와 같은 도구들을 통해 이용할 수 있으며 cli는 그 중 한가지 방법으로 터미널에서 접근할 수 있는 기능을 제공한다

새로 출시되는 iaas 기능 및 서비스에 대해 aws는 api와 cli를 통한 관리 기능을 즉시 또는 180일 이내에 제공한다

```text
                                       |=============|
aws console  ----------------------->  |             |
                                       |             |
aws cli ---------------------------->  | aws service |
                                       |             |
aws sdk(aws api) ------------------->  |             |
                                       |=============|
```


## aws cli version 2

가장 aws cli 최신 버전, 버전 1과 호환되지 않는 브레이킹 체인지가 포함되어 있다

참고로 aws cli 버전 2는 aws 공식 페이지에서 제공하는 번들 인스톨러로만 설치가 가능하다 (다른 패키지 매니저에서 제공하는 aws cli는 aws에서 관리하지 않는 비공식 cli임)

아래의 명령어를 통해 로컬에 설치된 aws 버전을 확인할 수 있다

```shell
aws --version
```


## installation

### 맥os 설치

#### global 설치

아래의 명령어들로 aws cli를 설치할 수 있다

`-o "AWSCLIV2.pkg"` 옵션은 curl을 통해 다운로드된 파일의 이름을 지정하는 옵션이다

이후 macos에서 제공하는 installer 프로그램을 통해 다운로드된 .pkg 파일을 통해 aws cli를 루트 디렉토리 하위에 설치한다

파일들은 모두 `/aws/local/aws-cli` 디렉토리에 설치되며 자동적으로 `/usr/local/bin` 디렉토리에 심볼릭 링크가 생성된다

```shell
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg AWSCLIV2.pkg -target /
```

#### 현재 유저에만 설치

[참고](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html#getting-started-install-instructions)


## configuration - credentials/config

[aws의 인증/인가 검증](../security/core concept#authenticationauthorization-workflo) 과정을 통과해야 aws cli에서 aws 리소스 및 서비스에 접근할 수 있으므로 로컬 환경에 적절한 자격 증명을 구성할 필요가 있다

aws cli은 한 개의 자격 증명만 설정할 수 있도록 제한하지 않고 여러 개의 자격 증명과 기타 정보를 설정할 수 있는데, 이를 위해 profile이란 개념을 사용한다

profile이란 credentials와 config 파일에 정의된 설정 집합으로 여러 프로파일을 만들어 다양한 aws 계정이나 IAM role에 접근할 수 있게 한다

즉, aws cli는 credentials/config 파일을 통해 자격 증명과 구성 정보를 설정하고 파일의 설정 모음(섹션)들은 profile 단위로 구분된다

credentials 파일
- aws 액세스 키 및 시크릿 키같은 자격 증명을 저장하는 파일
- 리눅스/맥: `~/.aws/credentials`
- 윈도우: `%USERPROFILE%\.aws\credentials`

config 파일
- 프로파일 별 설정 정보(리전, 출력 형식 등)를 저장하는 파일
- 리눅스/맥: `~/.aws/config`
- 윈도우: `%USERPROFILE%\.aws\config`

### format of the config/credentials file

config/credentials 파일의 내용은 여러 프로파일과 섹션으로 구성되며 profile 이외에도 sso-session, service으로 섹션을 나눌 수 있다


각 파일은 텍스트 파일로 아래의 형식을 차용한다

- 섹션 이름은 대괄호로 감싸서 표시한다 `[default]` `[profile user1]` `[sso-session sso-1]`
- 섹션의 모든 엔트리는 `NAME=VALUE` 형식을 사용하며 등호 사이의 띄워쓰기를 허용한다
- 라인의 맨 앞에 `#` 캐릭터를 사용해서 주석 처리를 할 수 있다

```text
[default]
region = ap-northeast-2
output = json
```


### format of the profile configuration

config 파일의 profile 구성

```text
[default]
...

[profile user1]
...
```

credentials 파일의 profile 구성

```text
[default]
...

[user1]
...
```

crendentials 파일의 profile은 config 파일과 달리 명시적으로 "profile" 단어를 앞에 사용하지 않고 profile name만 대괄호로 감싼다


각 프로파일은 서로 다른 자격 증명과 aws 리전, 출력 형식을 지정할 수 있다

### configuring using aws cli commands 

aws cli 명령어를 사용하여 profile을 설정할 수 있다
- IAM identity center sso
- 단기 자격 증명
- 장기 자격 증명
- IAM role
- ec2 instance metadata 자격 증명

#### long-term credentials configuration

`aws configure` 명령어는 장기 자격 증명(액세스 키/비밀 키)와 기본 리전 및 출력 형식을 입력받아서 기본 프로파일(default)을 설정한다 


```shell
$ aws configure

AWS Access Key ID [None]: account_access_key_id
AWS Secret Access Key [None]: account_secret_access_key 
Default region name [None]: ap-northeast-2
Default output format [None]: json
```

```shell
$ cat ~/.aws/credentials 

[default]
aws_access_key_id = ******
aws_secret_access_key = ****** 
```

```shell
$ cat ~/.aws/config                               

[default]
region = ap-northeast-2
output = json
```

#### short-term credentials configuration

아래의 명령어로 sts(security token service)로부터 임시 자격 증명을 발급받는다

```shell
aws sts get-session-token --duration-seconds 3600
```

```json
{
  "Credentials": {
    "AccessKeyId": "******",
    "SecretAccessKey": "******",
    "SessionToken": "******",
    "Expiration": "2025-03-015T19:00:00Z"
  }
}
```

발급받은 임시 자격 증명을 환경 변수로 설정하거나 cli 명령을 통해 세션 토큰만 설정하여 사용할 수 있다

```shell
$ aws configure set aws_access_token ${SessionToken}
```

위와 같이 cli 명령을 사용하면 세션 토큰이 credentials 파일에 추가된다


```shell
export AWS_ACCESS_KEY_ID=AccessKeyId
export AWS_SECRET_ACCESS_KEY=SecretAcessKey
export AWS_SESSION_TOKEN=SessionToken
```


## aws cli workflow

aws cli 명령어를 사용해서 aws 서비스와 상호작용할 때 다음과 같은 처리 수행 과정을 거친다

### 1. find credentials

aws cli는 아래의 순서대로 자격 증명을 찾는다
- 환경 변수(`AWS_ACCESS_KEY_ID` `AWS_SECRET_ACCESS_KEY` `AWS_SESSION_TOKEN` (필요한 경우))
- credentials 파일
- config 파일
- ec2 instance metadata (필요한 경우)

### 2. aws api request creation/signing

사용자가 입력한 명령어에 해당하는 aws api 호출로 변환한다 (`aws s3 ls` -> `ListBuckets`)

요청의 무결성과 발신자의 신원 보장을 위해 aws cli가 획득한 자격 증명을 사용해 전자 서명을 추가한다

### 3. authentication/authorization

서명된 요청을 https를 통해 aws 서비스의 엔드포인트로 보내면 요청의 서명을 검증하고, IAM policy를 평가하여 요청된 작업 수행 여부를 결정한다

### 4. process task and send response

aws 서비스가 작업을 처리하고 그 결과를 aws cli로 반환한다

aws cli는 요청을 보낸 프로파일에 설정되어 있는 형식(json 등)으로 변환하여 출력한다


## account/profile commands

`aws sts get-caller-identity`: 현재 사용 중인 계정 id, 사용자 arn 확인

`aws iam list-users`: 현재 계정의 모든 IAM 유저 목록 확인

`aws iam create-user`: 새로운 IAM 유저 생성

`aws iam create-role --role-name MyRole --assume-role-policy-document file://trust-policy.json`: 새로운 IAM role 생성

trust-policy.json

```json
{
  "Version": "2012-10-17",
  "Statement": {
    "Effect": "Allow",
    "Principal": {"Service": "ec2.amazonaws.com"},
    "Action": "sts:AssumeRole"
  }
}
```


`aws iam attach-user-policy --user-name MyIAMUser --policy-arn arn:aws:iam::aws:policy/AmazonEC2FullAccess`: IAM 유저에게 policy 연결

`aws iam list-roles`: 현재 계정의 모든 IAM role 목록 확인

`aws sts assume-role --role-arn arn:aws:iam::123456789012:role/MyRole --role-session-name MySession`: IAM role을 가정하여 임시 자격 증명 발급

`aws sts get-session-token --serial-number arn:aws:iam::123456789012:mfa/MyUser --token-code 123456`: MFA 기반 임시 자격 증명 발급
  

## ec2 commands 

`aws ec2 describe-instances`: 모든 ec2 인스턴스 상태 확인

`aws ec2 run-instances --image-id ami-0abcdef1234567890 --instance-type t2.micro --key-name MyKeyPair --security-group-ids sg-12345678 --subnet-id subnet-12345678`: 새로운 ec2 인스턴스 생성

`aws ec2 start-instances --instance-ids i-1234567890abcdef0`: 인스턴스 시작

`aws ec2 stop-instances --instance-ids i-1234567890abcdef0`: 인스턴스 중지

`aws ec2 terminate-instances --instance-ids i-1234567890abcdef0`: 인스턴스 종료

`aws ec2 describe-instance-status --instance-ids i-1234567890abcdef0`: 인스턴스 상태와 시스템/인스턴스 점검 결과 확인

`aws ec2 get-console-output --instance-id i-1234567890abcdef0`: 인스턴스 콘솔 출력 확인



``

