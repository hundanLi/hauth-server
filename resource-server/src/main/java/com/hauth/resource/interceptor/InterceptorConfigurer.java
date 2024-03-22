package com.hauth.resource.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/21 23:32
 */
@Configuration
public class InterceptorConfigurer implements WebMvcConfigurer {


    @Autowired
    private AuthenticateInterceptor authenticateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticateInterceptor)
                .addPathPatterns("/*");
    }
}
