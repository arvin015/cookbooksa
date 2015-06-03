package com.sky.cookbooksa;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.MD5Util;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity{

	private Context context;

	private Button submitBtn, getCodeAgainBtn;
	private ImageButton back;
	private EditText phoneEdit, pwdEdit, pwdAgainEdit;
	private TextView titleName, registerTip, timerTip;

	private AlertDialog adialog;

	private SMSEventHandler eventHandler;

	private Timer timer;

	private String phoneStr;

	private int timeCounter = 60;//倒计时60秒

	private int GETCODE_FAIL = 111;
	private int CHECKCODE_FAIL = 112;
	private int GETCODE_SUCCESS = 001;
	private int CHECKCODE_SUCCESS = 002;
	private int NOTICE_TIME_CHANGE = 100;

	private SUBMIT_TYPE type = SUBMIT_TYPE.GETCODE;

	protected enum SUBMIT_TYPE{
		GETCODE, POSTCODE, POSTPWD
	};

	protected enum AJAX_VALUE{
		ISEXIST, REGISTER
	};

	private int isExistValue = -1;//手机号是否已注册标记，-1-异常，1-已注册，2-未注册

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == GETCODE_FAIL){

				ToastUtil.toastShort(context, "获取验证码失败！");
				submitBtn.setEnabled(true);

			}else if(msg.what == CHECKCODE_FAIL){

				ToastUtil.toastShort(context, "验证码输入错误！");

			}else if(msg.what == GETCODE_SUCCESS){//获取短信验证码成功

				submitBtn.setEnabled(true);
				submitBtn.setText("提交验证码");
				phoneEdit.setText("");
				registerTip.setText("请输入短信获取的验证码");

				type = SUBMIT_TYPE.POSTCODE;

			}else if(msg.what == CHECKCODE_SUCCESS){//验证码验证成功

				cancelTimer();

				pwdEdit.setVisibility(View.VISIBLE);
				pwdAgainEdit.setVisibility(View.VISIBLE);
				phoneEdit.setVisibility(View.GONE);
				registerTip.setVisibility(View.GONE);
				timerTip.setVisibility(View.GONE);

				submitBtn.setText("提交密码");

				type = SUBMIT_TYPE.POSTPWD;

			}else if(msg.what == NOTICE_TIME_CHANGE){

				setTimerTip();
				if(timeCounter <= 0){
					cancelTimer();
					timerTip.setVisibility(View.GONE);
					getCodeAgainBtn.setVisibility(View.VISIBLE);
					submitBtn.setEnabled(false);

					type = SUBMIT_TYPE.GETCODE;
				}
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		init();
	}

	//初始化
	private void init(){

		context = this;

		submitBtn = (Button) findViewById(R.id.register);
		submitBtn.setVisibility(View.VISIBLE);
		getCodeAgainBtn = (Button) findViewById(R.id.getcode_again);
		phoneEdit = (EditText) findViewById(R.id.phone_edit);
		pwdEdit = (EditText) findViewById(R.id.password_edit);
		pwdAgainEdit = (EditText) findViewById(R.id.password_edit_again);
		back = (ImageButton) findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		titleName = (TextView) findViewById(R.id.title);
		registerTip = (TextView) findViewById(R.id.register_tip);
		timerTip = (TextView) findViewById(R.id.timer_tip);

		titleName.setText("用户注册");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});

		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(type == SUBMIT_TYPE.GETCODE){

					if(!validate(phoneEdit.getText().toString())){
						return;
					}

					checkIsExist(phoneEdit.getText().toString());

				}else if(type == SUBMIT_TYPE.POSTCODE){

					if(StringUtil.isEmpty(phoneEdit.getText().toString().trim())){
						ToastUtil.toastShort(context, "验证码不能为空！");
						return;
					}

					submitVerificationCodeHandle();

				}else{//注册
					registerUser();
				}

			}
		});

		getCodeAgainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getVerificationCodeHandle();

				startTimer();

				getCodeAgainBtn.setVisibility(View.GONE);

				timeCounter = 60;

				setTimerTip();
			}
		});
	}

	//输入验证
	private boolean validate(String phone){
		if(StringUtil.isEmpty(phone)){
			ToastUtil.toastShort(context, "手机号不能为空！");
			return false;
		}else if(!StringUtil.isMobile(phone)){
			ToastUtil.toastShort(context, "请输入有效的手机号！");
			return false;
		}

		return true;
	}

	//检测手机号是否已被注册
	private void checkIsExist(String phoneValue){

		AjaxParams params = new AjaxParams();
		params.put("phone", phoneValue);

		fh.get(Constant.url_userisexist, params, new MyAjaxCallBack(AJAX_VALUE.ISEXIST));

	}

	//获取短信验证码
	private void getVerificationCodeHandle(){
		SMSSDK.initSDK(context, Utils.SMSSDK_KEY, Utils.SMSSDK_SECRET);

		if(eventHandler == null){
			eventHandler = new SMSEventHandler();
			SMSSDK.registerEventHandler(eventHandler);
		}

		SMSSDK.getVerificationCode(Utils.CHINA_CODE, phoneStr);
	}

	//发送短信验证码
	private void submitVerificationCodeHandle(){
		SMSSDK.submitVerificationCode(Utils.CHINA_CODE, phoneStr, phoneEdit.getText().toString());
	}

	//开始计时
	private void startTimer(){
		cancelTimer();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeCounter --;
				handler.sendEmptyMessage(NOTICE_TIME_CHANGE);
			}
		}, 1000, 1000);
	}

	//取消计时
	private void cancelTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}

		timer = new Timer();
	}

	private void setTimerTip(){
		if(timerTip != null){
			timerTip.setVisibility(View.VISIBLE);
			timerTip.setText(Html.fromHtml("<font>等待<span color=\"#FF00FF\">"+timeCounter+"</span>秒后重新发送短信！</font>"));
		}
	}

	//注册到数据库
	private void registerUser(){

		String pwdStr = pwdEdit.getText().toString();
		String pwdAgainStr = pwdAgainEdit.getText().toString();

		if(StringUtil.isEmpty(pwdStr)){
			ToastUtil.toastShort(context, "密码不能为空！");
			return;
		}

		if(StringUtil.isEmpty(pwdAgainStr)){
			ToastUtil.toastShort(context, "确认密码不能为空！");
			return;
		}

		if(StringUtil.iscontainSpecialCharacter(pwdStr) || 
				StringUtil.iscontainSpecialCharacter(pwdAgainStr)){
			ToastUtil.toastShort(context, "不能包含特殊字符！");
			return;
		}

		if((pwdStr.length() < 6 || pwdStr.length() > 20) || 
				(pwdAgainStr.length() < 6 || pwdAgainStr.length() > 20)){
			ToastUtil.toastShort(context, "密码长度在6-20之间！");
			return;
		}

		if(!(pwdStr+"").equals(pwdAgainStr)){
			ToastUtil.toastShort(context, "两次输入的密码不一致！");
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put("phone", phoneStr);
		params.put("password", MD5Util.getMD5String(pwdStr));

		fh.get(Constant.url_userregister, params, new MyAjaxCallBack(AJAX_VALUE.REGISTER));
	}

	//回调函数
	class SMSEventHandler extends EventHandler{

		@Override
		public void afterEvent(int arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			super.afterEvent(arg0, arg1, arg2);

			if(arg1 == SMSSDK.RESULT_COMPLETE){

				if(arg0 == SMSSDK.EVENT_GET_VERIFICATION_CODE){//获取短信验证码成功
					handler.sendEmptyMessage(GETCODE_SUCCESS);
				}else if(arg0 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//发送验证码成功
					handler.sendEmptyMessage(CHECKCODE_SUCCESS);
				}

			}else{

				if(arg0 == SMSSDK.EVENT_GET_VERIFICATION_CODE){//获取短信验证码失败
					handler.sendEmptyMessage(GETCODE_FAIL);
				}else if(arg0 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//发送验证码失败
					handler.sendEmptyMessage(CHECKCODE_FAIL);
				}
			}

		}

		@Override
		public void onUnregister() {
			// TODO Auto-generated method stub
			super.onUnregister();
		}

	}

	class MyAjaxCallBack extends AjaxCallBack<Object>{

		private AJAX_VALUE ajax;

		public MyAjaxCallBack(AJAX_VALUE ajax){
			this.ajax = ajax;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			// TODO Auto-generated method stub
			super.onFailure(t, errorNo, strMsg);

			if(ajax == AJAX_VALUE.REGISTER){
				ToastUtil.toastShort(context, "注册失败="+strMsg);
			}else{
				isExistValue = -1;
				ToastUtil.toastShort(context, "发生异常="+strMsg);
			}
		}

		@Override
		public void onSuccess(Object t) {
			// TODO Auto-generated method stub
			super.onSuccess(t);

			JSONObject obj = null;
			try {
				obj = new JSONObject((String)t);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(ajax == AJAX_VALUE.ISEXIST){

				String result = obj.optString("state");

				if("true".equals(result)){
					ToastUtil.toastShort(context, "该手机号已被注册！");
					isExistValue = 1;
				}else{
					isExistValue = 2;
				}

				if(isExistValue == 2){
					adialog = new AlertDialog.Builder(context).setMessage(
							Html.fromHtml("<font>确定向手机号 <span color=\"#FF00FF\">"+phoneEdit.getText().toString()+"</pan>发送注册验证码？</font>"))
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									adialog.dismiss();

									startTimer();

									timerTip.setVisibility(View.VISIBLE);

									submitBtn.setEnabled(false);

									phoneStr = phoneEdit.getText().toString();

									getVerificationCodeHandle();
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									adialog.dismiss();
								}
							})
							.show();
				}
			}else{
				String result = obj.optString("state");

				if("true".equals(result)){
					ToastUtil.toastShort(context, "注册成功，请登录！");
					finish();
				}else{
					ToastUtil.toastShort(context, "注册失败！");
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(eventHandler != null){
			SMSSDK.unregisterEventHandler(eventHandler);
		}
	}
}
