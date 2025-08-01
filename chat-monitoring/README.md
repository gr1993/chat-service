# chat-monitoring

채팅 앱 서버는 Spring Boot 기반으로 실행되며, Actuator를 통해 애플리케이션 메트릭(Application Metrics)을 노출한다.  
Prometheus는 일정 간격으로 이 메트릭을 수집(pull)하고, Grafana를 통해 시각화할 예정이다.  

### 모니터링 시스템
* Prometheus
* Grafana