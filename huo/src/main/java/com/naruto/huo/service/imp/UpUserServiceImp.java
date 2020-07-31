package com.naruto.huo.service.imp;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naruto.huo.mapper.UpUserMapper;
import com.naruto.huo.model.UpUser;
import com.naruto.huo.service.UpUserService;



@Service
public class UpUserServiceImp implements UpUserService{

	@Autowired
	private UpUserMapper upUserMapper;

	@Override
	public Integer selectAll() {
		
		return upUserMapper.selectAll();
	}

	@Override
	public List<UpUser> selectSubject() {
		
		return upUserMapper.selectSubject();
	}
}
