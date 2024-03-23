package com.hauth.cas.oauth2.impl;

import com.hauth.cas.oauth2.TokenStorage;
import com.hauth.cas.oauth2.config.OAuth20Properties;
import com.hauth.cas.oauth2.dto.UserProfile;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/23 14:55
 */
@Slf4j
public abstract class AbstractTokenStorage implements TokenStorage {

    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();

    @Autowired
    private OAuth20Properties oAuth20Properties;

    @Override
    public void saveAccessToken(String accessToken, UserProfile userProfile) {
        saveAccessTokenInternal(accessToken, userProfile);
        Duration tokenTimeout = oAuth20Properties.getTokenTimeout();
        hashedWheelTimer.newTimeout(new TokenTimerTask(accessToken, this::removeAccessToken), tokenTimeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveRefreshToken(String refreshToken, UserProfile userProfile) {
        saveRefreshTokenInternal(refreshToken, userProfile);
        Duration tokenTimeout = oAuth20Properties.getRefreshTimeout();
        hashedWheelTimer.newTimeout(new TokenTimerTask(refreshToken, this::removeRefreshToken), tokenTimeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static class TokenTimerTask implements TimerTask {
        private final String token;
        private final Consumer<String> tokenConsumer;

        public TokenTimerTask(String token, Consumer<String> tokenConsumer) {
            this.token = token;
            this.tokenConsumer = tokenConsumer;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            log.warn("Token {} timeout, remove it", token);
            tokenConsumer.accept(token);
        }
    }

    /**
     * 保存accessToken，子类实现
     *
     * @param accessToken token
     * @param userProfile 用户信息
     */
    protected abstract void saveAccessTokenInternal(String accessToken, UserProfile userProfile);

    /**
     * 保存refreshToken，子类实现
     *
     * @param refreshToken refreshToken
     * @param userProfile  用户信息
     */
    protected abstract void saveRefreshTokenInternal(String refreshToken, UserProfile userProfile);

}
