package com.hauth.cas.dao.service;

import com.hauth.cas.dao.entity.CasAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hundanli
 * @since 2024-03-27
 */
public interface ICasAccountService extends IService<CasAccount> {

    /**
     *  注册用户
     *  @param username 用户名
     * @param password 密码
     */
    void registerUser(String username, String password);
}
