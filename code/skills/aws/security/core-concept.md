---
layout: default
title:
---

#### index
- [aws credentials](#aws-credentials)
- [aws credentials management](#aws-credentials-management)
- [authentication/authorization services](#authenticationauthorization-services)
- [authentication/authorization workflow](#authenticationauthorization-workflow)


## aws credentials

자격 증명은 사용자가 시스템에 자신을 인증하고 특정 작업을 수행할 권한을 증명하는 데 사용되는 정보로, identity(사용자 식별 정보)와 permissions(수행할 수 있는 작업 범위)를 연결한다

aws에서는 생성 방식과 사용 목적에 따라 자격 증명의 유형을 다음과 같이 구분한다

### long-term credentials

장기 자격 증명은 만료되지 않는 자격 증명으로, 주로 root user 및 IAM user에 연결된다

Access Key ID와 Secret Access Key로 구성되며 aws cli, sdk에서 사용할 수 있다

console, cli, sdk에서 새 자격 증명을 발급할 수 있다 (rotation)

### temporary credentials

aws sts(security token service)에서 발급하는 자격 증명으로 유효 기간이 제한되며 자동 갱신을 지원한다

Access Key ID, Secret Access Key, Session Token(임시 자격 증명을 식별하는 추가 토큰), Expiration(기본 1시간, 최대 12시간)으로 구성된다

역할 전환, sso, 모바일/외부 사용자 인증의 용도로 사용되며 다음과 같은 방법으로 발급할 수 있다
- `aws sts assume-role`: IAM 역할 전환
- `aws sts get-session-token`: MFA 기반 임시 자격 증명
- `aws configure sso`: sso 로그인

### root user credentials

aws 계정 생성 시 제공되는 기본 자격 증명

기본적으로 이메일 주소와 암호로 구성되며 MFA(OTP, 하드웨어 토큰) 다중 인증을 구성할 수 있다

모든 권한을 가지는 최상위 계정으로 계정 수준 작업(결제 정보 변경, MFA 설정 등)의 용도로 사용된다

보안 이슈를 방지하기 위해 최소한으로 사용하고 IAM 사용자를 생성한 뒤 대체하는 방식을 권장한다

### IAM user credentials

root user로부터 생성된 계정의 자격 증명

기본적으로 username만 가지며 root user가 필요에 따라 콘솔 로그인을 활성화하여 비밀번호를 생성하거나 프로그래밍 액세스를 할 수 있도록 액세스 키를 만들 수 있다

또한 sts를 통해 임시 자격 증명을 생성하거나 MFA 다중 인증을 구성할 수 있다

### sso credentials

IAM identity center를 통해 발급된 토큰 기반 자격 증명

sso 포털 로그인 -> 토큰 캐시 -> sts 호출 흐름을 통해 임시 자격 증명을 생성할 수 있다

인증 방식을 중앙화하여 다중 계정 및 애플리케이션에 접근할 수 있으며 IAM identity center 디렉토리를 사용하거나 외부 idp(okta, azure ad 등)와 통합할 수 있다

### ec2 instance role credentials

ec2 인스턴스에 부여된 IAM 역할을 기반으로 인스턴스 메타데이터 서비스(IMDS)에서 발급하는 임시 자격 증명

ec2 내 애플리케이션에서 aws 리소스에 접근할 때 사용되며 sts를 통해 주기적으로 갱신된다


## aws credentials management

### credentials creation

IAM user: IAM 콘솔에서 사용자 생성 -> 자격 증명 발급

sts: cli/sdk로 임시 자격 증명 요청

sso: IAM identity center 설정 후 로그인

### save credentials 

aws cli
- `~/.aws/credentials`: 장기 자격 증명 저장
- `~/.aws/config`: sso 프로파일 및 설정 저장
- `~/.aws/sso/cache`: sso 토큰 캐시

sdk
- 환경 변수(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
- 코드 내 지정

### auto management

ec2 역할: IMDS (instance metadata service)에서 임시 자격 증명 동적 제공

aws lambda: 실행 역할로 자동 주입


## authentication/authorization services

aws interface(console, cli, sdk 등)에서 aws 서비스 또는 리소스에 접근하려면 적절한 권한을 가진 자격 증명을 aws 백엔드에게 전송한 뒤 검증받아야 한다

이 과정에서 인증/인가 단계를 모두 거치는데, 모든 인터페이스에서 공통적으로 적용될 수 있는 구성 요소와 동작 과정을 알아보자

IAM (identity and access management)
- 사용자, 그룹, 역할(role)에 대한 자격 증명과 권한 정책을 설정하는 서비스
- 자격 증명 발급, 정책 평가 기반 제공

STS (security token service)
- 임시 자격 증명(temporary credentials)을 발급하는 서비스
- SSO, MFA, 역할 전환(AssumeRole) 등에서 사용

SigV4 (signature version 4)
- 요청의 인증 및 무결성을 보장하는 서명
- 모든 API의 요청에 서명을 적용한다

aws backend authentication system
- 자격 증명의 유효성을 확인하고 정책을 평가하는 aws 시스템
- 모든 요청에 대해 인증/인가 처리를 수행한다

service endpoint
- 요청을 수신하고 처리하는 각 aws 서비스(s3, ec2 등)의 진입점
- 자격 증명 검증 및 작업 실행


## authentication/authorization workflow

### 1. credentials acquisition

aws 서비스를 사용하기 위해 자격 증명을 획득 과정으로 공통적으로 적용되는 점은 다음과 같다
- 자격 증명 정보는 root/IAM user, 역할 또는 sso를 기반으로 생성한다 
- 장기 자격 증명(액세스 키) 또는 단기 자격 증명(sts 발급) 방식을 사용하여 자격 증명을 유지한다

#### aws management console

normal login
- root user 또는 iam user의 사용자 이름과 암호를 입력해 로그인한다
- MFA 다중 인증(OTP, 하드웨어 토큰)을 선택적으로 추가할 수 있다
- 콘솔 URl에 직접 접근하는 방식 `https://signin.aws.amazon.com/signin?redirect_uri=https%3A%2F%2Fap-northeast-2.console.aws.amazon.com%2F&code_challenge_method=SHA-256`

sso (IAM identity center)
- IAM identity center에서 제공하는 sso 포털(`https://ap-northeast-2.signin.aws/platform/login`)에 접근하여 로그인한다 
- 외부 idp(okta, azure ad 등) 또는 IAM identity center 디렉토리를 사용한다

#### aws cli

normal login (`aws configure`) 
- IAM 사용자 액세스 키(access key id, secret)를 설정한다
- `aws sts get-session-token` 명령어를 통해 MFA 기반 임시 자격 증명을 수동으로 획득할 수 있다

sso (`aws configure sso`)
- sso 설정 후 브라우저에서 로그인한다
- sso 포털 로그인 -> 토큰 캐시 -> sts 임시 자격 증명 발급
- `~/.aws/config`에 프로파일을 저장한다

#### aws sdk

normal login
- 코드 내에서 액세스 키를 직접 지정하거나 환경 변수로 설정한다
- sdk의 `STS.get_session_token()` 메서드 호출로 MFA 기반 임시 자격 증명을 생성할 수 있다

sso 
- sdk에서 지원하는 sso 로그인 방식을 사용한다
- cli와 유사하게 sso 토큰을 캐시에서 가져오거나 갱신하는 방식으로 동작한다

### 2. aws api request creation

각 인터페이스는 사용자의 작업을 aws api 호출로 변환한다

console ui 클릭, cli 명령어, sdk 메서드 -> api 요청

api 요청에는 다음과 같은 정보가 포함된다
- http 메서드: GET, POST 등
- 엔드포인트: e.g) `https://s3.ap-northeast-2.amazonaws.com`
- 자격 증명 정보: 액세스 키/세션 토큰(임시 자격 증명)

### 3. request signing

인터페이스에서 발생한 모든 요청은 무결성과 발신자 인증을 보장하기 위해 SigV4(aws signature version 4)를 통해 서명되며 그 과정은 다음과 같다

클라이언트 측에서 다음과 같은 과정을 통해 서명을 생성한다

#### 1. canonical request 생성 

canonical request: 요청의 표준화된 문자열(메서드, 경로, 쿼리, 헤더 등)

```text
GET
/my-bucket
Action=ListBuckets
host:s3.us-west-2.amazonaws.com
x-amz-date:20250313T120000Z
```

#### 2. SHA254으로 해시 생성

#### 3. string to sign 생성

string to sign: canonical request의 해시, 날짜, credential scope 등

```text
AWS4-HMAC-SHA256
20250313T120000Z
20250313/us-west-2/s3/aws4_request
<canonical request 해시>
```

#### 4. 자격 증명을 통해 HMAC-SHA256 계산

SecretAccessKey 또는 sts 토큰을 통해 HMAC-SHA256 계산을 여러 단계로 수행하여 서명을 생성한다

`Signing Key = HMAC(HMAC(HMAC(HMAC("AWS4" + SecretAccessKey, Date), Region), Service), "aws4_request")`

`Signature = HMAC(Signing Key, String to Sign)`

생성된 서명(AccessKeyID 포함)은 요청의 헤더에 포함된다

```text
Authorization: AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20250313/ap-northeast-2/s3/aws4_request, SignedHeaders=host;x-amz-date, Signature=fe5f80...
```

### 4. request sending

https를 통해 서명된 요청을 해당 aws 서비스 엔드포인트로 전송한다

엔드포인트는 리전별로 다른데, 만약 서울의 s3 서비스에 요청을 보낸 경우 `s3.ap-northeast-2`가 대상 엔드포인트로 설정된다

### 5. authentication

aws 백엔드 인증 시스템이 요청을 수신하고 다음을 확인한다
- 자격 증명 유효성: 액세스 키 또는 sts 토큰이 유효하거나 만료되지 않았는지 검증한다
- 서명 검증: 요청에 포함된 서명이 secret key로 재계산된 값과 일치하는지 확인한다

상세 과정
- 요청 수신
- canonical request 재구성: 요청 데이터를 클라이언트와 동일한 방식으로 표준화
- string to sign 재구성: 날짜, 리전, 서비스 정보를 포함해 동일한 문자열 생성
- signing key 재계산: aws는 AccesskeyId를 통해 내부적으로 SecretAccessKey를 조회하고 동일한 HMAC-SHA256 과정을 반복해서 singingkey를 생성한다
- signature 재계산: 재구성된 string to sign과 signingkey로 서명을 계산한다
- 비교: 요청에 포함된 서명과 재계산된 서명이 일치하는지 확인한 뒤 동일하면 인가 단계로 진행하고, 아니라면 SignatureDoesNotMatch 오류를 반환한다

사용되는 보안 메커니즘
- 시간 검증: `x-amz-date` 헤더를 통해 요청 시간이 허용 범위(기본 15분) 이내인지 확인한다
- 무결성 보장: 요청 데이터가 변조되면 canonical request가 달라져 서명이 일치하지 않는다
- SecretAccessKey 보호: 네트워크로 전송되지 않고, aws 백엔드만 알고 있다

### 6. authorization

인증이 완료되면 aws 서비스는 IAM 정책을 평가하여 작업을 허용할지 결정한다

1. 요청의 주체(principal) 확인: IAM 사용자, 역할, sso
2. 정책 평가: IAM 정책(사용자/역할에 연결된 정책), 리소스 정책(s3 버킷 정책 등), aws organization scp(service control policy, optional)
3. 작업 허용 여부 결정: 요청된 작업이 정책에 명시된 권한과 일치하면 작업을 실행한 후 응답을 반환하고, 아니라면 AccessDenied 오류를 반환한다

```json
{
  "Effect": "Allow",
  "Action": "s3:ListBucket",
  "Resource": "arn:aws:s3:::my-bucket"
}
```

### 7. response receiving

aws 서비스가 작업을 수행한 결과를 인터페이스에게 반환한다

console: 웹 ui에 결과 표시

cli: 터미널에 json/text 출력

sdk: 프로그래밍 언어 객체로 반환
