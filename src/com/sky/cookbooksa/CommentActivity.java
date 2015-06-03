package com.sky.cookbooksa;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.sky.cookbooksa.adapter.ReplyAdapter;
import com.sky.cookbooksa.entity.Comment;
import com.sky.cookbooksa.entity.Reply;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonListView;
import com.sky.cookbooksa.widget.CommonScrollView;
import com.sky.cookbooksa.widget.CommonScrollView.OnBorderListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CommentActivity extends BaseActivity{

	private LinearLayout contentContainer;
	private ImageButton backBtn;
	private TextView title;
	private CommonScrollView scrollview;
	private EditText contentEdit;
	private Button submitBtn;
	private TextView emptyTip;

	private View loadingView;

	private LayoutInflater inflater;

	private ArrayList<Comment> comments;

	private String dishId;

	private String currentContent;

	private int totalCount = 0;

	private int currentPage = 1;

	private boolean isLoading = false;//是否正在加载

	private boolean isFirst = true;//是否是第一次提示加载完毕

	private boolean isComment = true;//是否评论

	private int currentCommentId = 0;//当前评论

	private ListView currentListView;//当前回复ListView
	private ArrayList<Reply> currentReplies;//当前回复集合

	protected enum AJAX_MODE{
		GET, AGREE, PUBLISH, PUBLISHREPLY
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);

		dishId = getIntent().getExtras().getString("dishId");

		loading("正在加载数据...");

		init();
	}

	@SuppressLint("UseSparseArrays")
	private void init(){

		inflater = LayoutInflater.from(context);

		comments = new ArrayList<Comment>();

		contentContainer = (LinearLayout) findViewById(R.id.content_container);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		scrollview = (CommonScrollView) findViewById(R.id.comment_scrollview);
		contentEdit = (EditText) findViewById(R.id.comment_edit);
		submitBtn = (Button) findViewById(R.id.submit_btn);
		emptyTip = (TextView) findViewById(R.id.empty_tip);

		//contentEdit进入无法获取焦点
		getFouse(submitBtn);

		setTitleText(totalCount);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});

		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String content = contentEdit.getText().toString().trim();
				currentContent = content;

				if(StringUtil.isEmpty(content)){
					ToastUtil.toastShort(context, "内容不能为空！");
					return;
				}

				toggleKeyboard(contentEdit, false);//close keyboard
				contentEdit.setText("");

				loading("正在提交...");

				AjaxParams params = new AjaxParams();
				params.put("content", content);
				params.put("userId", "0");

				if(isComment){

					params.put("dishId", dishId);

					fh.post(Constant.url_publishcomment, params, 
							new MyAjaxCallback(AJAX_MODE.PUBLISH));
				}else{

					params.put("commentId", currentCommentId + "");
					params.put("userNick", "arvin");

					fh.post(Constant.url_publishreply, params, new MyAjaxCallback(AJAX_MODE.PUBLISHREPLY));
				}
			}
		});

		scrollview.setOnBorderListener(new OnBorderListener() {

			@Override
			public void onTop() {//scroll top
				// TODO Auto-generated method stub

			}

			@Override
			public void onBottom() {//scroll bottom
				// TODO Auto-generated method stub
				scrollToBottomHandle();
			}

			@Override
			public void scroll() {
				// TODO Auto-generated method stub
				if(!isComment){
					isComment = true;
					contentEdit.setHint("说点什么吧？");
					submitBtn.setText("发表");
				}

				toggleKeyboard(contentEdit, false);
			}
		});

		loadData();
	}

	private void loadData() {
		// TODO Auto-generated method stub

		AjaxParams params = new AjaxParams();
		params.put("dishId", dishId);
		params.put("page", currentPage + "");

		fh.get(Constant.url_getallcommentsbydish, params, new MyAjaxCallback(AJAX_MODE.GET));
	}

	private TextView currentTextView;

	private View createView(final Comment comment){

		final View view = inflater.inflate(R.layout.comment_item, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.user_pic);
		TextView userNick = (TextView) view.findViewById(R.id.user_nick);
		TextView content = (TextView) view.findViewById(R.id.comment_content);
		TextView time = (TextView) view.findViewById(R.id.comment_time);
		final TextView agreeNum = (TextView) view.findViewById(R.id.agree_num);
		Button agreeBtn = (Button) view.findViewById(R.id.agree_btn);
		Button replyBtn = (Button) view.findViewById(R.id.reply_btn);
		final CommonListView listView = (CommonListView) view.findViewById(R.id.reply_listview);

		agreeNum.setText(comment.getAgreeNum() + "");
		content.setText(comment.getCommentContent());
		time.setText(comment.getCommentTime());
		userNick.setText(comment.getUserNick());

		fb.configLoadingImage(R.drawable.avatar_default);
		fb.configLoadfailImage(R.drawable.avatar_default);
		fb.display(imageView, Constant.DIR + comment.getUserPic());

		if(comment.getReplies().size() > 0){
			listView.setVisibility(View.VISIBLE);
		}

		final ReplyAdapter replyAdapter = new ReplyAdapter(context, comment.getReplies());
		listView.setAdapter(replyAdapter);

		agreeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentTextView = agreeNum;

				AjaxParams params = new AjaxParams();
				params.put("commentId", comment.getCommentId() + "");

				fh.post(Constant.url_commentagreenumincrease, params, new MyAjaxCallback(AJAX_MODE.AGREE));
			}
		});

		replyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				scrollview.scrollTo(0, view.getTop());//滑动至当前操作view所在位置

				isComment = false;

				toggleKeyboard(contentEdit, true);
				contentEdit.setHint("回复评论");

				submitBtn.setText("回复");

				currentCommentId = comment.getCommentId();
				currentListView = listView;
				currentReplies = comment.getReplies();

				getFouse(contentEdit);
			}
		});

		return view;
	}

	//滑至底部Handle
	private void scrollToBottomHandle() {
		// TODO Auto-generated method stub
		if(!isLoading){//未正在加载更多

			if(contentContainer.getChildCount() < totalCount){//未加载完数据
				if(loadingView == null){
					loadingView = inflater.inflate(R.layout.loading, null);
				}

				contentContainer.addView(loadingView);

				scrollview.post(new Runnable() {//自动滑动至底部

					@Override
					public void run() {
						// TODO Auto-generated method stub
						scrollview.fullScroll(View.FOCUS_DOWN);
					}
				});

				currentPage++;

				loadData();

				isLoading = true;
			}else{//加载完所有数据
				if(isFirst){
					ToastUtil.toastShort(context, "数据加载完毕！");
					isFirst = false;
				}
			}
		}
	}

	private void getFouse(View view){
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.requestFocusFromTouch();
	}

	private void setTitleText(int totalCount){
		title.setText("评论(共"+totalCount+"条)");
	}

	//捕捉物理键操作事件
	//	@Override
	//	public boolean dispatchKeyEvent(KeyEvent event) {
	//		// TODO Auto-generated method stub
	//
	//		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
	//			if(!isComment){
	//				isComment = true;
	//				contentEdit.setHint("说点什么吧？");
	//			}
	//		}
	//
	//		return super.dispatchKeyEvent(event);
	//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	class MyAjaxCallback extends AjaxCallBack<Object>{

		private AJAX_MODE mode;

		public MyAjaxCallback(AJAX_MODE mode){
			this.mode = mode;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			// TODO Auto-generated method stub
			super.onFailure(t, errorNo, strMsg);

			if(mode == AJAX_MODE.GET){
				ToastUtil.toastShort(context, "获取评论列表失败="+strMsg);
				isLoading = false;
				loadMissed();

				contentContainer.removeView(loadingView);

			}else if(mode == AJAX_MODE.PUBLISH){
				ToastUtil.toastShort(context, "发布评论失败="+strMsg);
				loadMissed();
			}else if(mode == AJAX_MODE.AGREE){
				ToastUtil.toastShort(context, "点赞失败="+strMsg);
			}else if(mode == AJAX_MODE.PUBLISHREPLY){
				ToastUtil.toastShort(context, "回复评论失败="+strMsg);
				loadMissed();
			}
		}

		@Override
		public void onSuccess(Object t) {
			// TODO Auto-generated method stub
			super.onSuccess(t);

			JSONObject obj = null;
			try {
				obj = new JSONObject((String)t);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(obj != null){
				if(mode == AJAX_MODE.GET){

					loadMissed();
					isLoading = false;

					contentContainer.removeView(loadingView);//删除LoadingView

					int count = obj.optInt("count");
					totalCount = count;
					setTitleText(count);

					if(count < 1){
						emptyTip.setVisibility(View.VISIBLE);
						return;
					}

					JSONArray arr = null;
					try {
						arr = obj.optJSONArray("result");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(arr != null){

						for(int i = 0; i < arr.length(); i++){
							Comment comment = new Comment(arr.optJSONObject(i));

							contentContainer.addView(createView(comment));
							comments.add(comment);
						}
					}
				}else {
					String result = obj.optString("result");

					if("true".equals(result)){
						if(mode == AJAX_MODE.PUBLISH){

							loadMissed();

							if(emptyTip.getVisibility() == View.VISIBLE){
								emptyTip.setVisibility(View.GONE);
							}

							String time = obj.optString("time");
							int commentId = obj.optInt("commentId");
							ToastUtil.toastShort(context, "发布评论成功！");

							totalCount++;

							setTitleText(totalCount);

							Comment comment = new Comment(commentId, currentContent, time, 0, 
									Utils.userPic, Utils.userNick);

							contentContainer.addView(createView(comment), 0);

							scrollview.scrollTo(0, 0);//scroll to top

						}else if(mode == AJAX_MODE.PUBLISHREPLY){

							loadMissed();

							currentListView.setVisibility(View.VISIBLE);

							String time = obj.optString("time");

							if(currentListView.getAdapter() != null){

								currentReplies.add(new Reply("arvin", currentContent, time));

								((ReplyAdapter)currentListView.getAdapter()).notifyDataSetChanged();
							}

						}else{
							currentTextView.setText((Integer.parseInt(currentTextView.getText().toString())+1) + "");
						}
					}else{
						ToastUtil.toastShort(context, "异常情况！");
					}
				}
			}
		}
	}

}
