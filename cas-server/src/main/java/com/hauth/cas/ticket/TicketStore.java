package com.hauth.cas.ticket;

import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 10:06
 */
public interface TicketStore {

    /**
     * 获取TGT
     *
     * @param sessionId 会话id
     * @return TGT
     */
    String getTicketGrantTicket(String sessionId);

    /**
     * 存储TGT
     *
     * @param sessionId 会话id
     * @param tgt       TGT
     */
    void setTicketGrantTicket(String sessionId, String tgt);


    /**
     * 获取ServiceTicket
     *
     * @param tgt TGT
     * @return ST列表
     */
    List<String> getServiceTickets(String tgt);

    /**
     * 存储ST
     *
     * @param st  ST
     * @param tgt TGT
     * @param service 关联服务
     */
    void addServiceTicket(String st, String tgt, String service);


    /**
     * 获取关联服务
     * @param st ST
     * @return 关联服务
     */
    String getRelatedService(String st);

    /**
     * 删除serviceTicket
     * @param st ST
     */
    void invalidateServiceTicket(String st);

    /**
     * 销毁TGT
     * @param sessionId 会话id
     * @param tgt TGT
     */
    void invalidateTicketGrantTicket(String sessionId, String tgt);

    /**
     * 获取登录用户
     * @param st ST
     * @return user
     */
    String retrieveUser(String st);

    /**
     * 绑定登录用户
     * @param st ST
     * @param user 用户名
     */
    void bindUser(String st, String user);
}
