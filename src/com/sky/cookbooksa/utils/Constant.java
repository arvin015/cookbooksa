package com.sky.cookbooksa.utils;

public class Constant {

	public static final String DIR = "http://www.skycookbook.sinaapp.com/Public/Uploads/";//远程

	//	public static String DIR = "http://192.168.23.106:8081/Public/Uploads/";//本地

	private static String httpip = "http://www.skycookbook.sinaapp.com/";//远程

	//	private static String httpip = "http://192.168.23.106:8081/";//本地

	public static String IMAGE_DIR = "http://www.skycookbook.sinaapp.com/Public/Images/";//远程

	//	public static String IMAGE_DIR = "http://192.168.23.106:8081/Public/Images/";//本地

	public static String url_dishlist = httpip + "index.php/Dish/page_list";

	public static String url_dishdetail = httpip + "index.php/Dish/dish_detail";

	public static String url_dishsearch = httpip + "index.php/Dish/dish_search";

	public static String url_getdishsbysearchcount = httpip + "index.php/Dish/getDishsBySearchCount";

	public static String url_getdishsbyclassify = httpip + "index.php/Dish/getDishByClassify";


	//UserInfo
	public static String url_iscollect = httpip + "index.php/UserInfo/isCollect";

	public static String url_dishcollect = httpip + "index.php/UserInfo/collectDish";

	public static String url_userregister = httpip + "index.php/UserInfo/register";

	public static String url_userisexist = httpip + "index.php/UserInfo/isUserExist";

	public static String url_userlogin = httpip + "index.php/UserInfo/userLogin";

	public static String url_getuserinfo = httpip + "index.php/UserInfo/getUserInfo";

	public static String url_userupdate = httpip + "index.php/UserInfo/userUpdate";


	//Activity
	public static String url_activityList = httpip + "index.php/Activity/activityList";

	//Comment
	public static String url_publishcomment = httpip + "index.php/Comment/publishComment";

	public static String url_commentagreenumincrease = httpip + "index.php/Comment/commentAgreeNumIncrease";

	public static String url_getallcommentsbydish = httpip + "index.php/Comment/getAllCommentsByDish";

	public static String url_publishreply = httpip + "index.php/Comment/publishReply";

	//Footprint
	public static String url_getallfootbyuserid = httpip + "index.php/Footprint/getAllFootByUserId";
	public static String url_deletefootbyfootid = httpip + "index.php/Footprint/deleteFootByFootId";

	//Collect
	public static String url_getalllovedishbyuserid = httpip + "index.php/Collect/getAllLoveDishByUserId";
	public static String url_getalllovepersonbyuserid = httpip + "index.php/Collect/getAllLovePersonByUserId";

	//Upload
	public static String url_uploadPhoto = httpip + "index.php/Upload/upload";

	//Message
	public static String url_getnewmessagesbyuserid = httpip + "index.php/Message/getNewMessagesByUserId";
	public static String url_getnewmessagesnumbyuserid = httpip + "index.php/Message/getNewMessagesNumByUserId";
	public static String url_updatemessagestate = httpip + "index.php/Message/updateMessageState";
	public static String url_deletemessagebymsgid = httpip + "index.php/Message/deleteMessageByMsgId";

}
