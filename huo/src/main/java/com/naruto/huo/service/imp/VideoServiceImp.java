package com.naruto.huo.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naruto.huo.mapper.VideoMapper;
import com.naruto.huo.model.Music;
import com.naruto.huo.model.Video;
import com.naruto.huo.service.VideoService;

@Service
public class VideoServiceImp implements VideoService{

	@Autowired
	private VideoMapper videoMapper;
	
	@Override
	public List<Video> getAllVideo(String videoName) {
		
		return videoMapper.getAllVideo(videoName);
	}

	@Override
	public Video getVideoUrl(Integer id) {
		
		return videoMapper.getVideoUrl(id);
	}


	@Override
	public List<Music> getMusicAll() {

		return videoMapper.getMusicAll();
	}

}
