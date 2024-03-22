package com.hauth.cas.client;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:53
 */
public interface AuthClientStore {
    /**
     * 存储
     *
     * @param authClient 客户端
     */
    void saveClient(AuthClient authClient);

    /**
     * 查找客户端
     *
     * @param clientId 客户端id
     * @return 客户端
     */
    AuthClient findByClientId(String clientId);

}
