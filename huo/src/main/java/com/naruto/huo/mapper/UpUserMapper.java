package com.naruto.huo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.naruto.huo.model.UpUser;

@Mapper
public interface UpUserMapper{

	Integer selectAll();

	List<UpUser> selectSubject();

}
