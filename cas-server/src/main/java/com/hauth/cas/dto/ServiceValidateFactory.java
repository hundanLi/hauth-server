package com.hauth.cas.dto;

import lombok.Data;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 19:24
 */
@Data
public class ServiceValidateFactory {

    private ServiceResponse serviceResponse;


    public static ServiceValidateFactory success(String user, Map<String, Object> attributes) {
        AuthenticationSuccess success = new AuthenticationSuccess();
        success.setUser(user);
        Map<String, Object> attributeMap = new HashMap<>(attributes.size());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            attributeMap.put("cas:" + entry.getKey(), entry.getValue());
        }
        success.setAttributes(attributeMap);
        ServiceResponse response = new ServiceResponse();
        response.setAuthenticationSuccess(success);
        ServiceValidateFactory serviceValidateFactory = new ServiceValidateFactory();
        serviceValidateFactory.setServiceResponse(response);
        return serviceValidateFactory;
    }

    public static ServiceValidateFactory fail(String errorCode, String description) {
        AuthenticationFailure failure = new AuthenticationFailure();
        failure.setCode(errorCode);
        failure.setDescription(description);
        ServiceResponse response = new ServiceResponse();
        response.setAuthenticationFailure(failure);
        ServiceValidateFactory serviceValidateFactory = new ServiceValidateFactory();
        serviceValidateFactory.setServiceResponse(response);
        return serviceValidateFactory;
    }


    public String serializeAsXml() throws Exception {
        if (serviceResponse.getAuthenticationSuccess() != null) {

            try (InputStream templateFile = new ClassPathResource("serviceValidateSuccess.xml").getInputStream()) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(templateFile);
                Element root = document.getRootElement();

                // 设置user
                Element userElement = root.element("authenticationSuccess").element("user");
                userElement.setText(serviceResponse.getAuthenticationSuccess().getUser());

                // 设置attributes
                Element attributesElement = root.element("authenticationSuccess").element("attributes");
                Map<String, Object> attributes = serviceResponse.getAuthenticationSuccess().getAttributes();
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    Element element = new DOMElement(key);
                    element.setText(value);
                    attributesElement.add(element);
                }
                // 返回
                return document.asXML();
            }

        } else {

            try (InputStream templateFile = new ClassPathResource("serviceValidateFailure.xml").getInputStream()) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(templateFile);
                Element root = document.getRootElement();

                // 设置code和description
                Element element = root.element("authenticationFailure");
                element.setText(serviceResponse.getAuthenticationFailure().getDescription());
                element.addAttribute("code", serviceResponse.getAuthenticationFailure().getCode());
                // 返回
                return document.asXML();
            }
        }
    }







}
