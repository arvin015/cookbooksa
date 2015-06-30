package com.sky.cookbooksa.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sky.cookbooksa.utils.StringUtil;

/**
 * 消息体
 * @author arvin.li
 *
 */
public class MessageInfo {

	private int id;
	private String createTime;
	private String title;
	private String content;
	private int read;//是否已读，0：未读，1：已读
	private int type;
	private int flag;

	public MessageInfo(){}

	public MessageInfo(String createTime, String title,
			String content, int type){
		this.createTime = StringUtil.convertimeStumpToDate2(createTime);
		this.title = title;
		this.content = content;
		this.type = type;
		this.flag = 0;
		this.read = 0;
	}

	public MessageInfo(JSONObject json){
		if(json == null){
			return;
		}

		id = json.optInt("msg_id");
		createTime = StringUtil.convertimeStumpToDate2(json.optString("msg_createtime"));
		title = json.optString("msg_title");
		content = json.optString("msg_content");
		type = json.optInt("msg_type");
		flag = json.optInt("msg_flag");
		read = json.optInt("msg_read");
	}

	public static List<MessageInfo> getMsgListFromJson(JSONArray arr){

		List<MessageInfo> msgList = new ArrayList<MessageInfo>();

		if(arr != null){
			for(int i = 0; i < arr.length(); i++){
				msgList.add(new MessageInfo(arr.optJSONObject(i)));
			}
		}

		return msgList;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = StringUtil.convertimeStumpToDate2(createTime);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

}
