package com.naruto.huo.model;

import java.sql.Date;

public class Music {
	private Integer id;
	
	private String musicName;
	
	private Date createTime;
	
	private String remark;

	private String musicPic;
	
	
	public String getMusicPic() {
		return musicPic;
	}

	public void setMusicPic(String musicPic) {
		this.musicPic = musicPic;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
