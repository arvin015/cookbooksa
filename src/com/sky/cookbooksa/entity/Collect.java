package com.sky.cookbooksa.entity;

import org.json.JSONObject;

public class Collect {

	private int id;
	private int dishId;
	private int userId;
	private int tagerUserId;
	private String dishName;
	private String userNick;
	private String mainPic;

	public Collect(){}

	public Collect(JSONObject json){
		this.id = json.optInt("id");
		this.userId = json.optInt("user_id");
		this.dishId = json.optInt("dish_id");
		this.tagerUserId = json.optInt("targetuser_id");
		this.dishName = json.optString("name");
		this.userNick = json.optString("nick");
		this.mainPic = json.optString("mainpic");
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDishId() {
		return dishId;
	}
	public void setDishId(int dishId) {
		this.dishId = dishId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getTagerUserId() {
		return tagerUserId;
	}
	public void setTagerUserId(int tagerUserId) {
		this.tagerUserId = tagerUserId;
	}
	public String getDishName() {
		return dishName;
	}
	public void setDishName(String dishName) {
		this.dishName = dishName;
	}
	public String getUserNick() {
		return userNick;
	}
	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}
	public String getMainPic() {
		return mainPic;
	}
	public void setMainPic(String mainPic) {
		this.mainPic = mainPic;
	}

}
