package com.ttasjwi.springmvc.basic.request;

import com.ttasjwi.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        log.info("username={}, age={}", username, age);

        response.getWriter().write("ok");
    }

    @RequestMapping("/request-param-v2")
    @ResponseBody
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberAge) {
        log.info("username={}, age={}", memberName, memberAge);
        return "ok";
    }

    @RequestMapping("/request-param-v3")
    @ResponseBody
    public String requestParamV3(
            @RequestParam String username,
            @RequestParam int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @RequestMapping("/request-param-v4")
    @ResponseBody
    public String requestParamV4(String username, int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @RequestMapping("/request-param-required")
    @ResponseBody
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age) {
        // int age <- null이 들어갈 수 없음

        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @RequestMapping("/request-param-default")
    @ResponseBody
    public String requestParamDefault(
            @RequestParam(defaultValue = "guest") String username,
            @RequestParam(defaultValue = "-1") int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }

    @RequestMapping("/request-param-map")
    @ResponseBody
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
        return "ok";
    }

    @RequestMapping("/model-attribute-v1")
    @ResponseBody
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        //log.info("HelloData={}", helloData);
        return "ok";
    }

    @RequestMapping("/model-attribute-v2")
    @ResponseBody
    public String modelAttributeV2(HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        //log.info("HelloData={}", helloData);
        return "ok";
    }
}
