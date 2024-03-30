package com.hauth.cas.controller;

import com.hauth.cas.dao.service.ICasAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/30 23:56
 */
@Slf4j
@RestController
@RequestMapping("/cas")
public class CasUserController {

    @Autowired
    ICasAccountService casAccountService;

    @PostMapping("register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        log.info("register user: {}", username);
        casAccountService.registerUser(username, password);
        return "ok";
    }

}
