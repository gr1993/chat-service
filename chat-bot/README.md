# chat-bot

채팅 서버를 테스트하기 위해 만들어진 시뮬레이션용 부하 테스트 봇 프로젝트  

### 부하테스트 사용 기술
* JDK 17
* RestTemplate
* ObjectMapper
* spring-websocket, spring-messaging

### Load Test Config (config.json)

| Key | Type | Description | Example |
|-----|------|-------------|---------|
| `userIdPrefix` | string | 시뮬레이션할 사용자명 prefix   | `userIdPrefix` |
| `userCount` | int | 총 시뮬레이션할 사용자 수 | `100` |
| `rampUpTimeSeconds` | int | 모든 사용자가 입장 완료되기까지 걸리는 시간(초) | `10` |
| `chatDurationSeconds` | int | 사용자가 채팅방에 머무는 시간(초) | `60` |
| `messageLength` | int | 생성할 메시지 문자열 길이 | `30` |
| `restApiBaseUrl` | string | 로그인/채팅 등 REST API의 Base URL | `http://localhost:8080` |
| `webSocketEndpoint` | string | WebSocket 접속 URL | `ws://localhost:8080/ws/chat` |

### 부하테스트 리포팅
아래는 부하테스트가 끝나고 출력되는 결과 예시이다.

모든 사용자 시뮬레이션이 완료되어 메인 함수를 종료합니다.
총 메세지 전송 갯수 : 600개