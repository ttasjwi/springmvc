---

# Spring MVC

김영한 님의 "스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술" 강의 코드를 따라치면서 간략하게나마 학습정리를 조금씩 하기 위한 Repository

---

### 프로젝트 환경

- jdk : 11
- IDE : intelliJ Ultimate
- 빌드 : gradle, Spring Boot
```groovy
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```
- 의존 라이브러리
  - lombok
  - spring-boot-starter-web
  - spring-boot-starter-thymeleaf

---

### welcome 페이지
`resources/static/index.html` : 강의 실습 코드들 실행 결과 `view`를 한 페이지에 모아둠

---

## Logger

```
2022-03-04 11:04:35.611  WARN 30252 --- [nio-8080-exec-1] c.t.springmvc.basic.LogTestController    :  warn log=spring
2022-03-04 11:04:35.611 ERROR 30252 --- [nio-8080-exec-1] c.t.springmvc.basic.LogTestController    : error log=spring
```
- sout과 같이 무조건적으로 로그에 콘솔에 찍어내리는 메서드를 사용하지 않고, 필요 레벨에 따라 콘솔에 로그를 출력하기 위함
- 별도의 설정시, 로그를 파일/네트워크 등의 위치에 별도로 로그를 남길 수 있음. (심지어 파일로 남길 경우 로그 분할/백업 등의 기능도 지원됨.)
- 출력 포맷 : 시간, 레벨, PID, 스레드명, 클래스명, 로그 메시지
- sout보다 성능이 더 좋음(내부 버퍼링, 멀티 스레드, ...)

<details>
<summary>세부적인 사용법(접기/펼치기)</summary>
<div markdown="1">



![slf4j.jpg](img/slf4j.jpg)

- 스프링부트에서는 기본적으로 로깅 라이브러리로 slf4j를 제공함
  - 인터페이스 : slf4j
  - 구현체 : Logback
```properties
##root 경로와 그 하위 로그 레벨 설정을 info로(기본값)
logging.level.root=info

##com.ttasjwi.springmvc 패키지와 그 하위 로그 레벨 설정
logging.level.com.ttasjwi.springmvc=debug
```
- 로그 레벨을 설정파일에서 조절 가능. (application.properties, yml, ...)
- `logging.level.패키지경로...=레벨` : 로길 레벨 조정(패키지 경로 및 그 하위에 대하여)
- 디폴트로 `root` 경로의 로그 레벨은 info로 잡혀있음.
- root의 레벨을 info 아래로 두면 라이브러리 수준의 로그까지 다 잡혀버림... 보통은 root 경로는 info로 두고, 하위 경로에서 필요에 따라 debug 수준으로 잡음

```java
//@Slf4j 롬복이 자동으로 Logger를 log 변수에 생성해줌
//@Controller : 일반적으로 반환타입이 view
@RestController // Http 응답 Body에 반환
public class LogTestController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "spring";

        //log.trace("trace log="+name); 문자열 결합 : 로그에 안 찍히는데도 실제로 연산을 수행하는 비용이 발생함. 쓰지 말 것
        log.trace("trace log={}", name); // 로그의 설정 레벨보다 낮을 경우 실행조차 되지 않음.
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);
        return "ok";
    }

}
```
- 로거 생성시 `LoggerFactory.getLogger(클래스)`을 통해 Logger를 받아온뒤 사용 가능
  - 롬복에서 지원하는 `@Slf4j` 어노테이션을 달아주면 위의 작업을 자동으로 수행해줌.("log" 변수로 받아옴)
- 로깅 레벨을 trace, debug, info, warn, error 수준으로 지정할 수 있음.
  - debug : 주로 개발 단계에서 사용
  - info : 주로 배포, 운영 단계에서 사용

### 잘못된 로그 사용법
```java
log.trace("trace log="+name);
```
- 설정된 레벨보다 낮은 레벨의 로그라서 출력되지 않더라도 실제로 문자열 결합 연산이 수행되는 비용이 발생함

### 올바른 로그 사용법
```java
log.trace("trace log={}",name);
```
- 설정된 레벨보다 낮은 로그의 경우 실행되지 않음


</div>
</details>


---

