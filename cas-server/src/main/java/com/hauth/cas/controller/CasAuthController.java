package com.hauth.cas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hauth.cas.constant.ErrorCodeConstant;
import com.hauth.cas.constant.SessionConstant;
import com.hauth.cas.dto.ServiceValidateDTO;
import com.hauth.cas.ticket.TicketGenerator;
import com.hauth.cas.ticket.TicketStore;
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
import java.util.HashMap;
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

    private static final String TGC = "TGC";
    private String user = "admin";
    private String pass = "123456";

    private String SERVICE = "service";
    private String REDIRECT = "redirect";

    private String JSON = "JSON";
    private String XML = "XML";


    @Autowired
    private TicketStore ticketStore;

    @Autowired
    private TicketGenerator ticketGenerator;

    private ObjectMapper jsonMapper = new ObjectMapper();

    private XmlMapper xmlMapper = new XmlMapper();

    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*", methods = RequestMethod.GET)
    @GetMapping("login")
    public String login(HttpServletResponse response,
                        HttpServletRequest request,
                        @RequestParam(value = "service", required = false) String service,
                        @RequestParam(value = "redirect", defaultValue = "true") Boolean redirect) throws IOException {
        if (hasLogin(request)) {
            String user = (String) request.getSession(false).getAttribute(SessionConstant.PRINCIPAL);
            return grantTicketAndRedirect(service, true, response, request, user);
        } else {
            HttpSession httpSession = request.getSession(true);
            if (StringUtils.hasText(service)) {
                httpSession.setAttribute(SERVICE, service);
            }
            if (redirect != null) {
                httpSession.setAttribute(REDIRECT, redirect);
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
        if (hasLogin(request)) {
            return "You have login successfully!";
        }
        if (user.equals(username) && pass.equals(password)) {
            return grantTicketAndRedirect(service, redirect, response, request, username);
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
            String sessionId = session.getId();
            String tgc = getTgc(request);
            ticketStore.invalidateTicketGrantTicket(sessionId, tgc);
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
                                  @RequestParam(value = "format", defaultValue = "XML") String format) throws JsonProcessingException {
        String user = ticketStore.retrieveUser(ticket);
        String relatedService = ticketStore.getRelatedService(ticket);
        ticketStore.invalidateServiceTicket(ticket);
        ServiceValidateDTO result;
        if (relatedService == null) {
            result = ServiceValidateDTO.fail(ErrorCodeConstant.INVALID_TICKET, " Service Ticket is invalid: " + ticket);
        } else if (!Objects.equals(relatedService, service)) {
            result = ServiceValidateDTO.fail(ErrorCodeConstant.INVALID_SERVICE, " Service is invalid: " + service);
        } else {
            result = ServiceValidateDTO.success(user, new HashMap<>(0));
        }
        if (XML.equals(format)) {
            return xmlMapper.writeValueAsString(result);
        } else {
            return jsonMapper.writeValueAsString(result);
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
                                          String user) throws IOException {
        HttpSession session = request.getSession(true);
        if (!StringUtils.hasText(service)) {
            service = (String) session.getAttribute(SERVICE);
            request.getSession().removeAttribute(SERVICE);
        }
        if (redirect == null) {
            redirect = Boolean.parseBoolean(session.getAttribute(REDIRECT) + "");
            request.getSession().removeAttribute(REDIRECT);
        }
        String ticketGrantTicket = getTgc(request);
        if (ticketGrantTicket == null) {
            ticketGrantTicket = ticketGenerator.generateTicketGrantTicket();
            Cookie cookie = new Cookie(TGC, ticketGrantTicket);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            String sessionId = session.getId();
            session.setAttribute(SessionConstant.PRINCIPAL, user);
            ticketStore.setTicketGrantTicket(sessionId, ticketGrantTicket);
        }

        if (StringUtils.hasText(service)) {
            String serviceTicket = ticketGenerator.generateServiceTicket(ticketGrantTicket);
            ticketStore.addServiceTicket(serviceTicket, ticketGrantTicket, service);
            if (StringUtils.hasText(user)) {
                ticketStore.bindUser(serviceTicket, user);
            }
            if (redirect) {
                redirectToService(service, serviceTicket, response);
                return "Redirect successfully: " + service;
            } else {
                return "Login successfully, ticket=" + serviceTicket;
            }
        } else {
            return "Login successfully!";
        }
    }

    private boolean hasLogin(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return false;
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), TGC)) {
                String ticketGrantTicket = ticketStore.getTicketGrantTicket(request.getSession(false).getId());
                if (ticketGrantTicket != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getTgc(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), TGC)) {
                String ticketGrantTicket = ticketStore.getTicketGrantTicket(request.getSession(false).getId());
                if (ticketGrantTicket != null) {
                    return ticketGrantTicket;
                }
            }
        }
        return null;
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
            redirectUrl = serviceUrl + "?ticket=" + serviceTicket;
        }
        log.info("send redirect to serviceUrl: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
