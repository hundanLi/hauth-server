package com.hauth.cas.dao.service.impl;

import com.hauth.cas.dao.entity.CasAccount;
import com.hauth.cas.dao.mapper.CasAccountMapper;
import com.hauth.cas.dao.service.ICasAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hundanli
 * @since 2024-03-27
 */
@Service
public class CasAccountServiceImpl extends ServiceImpl<CasAccountMapper, CasAccount> implements ICasAccountService {


    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(String username, String password) {
        CasAccount casAccount = new CasAccount();
        casAccount.setAccountName(username);
        casAccount.setDisplayName(randomString());
        casAccount.setPassword(passwordEncoder.encode(password));
        this.save(casAccount);
    }


    private String randomString() {
        StringBuilder sb = new StringBuilder("user_");
        for (int i = 0; i < 8; i++) {
            sb.append((char) (Math.random() * 26 + 'a'));
        }
        return sb.toString();
    }
}
