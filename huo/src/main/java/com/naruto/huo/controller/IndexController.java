package com.naruto.huo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.naruto.huo.model.KyUser;
import com.naruto.huo.service.LoginService;
import org.apache.axis.client.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.naruto.huo.model.Music;
import com.naruto.huo.model.Video;
import com.naruto.huo.service.VideoService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

	private String KEYINGYUN = "http://106.15.45.177:8080/";

	private String VIDEO = "video/";

	private String PICTURE = "pic/";
	
	private String MUSIC = "music/";

	@Autowired
	private VideoService videoService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/videoList")
	@ResponseBody
	public List<Video> videoList(String videoName) {
		List<Video> listVideo = videoService.getAllVideo(videoName);
		return listVideo;
	}

	/**
	 * 云路径用video
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/goVideo")
	public ModelAndView goVideo(Integer id) {
		Video video = videoService.getVideoUrl(id);
	/*	Integer count = jdbcTemplate.queryForObject("select count(1) from video where id = ?",Integer.class,new Object[]{id});
		System.out.println("count:"+count);*/
		String url = KEYINGYUN + VIDEO + video.getYunUrl();
		ModelAndView mav = new ModelAndView("goVideo");
		mav.addObject("url", url);
		mav.addObject("videoName",video.getVideoName());
		return mav;
	}
	
	@RequestMapping(value = "/music")
	public ModelAndView music() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("music");
		return mv;
	}
	
	@RequestMapping(value = "/getMusicAll")
	@ResponseBody
	public List<Music> getMusicAll() {
		List<Music> listMusic = videoService.getMusicAll();
		return listMusic;
	}

	//图片
	@RequestMapping(value = "/picture")
	public ModelAndView picture() {
		ModelAndView mv = new ModelAndView("picture");
		return mv;
	}
}
