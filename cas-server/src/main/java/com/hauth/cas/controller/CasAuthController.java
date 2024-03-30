package com.hauth.cas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hauth.cas.auth.Authentication;
import com.hauth.cas.auth.AuthenticationManager;
import com.hauth.cas.auth.impl.UserPasswordAuthentication;
import com.hauth.cas.auth.ticket.TicketGenerator;
import com.hauth.cas.auth.ticket.TicketStore;
import com.hauth.cas.constant.AuthenticateConstant;
import com.hauth.cas.constant.ErrorCodeConstant;
import com.hauth.cas.dto.ServiceValidateFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/14 16:15
 */
@Slf4j
@RestController
@RequestMapping("/cas")
public class CasAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TicketStore ticketStore;

    @Autowired
    private TicketGenerator ticketGenerator;

    private final ObjectMapper jsonMapper = new ObjectMapper();


    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*", methods = RequestMethod.GET)
    @GetMapping("login")
    public String login(HttpServletResponse response,
                        HttpServletRequest request,
                        @RequestParam(value = "service", required = false) String service,
                        @RequestParam(value = "redirect", defaultValue = "true") Boolean redirect) throws IOException {
        if (authenticationManager.hasLogin(request)) {
            Authentication authentication = (Authentication) request.getSession(false).getAttribute(AuthenticateConstant.PRINCIPAL);
            return grantTicketAndRedirect(service, true, response, request, authentication);
        } else {
            HttpSession httpSession = request.getSession(true);
            if (StringUtils.hasText(service)) {
                httpSession.setAttribute(AuthenticateConstant.SERVICE, service);
            }
            if (redirect != null) {
                httpSession.setAttribute(AuthenticateConstant.REDIRECT, redirect);
            }
            renderCasLoginPage(response);
            return "redirect to login";
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.HEAD})
    @PostMapping("login")
    public String formLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam(value = "service", required = false) String service,
                            @RequestParam(value = "redirect", defaultValue = "true") Boolean redirect,
                            HttpServletResponse response,
                            HttpServletRequest request) throws IOException {
        if (authenticationManager.hasLogin(request)) {
            return "You have login successfully!";
        }
        Authentication authentication = new UserPasswordAuthentication(username, password);
        if (authenticationManager.authenticate(authentication).isAuthenticated()) {
            return grantTicketAndRedirect(service, redirect, response, request, authentication);
        } else {
            return "Invalid credentials!";
        }
    }


    @RequestMapping(path = "logout", method = {RequestMethod.POST, RequestMethod.GET})
    public String logout(@RequestParam(value = "service", required = false) String service,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        if (StringUtils.hasText(service)) {
            redirectToService(service, null, response);
        }
        return "Logout successfully!";

    }

    @GetMapping(path = {"serviceValidate", "p3/serviceValidate"})
    public String serviceValidate(@RequestParam("service") String service,
                                  @RequestParam("ticket") String ticket,
                                  @RequestParam(value = "format", defaultValue = "XML") String format) throws Exception {
        Authentication authentication = ticketStore.retrieveAuthentication(ticket);
        String relatedService = ticketStore.getRelatedService(ticket);
        ticketStore.invalidateServiceTicket(ticket);
        ServiceValidateFactory result;
        if (relatedService == null) {
            result = ServiceValidateFactory.fail(ErrorCodeConstant.INVALID_TICKET, " Service Ticket is invalid: " + ticket);
        } else if (!Objects.equals(relatedService, service)) {
            result = ServiceValidateFactory.fail(ErrorCodeConstant.INVALID_SERVICE, " Service is invalid: " + service);
        } else {
            result = ServiceValidateFactory.success(authentication.getPrincipal(), authentication.getAttributes());
        }
        if (AuthenticateConstant.XML.equals(format)) {
            String xml = result.serializeAsXml();
            log.info("service validate xml response, user:{}, xml:\n {}", authentication.getPrincipal(), xml);
            return xml;
        } else {
            jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = jsonMapper.writeValueAsString(result.getServiceResponse());
            log.info("service validate json response, user:{}, json:\n {}", authentication.getPrincipal(), json);
            return json;
        }
    }

    @GetMapping("/redirect")
    public void redirect(@RequestParam(value = "service", required = false) String service,
                         HttpServletResponse response) throws IOException {
        if (StringUtils.hasText(service)) {
            response.sendRedirect(service);
        } else {
            response.sendRedirect("/");
        }
    }

    private String grantTicketAndRedirect(String service, Boolean redirect,
                                          HttpServletResponse response, HttpServletRequest request,
                                          Authentication authentication) throws IOException {
        HttpSession session = request.getSession(true);
        if (!StringUtils.hasText(service)) {
            service = (String) session.getAttribute(AuthenticateConstant.SERVICE);
            request.getSession().removeAttribute(AuthenticateConstant.SERVICE);
        }
        if (redirect == null) {
            redirect = Boolean.parseBoolean(session.getAttribute(AuthenticateConstant.REDIRECT) + "");
            request.getSession().removeAttribute(AuthenticateConstant.REDIRECT);
        }
        String ticketGrantTicket = authenticationManager.getTicketGrantCookie(request);
        if (ticketGrantTicket == null) {
            ticketGrantTicket = ticketGenerator.generateTicketGrantTicket();
            Cookie cookie = new Cookie(AuthenticateConstant.COOKIE_TGC, ticketGrantTicket);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            String sessionId = session.getId();
            session.setAttribute(AuthenticateConstant.PRINCIPAL, authentication);
            ticketStore.setTicketGrantTicket(sessionId, ticketGrantTicket);
        }

        if (StringUtils.hasText(service)) {
            String serviceTicket = ticketGenerator.generateServiceTicket(ticketGrantTicket);
            ticketStore.addServiceTicket(serviceTicket, ticketGrantTicket, service);
            if (Objects.nonNull(authentication)) {
                ticketStore.bindAuthentication(serviceTicket, authentication);
            }
            if (redirect) {
                redirectToService(service, serviceTicket, response);
                return "Redirect successfully: " + service;
            } else {
                return "Login successfully, ticket=" + serviceTicket;
            }
        } else {
            return "Login successfully: " + authentication.getPrincipal();
        }
    }


    private void renderCasLoginPage(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/login.html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private void redirectToService(String serviceUrl, String serviceTicket, HttpServletResponse response) throws IOException {
        String redirectUrl = serviceUrl;
        if (StringUtils.hasText(serviceTicket)) {
            if (serviceUrl.contains("?")) {
                redirectUrl = serviceUrl + "&ticket=" + serviceTicket;
            } else {
                redirectUrl = serviceUrl + "?ticket=" + serviceTicket;
            }
        }
        log.info("send redirect to serviceUrl: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
