package com.hauth.cas.oauth2.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/22 13:36
 */
@Data
@Accessors(chain = true)
public class CodeChallenge {

    private String codeChallenge;
    private String codeChallengeMethod;

}
