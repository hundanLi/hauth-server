package com.hauth.auth.dao.service.impl;

import com.hauth.auth.dao.entity.AuthUser;
import com.hauth.auth.dao.mapper.AuthUserMapper;
import com.hauth.auth.dao.service.IAuthUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hundanli
 * @since 2024-03-08
 */
@Service
public class AuthUserServiceImpl extends ServiceImpl<AuthUserMapper, AuthUser> implements IAuthUserService {

}
