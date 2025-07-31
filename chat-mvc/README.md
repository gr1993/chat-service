# chat-mvc

Spring Boot 기반에서 web과 websocket 의존성을 사용하여 채팅 서버 API를 제공하는 프로젝트이다.  

### 백엔드 기술
* Spring Boot 3.5.3 (JDK 17)
* spring-boot-starter-aop : API 엔드포인트 시간 측정에 사용 예정
* websocket
* spring-boot-starter-test (junit5, Mockito, MockMvc)

### 도커 환경 구성
이 프로젝트는 jdk17-alpine 기반의 Dockerfile을 사용하여 Gradle로 빌드된다.
프로젝트 루트에 있는 docker-compose.yml 파일을 통해 빌드된 이미지를 실행할 수 있으며, 리소스가 제한된 환경에서  
서버를 구동하고 테스트할 수 있도록 구성되어 있다.

재빌드 후 실행 명령어 : docker-compose up --build -d
재실행 명령어 : docker-compose down && docker-compose up -d