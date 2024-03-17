package com.hauth.cas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/16 22:57
 */
@RestController
public class IndexController {


    @GetMapping("/")
    public String index() {
        return "Hello, man!";
    }

}
