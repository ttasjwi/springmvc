package com.ttasjwi.springmvc.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
