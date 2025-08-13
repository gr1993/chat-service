# chat-webflux

Spring WebFlux 기반에서 web과 websocket 의존성을 사용하여 비동기 논블로킹 채팅 서버 API를 제공하는 프로젝트이다.
도커 컨테이너 구동 및 리소스 제한은 mvc와 동일하게 구성하여 진행하였다.

### 백엔드 기술
* Spring Boot 3.5.4 (JDK 17)
* spring-boot-starter-webflux : 이 의존성에 WebSocket도 포함되어 있음


### Spring WebFlux 환경에서 STOMP
Spring WebFlux에서 STOMP를 쓰는 건 MVC에서처럼 쉽게 되지 않는다. 기본적으로 제공하는 API도 없기 때문이다.  
그리고 실제 WebFlux + STOMP 조합은 잘 쓰이지도 않고 권장되지 않는 방향이라고 한다. Webflux 환경에서는 STOMP를  
사용하는 대신, 필요에 따라 Redis나 Kafka와 같은 외부 Pub/Sub(발행/구독) 모델 저장소를 활용하는 것이 더 일반적이고  
효율적인 접근 방식이다. 이번 프로젝트에서는 클라이언트에서 이미 WebSocket + STOMP 형식으로 전송하고 있으며 성능을  
비교할 Spring Boot MVC 서버도 STOMP 기반이니 수동 구현으로 흉내를 내는 수준으로 구현하고 테스트할 예정이다.  
그리고 다음 포트폴리오 때 카프카를 이용하여 더 정석적인 방법으로도 구현해보도록 하겠다.
