package com.naruto.huo.model;

import java.sql.Date;

public class Video {
	private Integer id;
	
	private String videoName;
	
	private Date createTime;
	
	private String remark;
	
	private String yunUrl;
	
	private String coverPic;

	public String getCoverPic() {
		return coverPic;
	}

	public void setCoverPic(String coverPic) {
		this.coverPic = coverPic;
	}

	public String getYunUrl() {
		return yunUrl;
	}

	public void setYunUrl(String yunUrl) {
		this.yunUrl = yunUrl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
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
