---
layout: default
title:
---

#### index
- [IAM](#iam)
- [IAM user](#iam-user)
- [user group](#user-group)
- [role](#role)
- [policy](#policy)
- [IAM identity center](#iam-identity-center)
- [security token service](#security-token-service)


## IAM

IAM(Identity and Access Management) 서비스는 aws 리소스에 대한 액세스를 안전하게 관리할 수 있도록 설계되었다

IAM을 통해 사용자, 그룹, 역할, 정책을 생성하고 관리하여 aws 환경 내에서 누가 어떤 리소스에 어떤 조건으로 접근할 수 있는지를 제어한다

### IAM main purpose

인증: aws 리소스에 접근하려는 사용자 또는 서비스의 identity를 확인한다

인가: 인증된 사용자나 서비스가 특정 리소스에서 수행할 수 있는 작업을 정의하고 제어한다

보안 강화: 최소 권한 원칙(least privilege)을 적용하여 필요한 권한만 부여함으로써 보안을 강화한다

중앙 집중식 관리: aws 계정 내 모든 리소스와 서비스에 대한 접근 권한을 하나의 서비스에서 통합으로 관리한다

### IAM main features

세밀한 액세스 제어: 특정 리소스나 작업 단위로 권한을 정의할 수 있어 상세한 제어가 가능하다 - e.g) 특정 s3 버킷에 대한 읽기만 허용

임시 보안 자격 증명: [aws sts](#security-token-service)를 통해 일정 시간 동안만 유효한 자격 증명을 발급한다 - e.g) 모바일 앱에서 s3 업로드를 위한 단기 접근

다중 인증(MFA): 사용자 인증 시 OTP, 하드웨어 토큰 등의 추가 보안 계층을 요구하여 계정 보안을 강화한다

다른 aws 서비스와의 통합: ec2, s3, lambda 등 대부분 aws 서비스와 연동되어 중앙에서 권한 관리를 할 수 있다 - e.g) IAM 역할을 통해 lambda 함수가 dynamodb에 접근

보안 감사 및 규제 준수 확인: aws cloudtail과 연계하여 접근 기록을 남기고 추적할 수 있다

### how IAM works

1. 사용자 또는 애플리케이션이 aws 리소스에 접근 요청
2. IAM이 요청자의 자격 증명 확인
3. 자격 증명과 연결된 권한 정책을 평가하여 작업 허용 여부 결정
4. 허용 시 작업 실행, 그렇지 않으면 Access Denied 반환

모든 작업은 기본적으로 거부되며, 권한 정책에서 명시적으로 허용된 경우에만 실행된다


## IAM user

IAM user는 aws 계정 내에서 만들 수 있는 또 다른 계정이자 특정 권한을 가진 엔터티로 사람(개발자/관리자)이나 애플리케이션이 aws 서비스에 접근할 때 사용된다

루트 사용자와 달리 제한된 권한을 가지며 최소 권한 원칙(least privilege)을 준수한다

자격 증명 유형
- 콘솔 로그인: aws management console에 로그인할 때 필요한 사용자 이름과 암호
- 액세스 키: aws cli, sdk를 통해 프로그래밍 방식으로 접근할 때 사용하는 액세스 키 id와 비밀 액세스 키

### create IAM user

루트 사용자로 aws console에 로그인한 후 IAM 서비스에 들어가서 좌측의 users 메뉴를 클릭하고, create user 버튼을 클릭한다

![iam user create user](./assets/iam%20user%20create%20user%201.png)

사용자 생성은 3가지 단계로 이루어진다
- step 1: IAM 사용자의 username 설정
- step 2: IAM 사용자의 권한 설정
- step 3: 생성 정보 요약 및 사용자 생성

step 2에서 아래와 같이 세 가지 옵션을 통해 권한을 설정할 수 있다

![iam user create user permission options](./assets/iam%20user%20create%20user%202.png)

Add user to group: [그룹](#user-group)에 사용자를 추가하는 방식(해당 그룹에 설정된 모든 권한 정책이 사용자에게 적용된다)

Copy permissions: 기존 IAM user의 모든 권한 설정을 그대로 복사하여 적용하는 방식

Attach policies directly: aws에서 제공하는 모든 권한 중 필요한 권한을 선택하여 설정하는 방식

### configure credentials

생성된 사용자는 기본적으로 username으로만 구성되며 자격 증명 설정을 통해 비밀번호를 생성하거나 액세스 키 발급 등을 할 수 있다

아래와 같이 자격 증명을 설정할 사용자를 클릭하고 security credentials 탭에 들어가면 설정 가능한 옵션들이 표시되며 주요 옵션은 다음과 같다

![iam user security credentials](./assets/iam%20user%20create%20user%203.png)

#### Console sign-in

IAM user를 aws management console을 통해 로그인할 수 있도록 비밀번호를 생성하는 설정으로 자동/수동 생성 옵션을 지원한다

암호 생성 시 console sign-in url, username, password 정보를 제공하며 csv 파일로 다운로드받을 수도 있다

#### Multi-factor authentication (MFA)

계정 로그인 시 추가적으로 진행해야 하는 인증 과정을 설정할 수 있는 옵션으로 패스키(보안키), authenticator 앱, 하드웨어 TOTP 토큰 옵션이 제공된다

두 개 이상의 MFA 다중 인증 설정을 지원한다

#### Access keys

프로그래밍 방식(aws cli, aws sdk)으로 aws 서비스에 접근할 때 사용하는 액세스 키를 발급하는 설정

발급 시 Access Key와 Secret Access Key를 제공하며, 키의 활성 상태(active/inactive)와 상관없이 계정 당(IAM user) 최대 2개까지만 발급할 수 있다

#### X.509 Signing certificates

일부 aws 서비스에 안전한 SOAP 프로토콜 요청을 보낼 수 있도록 인증서를 발급하는 설정

액세스 키와 마찬가지로 활성 상태와 상관없이 계정 당(IAM user) 최대 2개까지만 발급할 수 있다


## user group

user group은 여러 IAM 사용자를 하나의 논리적 단위로 묶어 권한을 공통적으로 부여하는 기능으로, 그룹에 설정된 권한 정책들이 그룹에 포함된 모든 사용자에게 적용된다

개발/관리 등 역할에 따라 그룹으로 분리한 뒤 특정 IAM user를 특정 그룹에 포함시킴으로써 동일한 역할의 사용자에게 동일한 권한을 보장할 수 있다

주의점
- 그룹 자체는 자격 증명을 가지지 않으며 로그인할 수 없다
- 그룹에 직접 연결된 권한 정책만 상속하며, [역할](#role)은 그룹에 연결할 수 없다 


## role

IAM role은 특정 엔티티(aws 서비스/계정, 웹 idp, SAML 2.0 등)가 특정 작업을 수행할 수 있는 권한을 부여받은 임시 identity를 제공하는 객체이다

IAM user와 다르게 고정된 자격 증명(액세스 키 등)이 아니라 필요할 때마다 "가정(assume)"하여 임시 자격 증명을 발급받아 사용한다

주로 aws 서비스 간 상호작용이나 외부 엔티티의 접근을 위해 사용된다

### 특징

| 구분    | IAM role           | IAM user      |
|-------|--------------------|---------------|
| 자격 증명 | 임시(sts 발급)         | 장기(암호, 액세스 키) |
| 주체    | 서비스, 사용자, 외부 idp 등 | 사람, 애플리케이션    |
| 용도    | 동적/임시 접근           | 고정/지속적 접근     |
| 관리    | 신뢰 정책으로 제어         | 직접 정책 연결      |

IAM role은 IAM 사용자처럼 자격 증명(고정된 암호나 액세스 키)을 가지지 않는다

역할이 가정되면 aws sts(security token service)를 통해 임시 자격 증명(AccessTokenId, SecretAccessKey, SessionToken)이 발급되며 일정 시간 내에만(기본 1시간, 최대 12시간) 유효성을 가진다

다양한 [주체](#entities)가 가정하여 role을 사용할 수 있으며 필요에 따라 역할 전환을 할 수도 있다

### entities

aws service: ec2, lambda 등

aws account: 다른 계정의 사용자/서비스

web identity: aws cognito, 외부 idp

SAML 2.0 federation: sso 기반 접근

### role components

iam role은 신뢰 정책(trust policy)과 권한 정책(permissions policies)라는 두 가지 핵심 정책으로 구성된다

#### trust policy (trust entities)

누가(어떤 엔티티)가 이 역할을 사용(가정)할 수 있는지를 지정하는 json 문서

아래의 정책은 ec2 서비스가 이 역할을 사용할 수 있도록 허용한다

![iam user role trust policy](./assets/iam%20user%20role%201.png)

#### permissions policies

역할이 가정되었을 때 수행할 수 있는 작업을 지정하는 json 문서

아래의 정책은 ec2에 대한 모든 작업을 할 수 있는 권한을 가진다

![iam user role permissions policies](./assets/iam%20user%20role%202.png)

### usage

#### 1. ec2 instance

ec2 인스턴스 생성 시 역할을 연결하여 인스턴스 메타데이터 서비스(IMDS)에서 임시 자격 증명을 획득한다

#### 2. aws cli

생성된 role을 살펴보면 ARN(amazon resource name)을 제공한다

아래와 같이 `aws ts assume-role` 명령어를 통해 ARN에 해당하는 임시 자격 증명을 발급받을 수 있다

```shell
aws sts assume-role --role-arn arn:aws:iam::533267200761:role/iam-test-role --role-session-name my-session-1
```

#### 3. sso

IAM identity center에서 역할을 선택하고 임시 자격 증명으로 콘솔/cli에 접근할 수 있다

### use cases

서비스 간 접근: lambda가 dynamodb에 접근하거나, ec2에서 s3에 업로드할 때 IAM role을 사용한다

크로스 계정 접근: 계정 A의 role이 계정 B의 리소스에 접근 (trust policy에 계정 B 지정)

외부 사용자 인증: aws cognito 또는 SAML 기반 sso로 role을 가정한다

임시 권한 부여: MFA가 필요한 작업에서 임시 자격 증명을 발급한다


## policy

IAM policy는 aws 리소스에 대한 접근 권한을 json 형식으로 정의하는 문서로 사용자, 그룹, 역할과 같은 IAM identity에 연결되거나 일부 리소스(s3 버킷, kms 키 등)에 직접 연결되어 특정 작업을 허용하거나 거부하는 규칙을 설정한다

policy에 누가, 어떤 리소스에, 어떤 작업을 수행할 수 있는지 또는 금지하는지 설정하면 인가 처리 과정 중에 사용되어 요청자의 aws 리소스에 대한 접근을 허용하거나 거부한다

일반적으로 aws 차원에서 다양한 정책(AWS managed 또는 AWS managed - job function 타입)들을 제공하며, 필요에 따라 커스텀 정책을 만들 수 있다

aws에서 제공하는 정책은 여러 가지 버전을 제공한다 (json 문서 내의 Version과 별개)

### structure

IAM policy는 하나 이상의 statement(명령문)로 구성되며, 각 statement는 다음의 구성 요소를 포함한다
- Effect: 작업의 허용 여부 결정 - Allow, Deny
- Action: 허용 또는 거부할 aws 서비스의 api 작업 (`s3:GetObject`, `ec2:StartInstances`)
- Resource: 작업이 적용될 aws 리소스(s3 버킷, ec2 인스턴스), ARN 형식으로 지정한다
- Condition(optional): 정책이 적용되는 조건 (특정 ip 주소에서만 접근 허용)

Version은 정책 언어의 버전을 명시하며 가장 최신 버전은 `2012-10-17`이다

아래는 가장 최신 버전(Version 5)의 `AmazonEC2FullAccess` 정책으로 ec2, loadbalancing, cloudwatch, autoscaling, iam(condition)에 대한 api 작업을 허용한다 

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "ec2:*",
            "Effect": "Allow",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "elasticloadbalancing:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "cloudwatch:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "autoscaling:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "iam:CreateServiceLinkedRole",
            "Resource": "*",
            "Condition": {
                "StringEquals": {
                    "iam:AWSServiceName": [
                        "autoscaling.amazonaws.com",
                        "ec2scheduled.amazonaws.com",
                        "elasticloadbalancing.amazonaws.com",
                        "spot.amazonaws.com",
                        "spotfleet.amazonaws.com",
                        "transitgateway.amazonaws.com"
                    ]
                }
            }
        }
    ]
}
```

### type

policy는 연결 방식과 관리 형태에 따라 identity-based와 resource-based 유형으로 나뉜다

#### 1. identity-based policy

IAM user, user group, policy에 연결되는 정책으로 관리형 정책(managed policies)과 인라인 정책(inline policies)로 세분화된다

managed policies: 여러 엔티티에 재사용 가능한 정책으로 AWS가 생성하고 관리하는 AWS 관리형 정책과, 사용자가 생성하고 관리하는 사용자 관리형 정책으로 나뉜다

inline policies: 단일 엔티티에 직접 내장된 정책으로 재사용할 수 없다

#### 2. resource-based policy

특정 aws 리소스에 직접 연결되는 정책 (s3 버킷 정책, kms 키 정책 등)

리소스 소유자가 외부 엔티티(다른 계정/서비스)에 대한 접근을 제어할 때 사용한다

### policy evaluation logic

aws는 요청이 들어오면 관련된 모든 정책을 평가하여 접근의 허용 여부를 결정하며 이 과정에서 사용되는 평가 로직은 다음과 같다

1. 명시적 거부(explicit deny): 어떤 정책이든지 요청에 해당되는 Effect에 Deny가 포함되면 거부된다
2. 명시적 허용(explicit allow): Allow가 명시되고 Deny가 없으면 요청을 허용한다
3. 암시적 거부(implicit deny): Allow가 없으면 기본적으로 거부한다

정책 A와 B가 s3 버킷에 대한 작업 허용 여부를 결정한다고 가정해보자 
- 정책 A: s3 버킷 읽기 허용
- 정책 B: 동일 버킷에 대한 모든 작업 거부 

정책 B의 Deny가 우선하여 읽기 요청이 거부된다


## IAM identity center

IAM identity center는 조직 구성원(직원, 파트너 등)가 여러 aws 계정과 애플리케이션에 접근할 수 있도록 중앙화된 관리 기능을 제공하는 서비스이며 이전에는 aws single sign-on(sso)라는 이름을 가졌다

사용자가 한 번의 로그인으로 다양한 aws 리소스와 통합된 애플리케이션에 접근할 수 있게 해준다

주요 기능은 다음과 같다

#### identity management

IAM identity center 내에서 직접 사용자를 생성하거나 기존의 identity provider(idp)와 연결할 수 있다

외부 디렉토리에서 사용자와 그룹을 동기화하여 기존 아이덴티티를 aws 환경에서 활용할 수 있다

#### single sign-on

사용자는 한 번 로그인하면 aws 계정, aws 관리 애플리케이션(amazon q developer, amazon quicksight), 기타 클라우드 애플리케이션에 추가 로그인없이 접근할 수 있다

이를 위해 aws access portal이라는 웹 인터페이스를 제공하며 사용자는 자신에게 할당된 모든 리소스(권한, 역할 등)를 여기서 확인할 수 있다

장기 자격 증명(액세스 키) 대신 일시적인 자격 증명을 제공하여 보안성을 높이고, 신뢰된 아이덴티티 전파(trusted identity propagation)를 통해 aws 관리 애플리케이션 간 사용자 아이덴티티를 안전하게 공유할 수 있다

#### multi-account permissions management

aws organizations를 사용하는 경우 IAM identity center를 통해 여러 aws 계정에 대한 접근 권한을 일관적으로 관리할 수 있다

권한 세트(permission sets)라는 IAM 정책 세트를 정의하여 사용자나 그룹에 특정 계정에 대한 접근 권한을 부여한다

#### attribute-based access control (abac)

아이덴티티 소스의 속성(부서, 직함 등)을 바탕으로 동적인 권한을 정의할 수 있으며, 이를 통해 접근 관리를 간소화한다

### how identity center works

#### 1. identity source setup

IAM identity center의 기본 디렉토리에 사용자를 직접 생성하거나 외부 idp(azure ad, okta 등)와 연결한다

외부 idp를 사용할 경우 SAML 2.0 또는 SCIM(system for cross-domain identity management)을 통해 사용자와 그룹을 동기화한다

#### 2. permission set align

관리자는 IAM 정책으로 구성된 권한 세트(특정 계정에서 수행할 수 있는 작업)를 만들고 사용자나 그룹에 할당할 수 있다

#### 3. access portal login

사용자는 자신의 자격 증명(기본 디렉토리 또는 idp에서 제공된 자격 증명)으로 포털에 로그인한다

로그인 후, 할당된 계정과 애플리케이션에 접근할 수 있다

#### 4. issue temporary credential

aws cli/sdk를 통한 프로그래밍 방식의 접근이 필요한 경우 액세스 포털에서 일시적 자격 증명을 받아 사용할 수 있다

### IAM user vs IAM identity center user

| 구분      | IAM user                                           | IAM identity center user                                                        |
|---------|----------------------------------------------------|---------------------------------------------------------------------------------|
| 정의      | 단일 aws 계정 내에서 생성된 개별 아이덴티티로 aws 리소스/서비스에 접근할 때 사용된다 | 조직 전체에서 사용자 아이덴티티를 중앙 관리하고, sso를 통해 접근을 간소화한다. 여러 aws 계정과 애플리케이션에 중앙화된 접근을 제공한다 |
| 범위      | 각 계정마다 독립적으로 관리된다 (계정 A의 IAM 사용자는 계정 B에서 사용 불가)    | aws organizations와 통합되어 조직 전체에서 중앙 관리가 가능하며, 여러 aws 및 애플리케이션에 걸쳐서 접근할 수 있다      |
| 인증 방식   | aws에 직접 로그인하거나 액세스 키로 인증한다 (자격 증명은 aws 계정 내에 저장됨)  | idp 또는 기본 디렉토리를 통해 인증한다 (인증 후 일시적 자격 증명을 발급받음)                                  |
| 자격 증명 유형 | 장기 자격 증명(액세스 키)                                    | sso를 통해 발급받은 단기 자격 증명                                                           |
| 통합 기능   | 주로 aws 서비스에 대한 접근에 국한되며 외부 애플리케이션 통합은 제한된다         | SAML 2.0 또는 OIDC를 지원하는 서드파티 앱(salesforce, microsoft 365)과 통합할 수 있다              |
| 사용 사례   | 소규모 또는 단일 계정 환경                                    | 대규모, 다중 계정 및 sso가 필요한 조직                                                        |


## security token service

aws security token service(STS)는 사용자나 애플리케이션이 aws 리소스에 일시적으로 접근할 있도록 임시 자격 증명(temporary security credentials)을 발급해주는 기능을 지원한다

보안성을 높이기 위해 장기 자격 증명(IAM user 액세스 키 등)의 사용을 줄이고자 할 때 주로 사용된다

주요 기능은 다음과 같다

### issue temporary credentials

요청에 따라 Access Key ID, Secret Access Key, Session Token으로 구성된 임시 자격 증명을 발급한다

기본적으로 1시간동안 유효하고 설정에 따라 12시간까지 최대 유효 기간을 늘릴 수 있으며, 만료 후 재발급이 필요하다

### assume role

사용자가 특정 IAM role을 가정하여 해당 role에 부여된 권한을 일시적으로 사용할 수 있다

e.g: 개발자가 운영 환경의 리소스에 접근하기 위해 관리자 역할을 가정

### cross account access

특정 aws 계정의 사용자가 다른 aws 계정의 리소스에 접근할 수 있는 기능을 지원한다

e.g: 게정 A에서 계정 B의 s3 버킷에 접근

### federation

외부 idp(azure ad, okta 등)와 연동하여 사용자 인증을 처리하고 aws 리소스에 대한 임시 자격 증명을 발급할 수 있다

이를 통해 조직의 기존 사용자 인증 체계를 aws에서 활용할 수 있다

### how sts works

aws sts가 임시 자격 증명을 발급하고 작동하는 과정은 다음과 같다

#### 1. request temporary credential issue

사용자나 애플리케이션이 sts에 임시 자격 증명 발급을 요청한다

요청 시 가정할 역할의 ARN, 세션 이름, 유효 기간 등을 설정할 수 있다

#### 2. request validation

sts는 요청자의 자격 증명(액세스 키 등)과 role의 신뢰 정책(trust policy)을 확인한다

자격 증명을 통해 사용자의 아이덴티티를 확인하고 신뢰 정책이 요청자를 허용하는지 검증한다

#### 3. issue temporary credentials

검증에 성공하면 sts는 임시 자격 증명을 발급한다

#### 4. access aws resource

발급받은 자격 증명을 통해 aws api를 호출하여 리소스/서비스에 접근한다

설정된 유효 기간이 지나면 자동으로 만료되어 더 이상 사용할 수 없게 된다
