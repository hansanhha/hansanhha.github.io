---
layout: default
title:
---

[docker registry](#docker-registry)

[registry type](#registry-type)


## docker registry

도커 레지스트리는 도커 이미지를 업로드하고 배포하는 중앙 저장소 역할을 한다

레지스트리에 이미지를 업로드하고 다른 환경(개발, 테스트, 프로덕션)이나 다른 사용자와 공유할 수 있다

도커 클라이언트(docker pull, docker push)는 restful api를 통해 레지스트리와 통신하여 이미지를 업로드하거나 다운로드한다


## registry type

### public registry

docker hub: 기본적으로 사용되는 공개 도커 레지스트리, 도커 클라이언트는 레지스트리 주소를 생략하는 경우 도커 허브를 참조한다

quay.io, github container registry (ghcr) 등

### private registry

기업이나 조직 내부에서 도커 이미지를 안전하게 관리하기 위해 구축하는 레지스트리

보안, 접근 제어, 네트워크 정책 등을 커스터마이징하여 외부 노출 없이 이미지를 관리할 수 있다

docker trusted registry (dtr): docker enterprise edition에서 제공하는 프라이빗 레지스트리 솔루션

harbor: 오픈소스 프라이빗 레지스트리 - 이미지 스캔, RBAC, replication 기능 등을 지원한다