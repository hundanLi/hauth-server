package com.hauth.cas.oauth2.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/20 13:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessToken {
    private String accessToken;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private Integer expiresIn;

}
