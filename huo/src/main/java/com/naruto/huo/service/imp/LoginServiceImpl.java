package com.naruto.huo.service.imp;

import com.naruto.huo.mapper.LoginMapper;
import com.naruto.huo.model.KyUser;
import com.naruto.huo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;


    @Override
    public KyUser queryUser(KyUser u) {
        return loginMapper.queryUser(u);
    }

    @Override
    public Integer insertKyUser(KyUser u) {
        return loginMapper.insertKyUser(u);
    }
}
