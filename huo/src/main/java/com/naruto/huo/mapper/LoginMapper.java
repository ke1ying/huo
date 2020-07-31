package com.naruto.huo.mapper;

import com.naruto.huo.model.KyUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    KyUser queryUser(KyUser u);

    Integer insertKyUser(KyUser u);
}
