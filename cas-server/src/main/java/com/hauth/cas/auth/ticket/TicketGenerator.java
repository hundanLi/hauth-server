package com.hauth.cas.auth.ticket;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 16:13
 */
public interface TicketGenerator {

    /**
     * 生成[令牌授权]令牌
     *
     * @return Ticket Grant ticket
     */
    String generateTicketGrantTicket();

    /**
     * 生成服务令牌
     *
     * @param tgt [令牌授权]令牌
     * @return 服务令牌
     */
    String generateServiceTicket(String tgt);
}
