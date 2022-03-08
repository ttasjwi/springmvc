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

## 요청 매핑
- Http 요청의 URL(URI, PathVariable), 파라미터, 메서드, 여러가지 헤더를 지정하여 매핑되는 요청을 제한 가능

<details>
<summary>세부적인 사용법(접기/펼치기)</summary>
<div markdown="1">

### @RestController
```java
@RestController
public class MappingController {
```
- `@RestController` : 클래스 앞에 선언. 모든 메서드의 반환 값에 대하여 반환 값으로 뷰를 찾지 않고, 반환객체를 HTTP 바디에 바로 입력

### @RequestMapping
```java
@RequestMapping(value="/mapping-get-v1", method = RequestMethod.GET)
public String mappingGetV1() {
    log.info("mappingGetV1");
    return "ok";
}
```
```java
@GetMapping(value="/mapping-get-v2")
public String mappingGetV2() {
    log.info("mappingGetV2");
    return "ok";
}
```
- value(디폴트) : url URL 호출이 오면 이 메서드가 실행되도록 함
  - 배열로 지정시 다중 설정 가능(예: `{"/hello-basic", "/hello-go"}`)
- method(메서드) : HTTP 메서드. 축약된 어노테이션이 따로 존재하는데, 현업에선 이들을 주로 사용
  - GET -> `@GetMapping`
  - POST -> `@PostMapping`
  - PUT -> `@PutMapping`
  - PATCH -> `@PatchMapping`
  - DELETE -> `@DeleteMapping`


### @RequestMapping - PathVariable
```java
@GetMapping("/mapping/{userId}")
public String mappingPath(@PathVariable String userId) {
    log.info("mappingPath userId={}", userId);
    return "ok";
}
```
```java
@GetMapping("/mapping/users/{userId}/orders/{orderId}")
public String mappingPath(@PathVariable String userId, @PathVariable Long orderId) {
    log.info("mappingPath userId={}, orderId={}", userId, orderId);
    return "ok";
}
```
- 파라미터 앞에, `@PathVariable("경로변수명")`을 지정하여 요청의 경로변수를 사용할 수 있음
- 변수명이 같으면 ()를 생략해도 된다. 
  - 예) @PathVariable("userId") String userId -> @PathVariable userId
- 복수의 `pathVariable`도 사용 가능하다.

### @RequestMapping - 특정 Parameter
```java
@GetMapping(value="/mapping-param", params="mode=debug")
public String mappingParam() {
    log.info("mappingParam");
    return "ok";
}
```
- 특정 파라미터가 있거나 없는 조건식을 추가할 수 있음. 잘 사용되진 않는다.
  - "mode" - 모든 mode 파라미터에 대해
  - "!mode" - mode가 아닌 파라미터
  - "mode=debug" : mode가 debug일 때
  - "mode!=debug" : mode가 debug가 아닐 때
- 배열로 지정할 경우 `or`로 취급됨.
  - `params = {"mode="debug","data="good"}`

### @RequestMapping - 특정 Header
```java
@GetMapping(value="/mapping-header", headers="mode=debug")
public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
}
```
- 특정 헤더가 있거나 없는 조건식을 추가할 수 있음.
  - "mode" - 모든 mode 파라미터에 대해
  - "!mode" - mode가 아닌 파라미터
  - "mode=debug" : mode가 debug일 때
  - "mode!=debug" : mode가 debug가 아닐 때

