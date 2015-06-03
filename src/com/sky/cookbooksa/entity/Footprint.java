package com.sky.cookbooksa.entity;

import org.json.JSONObject;

import com.sky.cookbooksa.utils.StringUtil;

public class Footprint {

	private int footId;
	private String userId;
	private String dishId;
	private String dishName;
	private String footTime;

	public Footprint(String userId, String dishId, String dishName, String footTime){
		this.userId = userId;
		this.dishId = dishId;
		this.dishName = dishName;
		this.footTime = StringUtil.isEmpty(footTime)?"":StringUtil.convertimeStumpToDate2(footTime);
	}

	public Footprint(JSONObject obj){
		this.footId = obj.optInt("foot_id");
		this.userId = obj.optString("user_id");
		this.dishId = obj.optString("dish_id");
		this.dishName = obj.optString("dish_name");
		this.footTime = StringUtil.isEmpty(obj.optString("foot_time"))?"":
			StringUtil.convertimeStumpToDate2(obj.optString("foot_time"));
	}

	public int getFootId() {
		return footId;
	}
	public void setFootId(int footId) {
		this.footId = footId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	}
	public String getDishName() {
		return dishName;
	}
	public void setDishName(String dishName) {
		this.dishName = dishName;
	}
	public String getFootTime() {
		return footTime;
	}
	public void setFootTime(String footTime) {
		this.footTime = StringUtil.isEmpty(footTime)?"":StringUtil.convertimeStumpToDate2(footTime);
	}


}
