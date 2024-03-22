package com.hauth.cas.oauth2;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/18 19:57
 */
public interface AuthorizationCodeManager {

    /**
     * 生成code
     * @param clientId 客户端id
     * @return code
     */
    String generateCode(String clientId);

    /**
     * 校验code
     * @param clientId 客户端id
     * @param code code
     * @return 合法性
     */
    boolean checkCode(String clientId, String code);

    /**
     * 保存PKCE
     * @param clientId 客户端id
     * @param codeChallenge codeChallenge密文
     * @param codeChallengeMethod hash algo
     */
    void setCodeChallenge(String clientId, String codeChallenge, String codeChallengeMethod);

    /**
     * 校验PKCE
     * @param clientId 客户端id
     * @param codeVerifier codeChallenge明文
     * @return 合法性
     */
    boolean checkCodeVerifier(String clientId, String codeVerifier);

}
