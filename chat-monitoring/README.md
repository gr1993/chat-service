# chat-monitoring

채팅 앱 서버는 Spring Boot 기반으로 실행되며, Actuator를 통해 애플리케이션 메트릭(Application Metrics)을 노출한다.  
Prometheus는 일정 간격으로 이 메트릭을 수집(pull)하고, Grafana를 통해 시각화할 예정이다.  

### 모니터링 시스템
* Prometheus
* Grafana


### Prometheus의 지표명 확인 방법
Spring Boot에서는 /actuator/metrics 엔드포인트를 통해 JVM을 포함한 다양한 지표명을 쉽게 확인할 수 있다. 하지만  
Prometheus에서는 지표명만을 나열해주는 전용 엔드포인트가 제공되지 않기 때문에, 다음과 같은 방법을 통해 지표명을 확인할 수 있다  

```shell
# 프로메테우스 지표명 목록 출력 url
curl http://<prometheus-host>:9090/api/v1/label/__name__/values
```

#### 이번 프로메테우스에 사용된 지표명
```
# CPU 사용률
system_cpu_usage
process_cpu_usage

# 메모리 사용량
jvm_memory_used_bytes{area="heap"}

# GC 횟수 / 최대 지연 시간
jvm_gc_pause_seconds_count
jvm_gc_pause_seconds_max

# REST API 관련 지표(방 입장 API)
http_server_requests_seconds_count{uri="/api/room/{roomId}/enter"}
# 방 입장 API 평균 응답시간이며 급격한 변화율을 감지하고 싶어서 1분 이내로 설정
rate(http_server_requests_seconds_sum{uri="/api/room/{roomId}/enter"}[1m]) / rate(http_server_requests_seconds_count{uri="/api/room/{roomId}/enter"}[1m])
# 최근 1분 동안 가장 오래걸린 응답시간
max_over_time(http_server_requests_seconds_max{uri="/api/room/{roomId}/enter"}[1m])

# 웹소켓 메시지 전송 지표(커스텀 지표)
# 어플리케이션에서 websocket_message_seconds 커스텀 지표 추가
# 누적 호출수
websocket_message_seconds_count
# TPS
rate(websocket_message_seconds_count[1m])
# 최근 1분 동안 가장 오래걸린 처리시간
max_over_time(websocket_message_seconds_max[1m])

# 동시 접속자 수(커스텀 지표)
# 웹소켓 연결 수
chat_app_active_connections
```


### Grafana 시각화
Grafana에서 시각화를 설정하는 방법은 크게 두 가지가 있다.
1. Grafana 웹 UI에 접속해 데이터 소스 설정에서 Prometheus 서버의 접속 정보를 직접 등록하는 수동 방식
2. docker-compose.yml이 위치한 하위 경로에 프로비저닝(provisioning) 파일을 추가하여, 컨테이너가 시작될 때 자동으로 Prometheus를 데이터 소스로 등록하거나 대시보드를 불러오는 방식

이번에는 후자인 자동 설정 방식을 사용하였다.  
대시보드 또한 프로비저닝 방식을 사용했으며, 사전에 대시보드를 구성하는 것은 웹 UI로 진행한 후 Export 기능을 이용해  
JSON 파일로 저장하여 다음 컨테이너 구동부터 자동 설정되도록 구성하였다.

#### 대시보드 캡쳐 이미지


### 도커 환경 구성
docker-compose.yml 파일을 작성하여 쉽게 도커 컨테이너 구동이 가능하도록 제공하고 있다.  

```shell
# 실행 명령어
docker-compose up -d
```