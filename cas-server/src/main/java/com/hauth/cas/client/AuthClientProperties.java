package com.hauth.cas.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/19 23:43
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.client")
public class AuthClientProperties {

    private List<AuthClient> authClientList;

}
