package com.naruto.huo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.naruto.huo.model.Music;
import com.naruto.huo.model.Video;

@Mapper
public interface VideoMapper{

	List<Video> getAllVideo(@Param("videoName")String videoName);

	Video getVideoUrl(Integer id);

	List<Music> getMusicAll();

}
