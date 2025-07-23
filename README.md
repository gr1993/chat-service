# chat-service
동일한 채팅 서비스를 Spring MVC와 WebFlux 각각의 방식으로 구현하여 성능과 처리 방식 차이를 비교하기  


<pre><code>```
[chat-react]
    └── 간단한 채팅 클라이언트 (React 기반)

[chat-bot]
    └── 시뮬레이션용 부하 테스트 봇 (채팅 생성기)

[chat-mvc]
    └── Spring Boot MVC + Tomcat 기반 채팅 서버

[chat-webflux]
    └── Spring Boot WebFlux + Netty 기반 채팅 서버

[monitoring]
    └── Prometheus, Grafana 등 모니터링 설정
``` </code></pre>


# 프로젝트 개요
WebSocket 기반의 간단한 채팅 기능을 두 가지 방식(MVC와 WebFlux)으로 구현하였다. 이후 가상의 클라이언트인  
chat-bot를 통해 시뮬레이션 부하를 발생시키고 Prometheus, Grafana 등 모니터링 도구로 성능 및 안정성을 측정한다.  

모니터링 하고자 하는 지표는 아래와 같다.  

| 항목           | 설명                                      |
|----------------|-------------------------------------------|
| 메시지 처리량   | 초당 처리 가능한 메시지 수 (TPS)          |
| 응답 속도       | 평균 / 최대 지연 시간                    |
| 리소스 사용량   | CPU, 메모리, GC 시간 등                     |
| 메시지 유실률   | 전송 대비 수신 누락 비율                  |
| 최대 접속자 수  | 서버가 안정적으로 처리 가능한 동시 접속 수 |


### 채팅 어플리케이션 기능
* 아이디만 입력하여 채팅 서버로 입장
    * 사용자 입장은 REST API (단발성 요청)
* 채팅방 생성, 조회, 입장, 퇴장
    * 채팅방 생성은 REST API, 채팅방 생성 알림은 WebSocket
    * 첫 채팅방 목록 조회는 REST API, 실시간 채팅방 정보는 WebSocket
    * 채팅방 입장은 REST API, 채팅방 입장 알림은 WebSocket
    * 채팅방 퇴장은 REST API, 채팅방 퇴장 알림은 WebSocket
* 1:1 또는 그룹 채팅 기능
    * 채팅방 내 모든 실시간 활동(메시지, 입장/퇴장 알림 등)은 WebSocket


### 클라이언트를 통한 기능 시연영상



### 성능테스트 결과
MVC와 WebFlux 웹 서버는 성능 비교의 정확도를 높이기 위해 동일한 자원 제약 조건하에 실행하였다.  

Docker 컨테이너의 리소스 설정은 다음과 같다.
- CPU: 1024 shares
- 메모리: 2GB
- 디스크: 20GB