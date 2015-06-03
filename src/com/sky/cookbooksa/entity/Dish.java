package com.sky.cookbooksa.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Dish {

	private String id;
	private String name;
	private String desc;
	private String step;
	private String stepPic;
	private String mainPic;
	private String taste;
	private String during;
	private String userId;
	private String createTime;
	private String ingredientstr; //主料
	private String seasoningstr;   //调料
	private String style;       //菜系
	private String tip;			//小贴士

	private String[] ingredients, ingQuantity, seasoning, seaQuantity;

	protected String[] steps, step_imgs;

	private UserInfo userInfo;

	public Dish(){}

	public Dish(String result){
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(obj != null){
			this.id = obj.optString("dish_id");
			this.name = obj.optString("name");
			this.desc = obj.optString("desc");
			this.mainPic = obj.optString("mainpic");
			this.during = obj.optString("during");
			this.style = obj.optString("style");
		}
	}

	public Dish(JSONObject obj){

		if(obj != null){

			this.id = obj.optString("dish_id");
			this.name = obj.optString("name");
			this.desc = obj.optString("desc");
			this.step = obj.optString("step");
			this.stepPic = obj.optString("step_pic");
			this.mainPic = obj.optString("mainpic");
			this.taste = obj.optString("taste");
			this.during = obj.optString("during");
			this.createTime = obj.optString("create_time");
			this.ingredientstr = obj.optString("ingredients");
			this.seasoningstr = obj.optString("seasoning");
			this.style = obj.optString("style");
			this.tip = obj.optString("extra_tip");

			this.userInfo = new UserInfo(obj);

			parseStyle();
		}
	}

	private void parseStyle() {
		// TODO Auto-generated method stub
		//将食材解析成String数组
		String[] in = this.ingredientstr.split(";");
		ingredients = new String[in.length];
		ingQuantity = new String[in.length];
		for(int i = 0; i < in.length; i++){
			ingredients[i] = in[i].split(":")[0];
			try {
				ingQuantity[i] = in[i].split(":")[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ingQuantity[i] = "适量";
			}
		}
		String[] se = this.seasoningstr.split(";");
		seasoning = new String[se.length];
		seaQuantity = new String[se.length];
		for(int i = 0; i < se.length; i++){
			seasoning[i] = se[i].split(":")[0];
			try {
				seaQuantity[i] = se[i].split(":")[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				seaQuantity[i] = "适量";
			}
		}

		//将步骤解析成String数组
		steps = new String[this.step.length()];
		steps = this.step.split(";");

		step_imgs = new String[this.stepPic.length()];
		step_imgs = this.stepPic.split(";");
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getMainPic() {
		return mainPic;
	}
	public void setMainpic(String mainPic) {
		this.mainPic = mainPic;
	}
	public String getTaste() {
		return taste;
	}
	public void setTaste(String taste) {
		this.taste = taste;
	}
	public String getDuring() {
		return during;
	}
	public void setDuring(String during) {
		this.during = during;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getIngredientstr() {
		return ingredientstr;
	}

	public void setIngredientstr(String ingredientstr) {
		this.ingredientstr = ingredientstr;
	}

	public String getSeasoningstr() {
		return seasoningstr;
	}

	public void setSeasoningstr(String seasoningstr) {
		this.seasoningstr = seasoningstr;
	}

	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String[] getIngredients() {
		return ingredients;
	}

	public void setIngredients(String[] ingredients) {
		this.ingredients = ingredients;
	}

	public String[] getIngQuantity() {
		return ingQuantity;
	}

	public void setIngQuantity(String[] ingQuantity) {
		this.ingQuantity = ingQuantity;
	}

	public String[] getSeasoning() {
		return seasoning;
	}

	public void setSeasoning(String[] seasoning) {
		this.seasoning = seasoning;
	}

	public String[] getSeaQuantity() {
		return seaQuantity;
	}

	public void setSeaQuantity(String[] seaQuantity) {
		this.seaQuantity = seaQuantity;
	}

	public String[] getSteps() {
		return steps;
	}

	public void setSteps(String[] steps) {
		this.steps = steps;
	}

	public String[] getStep_imgs() {
		return step_imgs;
	}

	public void setStep_imgs(String[] step_imgs) {
		this.step_imgs = step_imgs;
	}


}
