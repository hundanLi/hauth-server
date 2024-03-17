package com.hauth.auth.cas3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 19:24
 */
@Data
public class ServiceValidateDTO {

    private ServiceResponse serviceResponse;


    public static ServiceValidateDTO success(String user, Map<String, Object> attributes) {
        AuthenticationSuccess success = new AuthenticationSuccess();
        success.setUser(user);
        success.setAttributes(new HashMap<>(attributes));
        ServiceResponse response = new ServiceResponse();
        response.setAuthenticationSuccess(success);
        ServiceValidateDTO serviceValidateDTO = new ServiceValidateDTO();
        serviceValidateDTO.setServiceResponse(response);
        return serviceValidateDTO;
    }

    public static ServiceValidateDTO fail(String errorCode, String description) {
        AuthenticationFailure failure = new AuthenticationFailure();
        failure.setCode(errorCode);
        failure.setDescription(description);
        ServiceResponse response = new ServiceResponse();
        response.setAuthenticationFailure(failure);
        ServiceValidateDTO serviceValidateDTO = new ServiceValidateDTO();
        serviceValidateDTO.setServiceResponse(response);
        return serviceValidateDTO;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ServiceResponse {
        AuthenticationSuccess authenticationSuccess;
        AuthenticationFailure authenticationFailure;
    }

    @Data
    static class AuthenticationSuccess {
        String user;
        Map<String, Object> attributes;
    }

    @Data
    static class AuthenticationFailure {
        String code;
        String description;
    }

}
