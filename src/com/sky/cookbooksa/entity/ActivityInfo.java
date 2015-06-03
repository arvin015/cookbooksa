package com.sky.cookbooksa.entity;

import org.json.JSONObject;

public class ActivityInfo {

	private String actId;
	private String actIntro;
	private String actPic;
	private int actType;

	public ActivityInfo(){}

	public ActivityInfo(String actId, String actIntro, String actPic, int actType){
		this.actId = actId;
		this.actIntro = actIntro;
		this.actPic = actPic;
		this.actType = actType;
	}

	public ActivityInfo(JSONObject obj){
		this.actId = obj.optInt("act_id") + "";
		this.actIntro = obj.optString("act_intro");
		this.actPic = obj.optString("act_pic");
		this.actType = obj.optInt("act_type");
	}

	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getActIntro() {
		return actIntro;
	}
	public void setActIntro(String actIntro) {
		this.actIntro = actIntro;
	}
	public String getActPic() {
		return actPic;
	}
	public void setActPic(String actPic) {
		this.actPic = actPic;
	}
	public int getActType() {
		return actType;
	}
	public void setActType(int actType) {
		this.actType = actType;
	}


}
