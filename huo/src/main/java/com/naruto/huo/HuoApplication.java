package com.naruto.huo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 这里，启用定时任务
public class HuoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(HuoApplication.class, args);
		
	}
	@Override
	protected SpringApplicationBuilder configure (SpringApplicationBuilder builder){
		return builder.sources(HuoApplication.class);
	}
}
