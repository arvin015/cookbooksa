package com.sky.cookbooksa.entity;

public class SRecord {

	private int id;
	private String content;
	private String createTime;

	public SRecord(){}
	
	public SRecord(String content, String createTime){
		this.content = content;
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


}
