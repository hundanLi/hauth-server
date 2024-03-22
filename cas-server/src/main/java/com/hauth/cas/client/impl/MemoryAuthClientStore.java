package com.hauth.cas.client.impl;

import com.hauth.cas.client.AuthClient;
import com.hauth.cas.client.AuthClientProperties;
import com.hauth.cas.client.AuthClientStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:56
 */
@Slf4j
@Service
public class MemoryAuthClientStore implements AuthClientStore {

    @Autowired
    private AuthClientProperties authClientProperties;

    private final Map<String, AuthClient> authClientMap = new ConcurrentHashMap<>();

    @Override
    public void saveClient(AuthClient authClient) {
        authClientMap.put(authClient.getClientId(), authClient);
    }

    @Override
    public AuthClient findByClientId(String clientId) {
        return authClientMap.get(clientId);
    }


    @PostConstruct
    private void init() {
        for (AuthClient authClient : authClientProperties.getAuthClientList()) {
            this.saveClient(authClient);
        }
    }
}
