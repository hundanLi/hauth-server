package com.hauth.resource.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 16:04
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessTokenDTO {

    private String accessToken;
    private String scope;
    private String tokenType;
    private Integer expiresIn;


}
