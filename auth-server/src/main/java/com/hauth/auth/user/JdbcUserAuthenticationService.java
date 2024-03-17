package com.hauth.auth.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hauth.auth.dao.entity.AuthUser;
import com.hauth.auth.dao.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 10:25
 */
@Service
@ConditionalOnProperty(value = "auth.jdbc", havingValue = "true")
public class JdbcUserAuthenticationService implements UserAuthenticateService, UserDetailsService {


    @Autowired
    private IAuthUserService authUserService;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAuthenticationResult authenticate(UserAuthentication userAuthentication) {
        LambdaQueryWrapper<AuthUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AuthUser::getUsername, userAuthentication.getUsername());
        queryWrapper.last("limit 1");
        AuthUser authUser = authUserService.getOne(queryWrapper);
        if (authUser == null) {
            return new UserAuthenticationResult("invalid credential", null);
        }
        if (passwordEncoder.matches(userAuthentication.getPassword(), authUser.getPassword())) {
            return new UserAuthenticationResult("", authUser);
        } else {
            return new UserAuthenticationResult("invalid credential", null);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<AuthUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AuthUser::getUsername, username);
        queryWrapper.last("limit 1");
        AuthUser authUser = authUserService.getOne(queryWrapper);
        if (authUser == null) {
            throw new UsernameNotFoundException("use not found: " + username);
        }
        return User.withUsername(username)
                .password(authUser.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .build();
    }
}
