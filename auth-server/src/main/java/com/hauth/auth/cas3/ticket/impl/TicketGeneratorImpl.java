package com.hauth.auth.cas3.ticket.impl;

import com.hauth.auth.cas3.ticket.TicketGenerator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 16:18
 */
@Service
public class TicketGeneratorImpl implements TicketGenerator {

    private final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);

    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public String generateTicketGrantTicket() {
        SecureRandom secureRandom = this.secureRandom.get();
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        String tgtString = Base64.getUrlEncoder().encodeToString(bytes);
        return "TGT-" + tgtString;
    }

    @Override
    public String generateServiceTicket(String tgt) {
        SecureRandom secureRandom = this.secureRandom.get();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String randomString = Base64.getUrlEncoder().encodeToString(bytes);
        int seq = atomicInteger.getAndIncrement();
        return "ST-" + seq + "-" + randomString;
    }

}
