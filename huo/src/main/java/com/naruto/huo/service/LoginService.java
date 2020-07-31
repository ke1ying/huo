package com.naruto.huo.service;

import com.naruto.huo.model.KyUser;

public interface LoginService {
    KyUser queryUser(KyUser u);

    Integer insertKyUser(KyUser u);
}
