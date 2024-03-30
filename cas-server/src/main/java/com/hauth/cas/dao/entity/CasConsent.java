package com.hauth.cas.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hundanli
 * @since 2024-03-27
 */
@TableName("cas_consent")
public class CasConsent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 到期时间
     */
    private Integer expireTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "CasConsent{" +
            "id = " + id +
            ", clientId = " + clientId +
            ", accountId = " + accountId +
            ", createTime = " + createTime +
            ", expireTime = " + expireTime +
        "}";
    }
}
