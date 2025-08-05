# chat-mvc

Spring Boot 기반에서 web과 websocket 의존성을 사용하여 채팅 서버 API를 제공하는 프로젝트이다.  

### 백엔드 기술
* Spring Boot 3.5.3 (JDK 17)
* spring-boot-starter-aop : API 엔드포인트 시간 측정에 사용 예정
* websocket
* spring-boot-starter-test (junit5, Mockito, MockMvc)
* spring-boot-starter-actuator : 모니터링에 필요하며 Micrometer를 포함한 모듈
* micrometer-registry-prometheus : MeterRegistry 프로메테우스 구현체


### 도커 환경 구성
이 프로젝트는 jdk17-alpine 기반의 Dockerfile을 사용하여 Gradle로 빌드된다.
프로젝트 루트에 있는 docker-compose.yml 파일을 통해 빌드된 이미지를 실행할 수 있으며, 리소스가 제한된 환경에서  
서버를 구동하고 테스트할 수 있도록 구성되어 있다.

#### 제한 설정 적용 위치
docker-compose.yml : CPU, Memory 제한 설정
Dockerfile : tc를 사용하여 네트워크 속도를 제한하려 했으나 호스트 OS가 Window11이라 불가하였다.

#### 컨테이너 실행 명령어
```shell
# 재빌드 후 실행 명령어
docker-compose up --build -d

# 재실행 명령어
# 명령어 파이프 (linux=&&, Window cmd=&, Window PowerShell=;)
docker-compose down && docker-compose up -d
```


### 리소스 제한 확인
도커 컨테이너 환경에서 리소스 제한이 걸려있는지 확인하는 명령어들을 정리하였다. 컨테이너에 직접 접속하여  
리눅스 명령어로 확인하였고 만약 도커 명령어단에서 확인하고 싶다면 docker inspect 명령어를 사용하면 된다.  

#### CPU 및 메모리 
```shell
# CPU의 사용 제한을 나타내며, -1이면 무제한을 의미
# 예 : 50000 (50000 마이크로초 (50ms)로 설정된 경우, 1초당 50ms만큼 CPU를 사용 가능)
cat /sys/fs/cgroup/cpu/cpu.cfs_quota_us

# 메모리 제한 확인 (제한이 없는 경우 매우 큰 수가 나옴)
cat /sys/fs/cgroup/memory/memory.limit_in_bytes
```

#### 네트워크 속도 테스트 명령어
```shell
wget http://speed.hetzner.de/100MB.bin

# 예시 : 41.2 Mbps 이며 5.15 MB/s 속도로 측정된 명령어 결과 확인 
2025-07-31 12:00:20 (5.15 MB/s) - ‘100MB.bin’ saved [104857600/104857600]

# wget에 속도 정보가 없는 경우 : openjdk:17-jdk-alpine 환경에서 추가적으로 실행할 명령어
apk add --no-cache wget
```
