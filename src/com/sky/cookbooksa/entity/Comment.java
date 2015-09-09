package com.sky.cookbooksa.entity;

import com.sky.cookbooksa.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Comment {

    private int commentId;
    private String commentContent;
    private String commentTime;
    private int dishId;
    private int userId;
    private String userNick;
    private int agreeNum;
    private String userPic;

    private ArrayList<Reply> replies;

    public Comment() {
    }

    public Comment(int commentId, String commentContent,
                   String commentTime, int agreeNum, String userPic, String userNick) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.commentTime = StringUtil.isEmpty(commentTime) ? "" : StringUtil.convertTimeStumpToDate(commentTime);
        this.agreeNum = agreeNum;
        this.userPic = userPic;
        this.userNick = userNick;

        replies = new ArrayList<Reply>();
    }

    public Comment(JSONObject obj) {
        this.commentId = obj.optInt("comment_id");
        this.commentContent = obj.optString("comment_content");
        this.commentTime = StringUtil.isEmpty(obj.optString("comment_time")) ? "" :
                StringUtil.convertTimeStumpToDate(obj.optString("comment_time"));
        this.dishId = obj.optInt("dish_id");
        this.agreeNum = obj.optInt("agree_num");

        JSONObject json = obj.optJSONObject("user");

        if (json != null) {
            this.userId = json.optInt("user_id");
            this.userPic = json.optString("user_pic");
            this.userNick = json.optString("nick");
        } else {
            this.userId = -1;
            this.userPic = "tourist";
            this.userNick = "匿名游客";
        }

        replies = new ArrayList<Reply>();

        JSONArray arr = null;
        try {
            arr = obj.optJSONArray("reply");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.optJSONObject(i);
                if (object != null) {
                    replies.add(new Reply(object));
                }
            }
        }

    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        if (!StringUtil.isEmpty(commentTime)) {
            this.commentTime = StringUtil.convertTimeStumpToDate(commentTime);
        } else {
            this.commentTime = commentTime;
        }
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

    public int getAgreeNum() {
        return agreeNum;
    }

    public void setAgreeNum(int agreeNum) {
        this.agreeNum = agreeNum;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setReplies(ArrayList<Reply> replies) {
        this.replies = replies;
    }

    public ArrayList<Reply> getReplies() {
        return replies;
    }
}
