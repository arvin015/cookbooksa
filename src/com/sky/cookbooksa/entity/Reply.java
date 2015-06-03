package com.sky.cookbooksa.entity;

import org.json.JSONObject;

import com.sky.cookbooksa.utils.StringUtil;

public class Reply {

	private int replyId;
	private String replyContent;
	private String replyTime;
	private int userId;
	private String userNick;
	private int commentId;

	public Reply(){}

	public Reply(String userNick, String replyContent, String replyTime){
		this.userNick = userNick;
		this.replyContent = replyContent;
		this.replyTime = StringUtil.isEmpty(replyTime)?"":StringUtil.convertTimeStumpToDate(replyTime);
	}

	public Reply(int replyId, String replyContent, String replyTime, int userId, 
			String userNick, int commentId){
		this.replyId = replyId;
		this.replyContent = replyContent;
		this.replyTime = StringUtil.isEmpty(replyTime)?"":StringUtil.convertTimeStumpToDate(replyTime);
		this.userId = userId;
		this.userNick = userNick;
		this.commentId = commentId;
	}

	public Reply(JSONObject obj){
		this.replyId = obj.optInt("reply_id");
		this.replyContent = obj.optString("reply_content");
		this.replyTime = StringUtil.isEmpty(obj.optString("reply_time"))?"":
			StringUtil.convertTimeStumpToDate(obj.optString("reply_time"));
		this.userId = obj.optInt("user_id");
		this.userNick = obj.optString("user_nick");
		this.commentId = obj.optInt("comment_id");
	}

	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getReplyTime() {
		return replyTime;
	}
	public void setReplyTime(String replyTime) {
		this.replyTime = StringUtil.isEmpty(replyTime)?"":StringUtil.convertTimeStumpToDate(replyTime);
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserNick() {
		return userNick;
	}
	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}


}