### @RequestMapping - Content-Type 기반 매핑
```java
@PostMapping(value = "/mapping-consume", consumes = MediaType.APPLICATION_JSON_VALUE)
public String mappingConsumes() {
    log.info("mappingConsumes");
    return "ok";
}
```
- consume : 소비하다 - 요청 Content-Type
  - `consumes="application/json"` : json
  - `consumes="!application/json"` : json이 아닌
  - `consumes="application/*"` : application/*에 해당하는
  - `consumes="*\/*"` : 모든
  - `MediaType.APPLICATION_JSON_VALUE` : `"application/json"`을 상수화

### @RequestMapping - Accept 헤더 기반
```java
@PostMapping(value = "/mapping-produce", produces = MediaType.TEXT_HTML_VALUE)
public String mappingProduces() {
    log.info("mappingProduces");
    return "ok";
}
```
- Accept : 클라이언트가 선호하는 미디어 타입
  - `produces="text/html"`
  - `produces="!text/html"`
  - `produces="text/*"`
  - `produces="*\/*"`
  - `produces = MediaType.TEXT_HTML_VALUE`

</div>
</details>

---

## 요청 매핑 - 실제 API 예시

<details>
<summary>세부적인 사용법(접기/펼치기)</summary>
<div markdown="1">

```java
@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {

    @GetMapping
    public String users() {
        return "get users";
    }

    @PostMapping
    public String addUser() {
        return "post user";
    }

    @GetMapping("/{userId}")
    public String findUser(@PathVariable String userId) {
        return "get userId="+ userId;
    }

    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable String userId) {
        return "patch userId="+ userId;
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return "delete userId="+ userId;
    }
}
```
![Postman_RequestMapping.png](img/Postman_RequestMapping.png)

- 클래스 레벨에서 `@RequestMapping(상위 path)`을 두고, 메서드 레벨에서 `@RequestMapping(하위 path)`를 지정하여 중복되는 경로부분을 생략할 수 있다.
- 실제 `Postman`으로 테스트를 해보면 반환된 문자열이 httpResponse Body에 그대로 담김

</div>
</details>

---

## Http 요청 - 기본, 헤더

- 어노테이션 기반 Spring Controller는 다양한 파라미터를 지원함
- 스프링 `@Controller`에서 사용가능한 파라미터 목록 : <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-arguments" target="_blank">Method Arguments</a>
- 스프링 `@Controller`에서 사용가능한 응답값 목록 : <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-return-types" target="_blank">Return Values</a>
<details>
<summary>예시(접기/펼치기)</summary>
<div markdown="1">

```java
@RequestMapping("/headers")
public String headers(HttpServletRequest request,
                      HttpServletResponse response,
                      HttpMethod httpMethod,
                      Locale locale,
                      @RequestHeader MultiValueMap<String, String> headerMap,
                      @RequestHeader("host") String host,
                      @CookieValue(value="myCookie", required = false) String cookie){
```
- `HttpServletRequest`, `HttpServletResponse`
- `HttpMethod`
- `Locale`
- `@RequestHeader MultiValueMap<String,String>` : 모든 HttpRequest 헤더
  - MultiValueMap : 한 key에 여러 value를 받을 수 있음. 꺼낼 때 get 메서드 호출 시, 배열로 받아짐.
- `@RequestHeader("key") String value` : 특정 헤더 조회
  - value : 조회 헤더
  - required : 필수값 여부(true이면 필수, false이면 필수 아님)
  - defaultValue : 기본값 지정

- `@CookieValue("key") String value` : 특정 쿠키 조회
  - value : 조회 쿠키
  - required : 필수값 여부(true이면 필수, false이면 필수 아님)
  - defaultValue : 기본값 지정


</div>
</details>

---

## Http 요청 시 값을 전달하는 방법

Http 요청 시, 서버에 데이터를 전송하는 방식은 크게 다음 세가지 방식이 존재함

- GET 방식 쿼리 파라미터
- HTML Form (POST방식으로 메시지 바디에 데이터를 넣어서 전송)
  - content-type : `application/x-www-form/urlencoded`
- Http Message Body에 직접 데이터를 담아서 전송
  - Http API에서 주로 사용하는 방식이고, 최근 제일 주류로 사용되는 것은 JSON
  - POST, PUT, PATCH

---

## Http 요청 - 요청 파라미터 조회

1. 요청 파라미터 조회
   - GET 방식, POST HTML Form 전송 방식 모두 구조적으로 동일한 방식으로 요청 파라미터를 조회할 수 있음.
   - 이 두가지 방식으로 넘어온 파라미터를 조회하는 방법을 통틀어 '요청 파라미터 조회'라고 한다.


2. 파라미터 조회 방법
   - Servlet : `HttpServletRequest` -> `request.getParameter(...)`
   - Spring : `@RequestParam`을 통해 파라미터들을 변수에 바인딩할 수 있음

<details>
<summary>세부적인 사용법(접기/펼치기)</summary>
<div markdown="1">

### V1 : HttpServletRequest을 통한 요청 파라미터 조회
```java
    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok");
    }
```
- `request.getParmeter(...)`을 통해 값 받아오기

### V2 : @RequestParam 어노테이션
```java
    @RequestMapping("/request-param-v2")
    @ResponseBody
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }
```
- `@RequestParam("파라미터명") 변수타입 변수명`
  - 파라미터 이름을 통해 변수에 바로 대입

### V3 : 파라미터명과 변수명이 같으면 @RequestParam(...)의 속성 생략 가능
```java
    @RequestMapping("/request-param-v3")
    @ResponseBody
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
```
- `@RequestParam("파라미터명") 변수타입 변수명`에서 파라미터명과 변수명이 같을 경우
- `@RequestParam(name="xxx")`에서 속성 생략 가능

### V4 : @RequestParam 생략
```java
    @RequestMapping("/request-param-v4")
    @ResponseBody
    public String requestParamV4(String username, int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
```
- 위의 단계에서 심지어 `@RequestParam`을 생략 가능
- 하지만 여기까지 오면 매우 생략이 과한 면이 있어서 의미를 명확히 하는 차원에서는 V3 정도가 적당하다는 김영한님의 의견이 있음.

### 파라미터 필수 여부 : @RequestParam(required=...)
```java
    @RequestMapping("/request-param-required")
    @ResponseBody
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        // int age <- null이 들어갈 수 없음

        log.info("username={}, age={}", username, age);
        return "ok";
    }
```
- required 지정을 통해, 값의 필수 여부를 설정할 수 있음
  - true : 값이 필수로 존재해야함
  - false : 값이 없어도 됨.
- 하지만, `required=false`일때 변수가 기본형인 경우 모순이 발생함.
  - 기본형은 null 값을 가질 수 없음
  - null을 가질 수 있는 래퍼클래스로 변수타입을 변경하거나, defaultValue를 지정
  - 또한, 요청이 올 때 `request=`의 형식으로 데이터가 전송되면 빈 문자열 `""`이 넘어오는 문제가 있음.

### 파라미터 기본값 지정 : @RequestParam(defaultValue=...)
```java
@RequestMapping("/request-param-default")
@ResponseBody
public String requestParamDefault(
        @RequestParam(defaultValue = "guest") String username,
        @RequestParam(defaultValue = "-1") int age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```
- defaultValue를 지정하여 파라미터에 값이 전달되지 않을 때에 대해서도 기본값을 적용할 수 있음.
- 빈 문자열이 온 경우에 대해서도 기본값이 적용됨
- defaultValue를 지정하면 required 속성이 의미가 없음
  - 값이 없더라도 기본값이 지정되므로 결국 무조건 값이 존재하게 됨

## 요청 파라미터를 Map으로 조회하기
```java
@RequestMapping("/request-param-map")
@ResponseBody
public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
    log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
    return "ok";
}
```
- 파라미터를 Map으로 받아서 조회할 수 있음.
- 동일 파라미터에 대해, 여러개의 값이 존재할 경우 `MultiValueMap`을 사용
  - 이 경우 get("파라미터명")을 통해 값을 꺼낼 경우 배열로 꺼낼 수 있음

</div>
</details>

---