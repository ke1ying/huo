package com.naruto.huo.service;

import java.util.List;

import com.naruto.huo.model.Music;
import com.naruto.huo.model.Video;

public interface VideoService {

	List<Video> getAllVideo(String videoName);

	Video getVideoUrl(Integer id);

	List<Music> getMusicAll();

}
