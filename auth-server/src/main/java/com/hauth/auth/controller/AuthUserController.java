package com.hauth.auth.controller;

import com.hauth.auth.dao.entity.AuthUser;
import com.hauth.auth.dao.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 15:20
 */
@RestController
@RequestMapping("authUser")
public class AuthUserController {

    @Autowired
    private IAuthUserService authUserService;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @PostMapping("register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password) {
        String encodedPassword = passwordEncoder.encode(password);
        AuthUser authUser = new AuthUser();
        authUser.setUserId("" + System.currentTimeMillis()/1000);
        authUser.setUsername(username);
        authUser.setPassword(encodedPassword);
        authUser.setMobile("");
        authUserService.save(authUser);
        return "ok";
    }
}
