package com.sky.cookbooksa.entity;

import org.json.JSONObject;

public class UserInfo {

	private String uId;
	private String uNick;
	private String uPhoto;
	private String uSex;
	private String uRegisterTime;

	public UserInfo(){}

	public UserInfo(String uId, String uNick, String uPhoto, String uSex, String uRegisterTime){
		this.uId = uId;
		this.uNick = uNick;
		this.uPhoto = uPhoto;
		this.uSex = uSex;
		this.uRegisterTime = uRegisterTime;
	}

	public UserInfo(JSONObject obj){
		this.uId = obj.optString("user_id");
		this.uNick = obj.optString("nick");
		this.uPhoto = obj.optString("user_pic");
		this.uSex = obj.optString("sex");
		this.uRegisterTime = obj.optString("register_time");
	}

	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getuNick() {
		return uNick;
	}
	public void setuNick(String uNick) {
		this.uNick = uNick;
	}
	public String getuPhoto() {
		return uPhoto;
	}
	public void setuPhoto(String uPhoto) {
		this.uPhoto = uPhoto;
	}
	public String getuSex() {
		return uSex;
	}
	public void setuSex(String uSex) {
		this.uSex = uSex;
	}
	public String getuRegisterTime() {
		return uRegisterTime;
	}
	public void setuRegisterTime(String uRegisterTime) {
		this.uRegisterTime = uRegisterTime;
	}


}
