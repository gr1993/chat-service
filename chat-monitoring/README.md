# chat-monitoring

채팅 앱 서버는 Spring Boot 기반으로 실행되며, Actuator를 통해 애플리케이션 메트릭(Application Metrics)을 노출한다.  
Prometheus는 일정 간격으로 이 메트릭을 수집(pull)하고, Grafana를 통해 시각화할 예정이다.  

### 모니터링 시스템
* Prometheus
* Grafana


### Grafana 시각화
그라파나를 시각화하는 방법은 크게 두 가지가 있다. 그라파나 웹 UI에 접속하여 데이터 소스 설정으로 프로메테우스 서버  
접속 정보를 등록하는 수동 방법이 있고, docker-compose.xml 파일이 있는 하위 경로에 프로비저닝 파일을 추가하여  
컨테이너 시작 시 자동으로 프로메테우스 데이터 소스 설정을 추가하는 방법이 있다. 이번에는 자동 설정 방식을 사용했다.

Grafana에서 시각화를 설정하는 방법은 크게 두 가지가 있다.
1. Grafana 웹 UI에 접속해 데이터 소스 설정에서 Prometheus 서버의 접속 정보를 직접 등록하는 수동 방식
2. docker-compose.yml이 위치한 하위 경로에 프로비저닝(provisioning) 파일을 추가하여, 컨테이너가 시작될 때 자동으로 Prometheus를 데이터 소스로 등록하는 방식

이번에는 후자인 자동 설정 방식을 사용하였다.  
대시보드 또한 프로비저닝 방식을 사용했으며, 사전에 대시보드를 구성하는 것은 웹 UI로 진행한 후 Export 기능을 이용해  
JSON 파일로 저장하여 다음 컨테이너 구동부터 자동 설정되도록 구성하였다.


### 도커 환경 구성
docker-compose.yml 파일을 작성하여 쉽게 도커 컨테이너 구동이 가능하도록 제공하고 있다.  

```shell
# 실행 명령어
docker-compose up -d
```