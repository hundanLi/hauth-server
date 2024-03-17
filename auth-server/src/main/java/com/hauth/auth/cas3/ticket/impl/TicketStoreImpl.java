package com.hauth.auth.cas3.ticket.impl;

import com.hauth.auth.cas3.ticket.TicketStore;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 10:38
 */
@Slf4j
@Service
@WebListener
public class TicketStoreImpl implements TicketStore, HttpSessionListener {

    private final Map<String, String> sessionTicketGrantTicketMap = new ConcurrentHashMap<>();

    private final Map<String, String> serviceTicketServiceMap = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> ticketGrantServiceTicketsMap = new ConcurrentHashMap<>();

    private final Map<String, String> serviceTicketUserMap = new ConcurrentHashMap<>();

    private final HashedWheelTimer wheelTimer = new HashedWheelTimer();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.info("session created, id={}", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        log.info("session destroyed, id={}", sessionId);
        invalidateTicketGrantTicket(sessionId, null);
    }

    @Override
    public String getTicketGrantTicket(String sessionId) {
        return sessionTicketGrantTicketMap.get(sessionId);
    }

    @Override
    public void setTicketGrantTicket(String sessionId, String tgt) {
        sessionTicketGrantTicketMap.put(sessionId, tgt);
    }

    @Override
    public List<String> getServiceTickets(String tgt) {
        return null;
    }

    @Override
    public void addServiceTicket(String st, String tgt, String service) {
        serviceTicketServiceMap.put(st, service);
        ticketGrantServiceTicketsMap.compute(tgt, (key, set) -> {
            return set == null ? new HashSet<>() : set;
        }).add(st);
        wheelTimer.newTimeout(new ServiceTicketExpireTask(st, tgt), 5, TimeUnit.MINUTES);

    }

    @Override
    public String getRelatedService(String st) {
        String service = serviceTicketServiceMap.get(st);
        if (service == null) {
            log.info("request st={}", st);
            for (Map.Entry<String, String> entry : serviceTicketServiceMap.entrySet()) {
                log.info("st={}, service={}", entry.getKey(), entry.getValue());
            }
        }
        return service;
    }

    @Override
    public void invalidateServiceTicket(String serviceTicket) {
        serviceTicketServiceMap.remove(serviceTicket);
        serviceTicketUserMap.remove(serviceTicket);
    }

    @Override
    public void invalidateTicketGrantTicket(String sessionId, String tgt) {
        String ticketGrantTicket = sessionTicketGrantTicketMap.remove(sessionId);
        ticketGrantServiceTicketsMap.remove(ticketGrantTicket);
        // todo Single Sign Out
    }

    @Override
    public String retrieveUser(String st) {
        return serviceTicketUserMap.get(st);
    }

    @Override
    public void bindUser(String st, String user) {
        serviceTicketUserMap.put(st, user);
    }

    class ServiceTicketExpireTask implements TimerTask {
        final String serviceTicket;
        final String ticketGrantTicket;

        public ServiceTicketExpireTask(String serviceTicket, String ticketGrantTicket) {
            this.serviceTicket = serviceTicket;
            this.ticketGrantTicket = ticketGrantTicket;
        }

        @Override
        public void run(Timeout timeout) {
            if (!timeout.isCancelled()) {
                serviceTicketServiceMap.remove(serviceTicket);
                serviceTicketUserMap.remove(serviceTicket);
                ticketGrantServiceTicketsMap.get(ticketGrantTicket).remove(serviceTicket);
            }
        }
    }
}
