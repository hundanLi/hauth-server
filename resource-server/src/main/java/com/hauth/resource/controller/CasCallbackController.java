package com.hauth.resource.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 17:04
 */
@Slf4j
@RestController
public class CasCallbackController {

    @GetMapping("casCallback")
    public String callback(@RequestParam("ticket") String ticket) {
        log.info("receive callback from cas-sever, ticket={}", ticket);
        return "ok";
    }
}
