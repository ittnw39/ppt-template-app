# PPT Template App

Spring Boot 기반의 PowerPoint 템플릿 데이터 치환 애플리케이션입니다.

## 🚀 주요 기능

- **PPT 템플릿 데이터 치환**: PPTX 파일의 플레이스홀더를 실제 데이터로 치환
- **데이터베이스 연동**: H2/MySQL/MariaDB 지원
- **REST API**: 완전한 CRUD 작업 지원
- **웹 기반 테스트**: 브라우저에서 직접 테스트 가능

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 21
- **Database**: H2 (개발용), MySQL/MariaDB (프로덕션)
- **Security**: Spring Security
- **Build Tool**: Gradle
- **Library**: Apache POI (PPTX 처리)

## 📋 API 엔드포인트

### 1. 데이터베이스 관련

#### 🔍 데이터베이스 연결 테스트
```
GET /ppt/db-test
```
- **기능**: 데이터베이스 연결 상태 확인
- **응답**: JSON (연결 상태, 총 레코드 수, 모든 보고서 목록)

#### 📝 테스트 데이터 생성
```
GET/POST /ppt/create-test-data?title=제목&content=내용
```
- **기능**: 데이터베이스에 새로운 보고서 데이터 생성
- **파라미터**: 
  - `title` (기본값: "테스트 보고서")
  - `content` (기본값: "이것은 테스트 데이터입니다.")
- **응답**: 생성된 보고서 정보

#### 📋 모든 보고서 조회
```
GET /ppt/reports
```
- **기능**: 데이터베이스에 저장된 모든 보고서 목록 조회
- **응답**: 보고서 배열 (JSON)

### 2. PPT 생성 관련

#### 📄 직접 PPT 생성
```
GET/POST /ppt/test-ppt-generation?title=제목&content=내용
```
- **기능**: 파라미터로 전달받은 데이터로 즉시 PPTX 파일 생성
- **파라미터**:
  - `title` (기본값: "테스트 제목")
  - `content` (기본값: "테스트 내용입니다.")
- **응답**: PPTX 파일 다운로드

#### 💾 저장된 데이터로 PPT 생성
```
GET /ppt/download/{id}
```
- **기능**: 데이터베이스에 저장된 보고서(ID)로 PPTX 파일 생성
- **파라미터**: `id` - 보고서 ID
- **응답**: PPTX 파일 다운로드

### 3. 템플릿 분석

#### 🔍 템플릿 분석 (JSON)
```
GET /ppt/analyze-template
```
- **기능**: PPTX 템플릿의 플레이스홀더 분석
- **응답**: 텍스트 형태의 분석 결과

#### 🌐 템플릿 분석 (HTML)
```
GET /ppt/analyze-template-html
```
- **기능**: 브라우저에서 보기 좋은 HTML 형태의 템플릿 분석
- **응답**: HTML 페이지 (테스트 링크 포함)

### 4. 데이터베이스 관리

#### 🗄️ H2 데이터베이스 콘솔
```
GET /h2-console
```
- **기능**: H2 데이터베이스 웹 콘솔 접근
- **연결정보**:
  - JDBC URL: `jdbc:h2:mem:testdb`
  - 사용자명: `sa`
  - 비밀번호: `password`

## 🧪 테스트 방법

### 1단계: 템플릿 분석
```
http://localhost:8080/ppt/analyze-template-html
```

### 2단계: 테스트 데이터 생성
```
http://localhost:8080/ppt/create-test-data?title=성과보고서&content=2025년1월프로젝트성과입니다
```

### 3단계: PPT 파일 생성
```
http://localhost:8080/ppt/test-ppt-generation?title=성과보고서&content=2025년1월프로젝트성과입니다
```

### 4단계: 저장된 데이터로 PPT 생성
```
http://localhost:8080/ppt/download/1
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/ppttemplateapp/
│   │   ├── controller/PptController.java    # REST API 컨트롤러
│   │   ├── service/PptService.java         # PPT 생성 서비스
│   │   ├── model/Report.java               # 데이터 모델
│   │   ├── repository/ReportRepository.java # 데이터 접근 계층
│   │   ├── config/SecurityConfig.java      # 보안 설정
│   │   └── DTO/ReportDto.java             # 데이터 전송 객체
│   └── resources/
│       ├── application.properties          # 애플리케이션 설정
│       └── templates/template.pptx         # PPT 템플릿 파일
```

## 🔧 설정 파일

### application.properties
```properties
# H2 데이터베이스 (개발용)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# JPA 설정
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 📝 템플릿 파일 형식

PPTX 템플릿 파일에서 다음 플레이스홀더를 사용:
- `${title}` - 보고서 제목
- `${content}` - 보고서 내용

## 🚀 실행 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 브라우저에서 테스트
```
http://localhost:8080/ppt/analyze-template-html
```

## 🔗 GitHub 리포지토리

- https://github.com/ittnw39/ppt-template-app.git
- https://github.com/Ean8510970/ppt-template-app.git

## 📄 라이선스

이 프로젝트는 테스트 목적으로 개발되었습니다.

---

**개발 완료일**: 2025년 6월 20일  
**주요 기능**: PPT 템플릿 데이터 치환, 데이터베이스 연동, REST API 제공 