---
layout: default
title:
---

[Multipart ContentType](#multipart-contenttype)

[Spring MVC File Handling Workflow](#spring-mvc-file-handling-workflow)

[Spring MultipartFile](#spring-multipartfile)

## Multipart ContentType

`multipart/*-data` ContentType은 HTTP 프로토콜에서 여러 개의 데이터를 한 번의 요청/응답으로 전송하기 위한 MIME 타입으로 

파일 업로드, 이메일, 복잡한 데이터 조합을 처리할 때 주로 사용됨

데이터를 파트 별로 나눠 전송하는 방식으로, 각 파트는 고유한 헤더와 데이터를 포함하며 바운더리로 구분됨

### Multipart Structure

HTTP Content-Type 헤더에 Multipart ContentType과 함께 바운더리 문자열을 지정함

바운더리
- 각 파트를 구분하는 문자열(클라이언트와 서버는 바운더리를 기준으로 데이터를 나눔)
- 고유한 식별자로 설정되며 `--`로 시작함
- HTTP Content-Type 헤더에 포함됨
- `--boundary--`로 Multipart 요청의 끝을 나타냄

```text
Content-Type: multipart/form-data; boundary="boundaryAir"
```

각 파트는 개별적인 헤더와 본문을 가짐
- 헤더: 해당 파트의 메타 정보(파일 이름, 타입, 크기 등)
  - Content-Disposition: 데이터 이름 및 추가 메타 정보
  - Content-Type: 데이터 MIME 타입
- 본문: 파일 또는 텍스트 데이터

```text
-----boundaryAir
Content-Disposition: form-data; name="filename"; filename="hamburger.jpg"
Content-Type: image/jpeg

(binary data of hamburger.jpg)
-----boundaryAir
Content-Disposition: form-data; name="username"

spongebob
--boundary--
```

### multipart/form-data

웹 폼 데이터 전송할 때 전송할 때(파일 업로드 포함) 사용하는 MIME 타입으로 

서버에게 `Content-Type: multipart/form-data` 헤더와 함께 파일과 기타 데이터를 전송함

#### multipart/form-data vs x-www-form-urlencoded

|| multipart/form-data  | x-www-form-urlencoded  |
|---|-------------------------------------------------|-----------------------------------|
|Content-Type| multipart/form-data; boundary=--exampleboundary | application/x-www-form-urlencoded |
|용도| 파일 업로드 포함된 폼 데이터 전송 | 단순 폼 데이터 전송 |
|데이터 형식| 바운더리로 구분되는 여러 파트(파트 별 헤더, 본문)| URL로 인코딩된 단순 문자열|
|크기 제한| 큰 데이터(파일) 지원| URL 길이 제한 적용 |

### multipart/mixed

여러 종류의 데이터를 혼합하여 전송할 때 사용하는 MIME 타입으로 주로 이메일 프로토콜에서 사용함

e.g 이메일 메시지 본문과 첨부 파일 제공

```text
Content-Type: multipart/mixed; boundary="-----exampleEmailBoundary"

-----exampleEmailBoundary
Content-Type: text/plain

email message body
-----exampleEmailBoundary
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="emailFile.pdf"

(binary data of emailFile)
--boundary42--
```

### multipart/alternative

동일한 컨텐츠를 여러 형식으로 전송할 때 사용하는 MIME 타입

e.g 이메일에서 HTMl 형식과 텍스트 본문을 함께 제공

```text
Content-Type: multipart/alternative; boundary="-------exampleEmailBoundary"

-------exampleEmailBoundary
Content-Type: text/plain

plain text version
-------exampleEmailBoundary
Content-Type: text/html

<html><body>html version../body></html>
```

## Spring MVC File Handling Workflow

1. 클라이언트 요청 `Content-Type: multipart/*-data`
2. 스프링의 MultipartResolver 인터페이스를 통해 멀티파트 요청 처리
   1. Servlet 애플리케이션: StandardServletMultipartResolver 구현체
3. MultipartFile 객체 생성
   1. MultipartResolver가 요청을 파싱하여 각 파일을 추출해 MultipartFile 객체로 변환하고, 기타 데이터를 일반 요청 파라미터로 파싱함
4. 컨트롤러 수신
   1. @RequestParam 또는 @ModelAttribute를 사용해 MultipartFile 객체를 매핑
5. 비즈니스 로직 처리

#### MultipartResolver
- 멀티파트 요청 파싱
- HttpServletRequest를 MultipartHttpServletRequest로 변환함 
- 파일 데이터는 MultipartFile 객체로 변환
- 기타 데이터는 요청 파라미터로 파싱

## Spring MultipartFile

```java
public interface MultipartFile extends InputStreamSource {

    // 멀티파트 form 파라미터 이름
	String getName();

    // 클라이언트가 업로드한 파일의 원래 이름
	@Nullable
	String getOriginalFilename();

    // 파일의 MIME 타입
	@Nullable
	String getContentType();

	boolean isEmpty();

    // 파일 크기(byte 단위)
	long getSize();

    // 파일의 내용을 byte 배열로 반환
	byte[] getBytes() throws IOException;
    
    // 파일의 내용을 InputStream으로 반환
	@Override
	InputStream getInputStream() throws IOException;

    // 파일의 내용을 지정된 경로에 저장
    void transferTo(File dest) throws IOException, IllegalStateException;
    
    /*
        MultipartFile 객체 정보(길이, 파일 이름 등) 제공
        RestTemple, WebClient의 입력값으로 사용할 수 있음
     */
	default Resource getResource() {
		return new MultipartFileResource(this);
	}

    /*
        파일의 내용을 지정된 경로에 저장하는 defalut 메서드
        기본 구현은 MultiparFile 구현체의 내용을 지정된 경로에 복사함
     */
	default void transferTo(Path dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(getInputStream(), Files.newOutputStream(dest));
	}

}
```