package com.hauth.cas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author hundanli
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse {
    AuthenticationSuccess authenticationSuccess;
    AuthenticationFailure authenticationFailure;
}