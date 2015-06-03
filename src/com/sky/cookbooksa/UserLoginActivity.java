package com.sky.cookbooksa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sky.cookbooksa.qrcode.CaptureActivity;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.MD5Util;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UserLoginActivity extends BaseActivity{

	private Button loginBtn, qrcodeLoginBtn, registerBtn;
	private EditText phoneEdit, pwdEdit;

	private SharedPreferencesUtils spfu;

	private int QRCODE = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);

		init();
	}

	private void init(){

		spfu = SharedPreferencesUtils.getInstance(context, null);

		phoneEdit = (EditText) findViewById(R.id.login_phone);
		pwdEdit = (EditText) findViewById(R.id.login_password);

		loginBtn = (Button) findViewById(R.id.loginbtn);
		qrcodeLoginBtn = (Button) findViewById(R.id.qrcodeLoginBtn);
		registerBtn = (Button) findViewById(R.id.registerbtn);

		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loginHandle();
			}

		});

		qrcodeLoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, CaptureActivity.class);
				startActivityForResult(intent, QRCODE);
			}
		});

		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intentHandle(RegisterActivity.class, null, false);
				UserLoginActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
	}

	private void loginHandle() {
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("phone", phoneEdit.getText().toString());
		params.put("password", MD5Util.getMD5String(pwdEdit.getText().toString()));

		fh.post(Constant.url_userlogin, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				ToastUtil.toastShort(context, "登录失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				try {
					JSONObject obj = new JSONObject((String)t);

					String result = obj.optString("state");
					if("true".equals(result)){

						ToastUtil.toastShort(context, "登录成功！");

						if(obj != null){
							JSONArray array = obj.optJSONArray("result");
							if(array != null){
								JSONObject json = array.optJSONObject(0);
								if(json != null){
									Utils.isLoaded = true;
									Utils.userPhone = phoneEdit.getText().toString();
									Utils.userId = json.optString("user_id", "0");
									Utils.userNick = json.optString("nick", "");
									Utils.userPic = json.optString("user_pic");

									spfu.saveSharedPreferences(Utils.USER_PHONE, Utils.userPhone);
									spfu.saveSharedPreferences(Utils.USER_ID, Utils.userId);
									spfu.saveSharedPreferences(Utils.USER_NICK, Utils.userNick);
								}
							}
						}

						setResult(RESULT_OK);
						finish();
					}else{
						ToastUtil.toastShort(context, "登录失败，请检查手机号和密码是否正确！");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ToastUtil.toastShort(context, "发送异常");
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK){
			if(requestCode == QRCODE){

				String result = data.getStringExtra("result");

				if(result != null){
					try {
						JSONObject obj = new JSONObject(result);
						if(obj != null){
							String phone = obj.optString("phone", "");
							String password = obj.optString("password", "");

							phoneEdit.setText(phone);
							pwdEdit.setText(password);

							loginHandle();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
