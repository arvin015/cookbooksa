package com.sky.cookbooksa;

import org.json.JSONException;
import org.json.JSONObject;

import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditActivity extends BaseActivity{

	private Context context;

	private RelativeLayout largeEditContainer;
	private EditText smallEditText, largeEditText;
	private ImageButton backBtn;
	private TextView title, totalNum;
	private Button sureBtn;

	private String content, flag, key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		init();
	}

	private void init(){

		context = this;

		Intent intent = getIntent();
		content = intent.getStringExtra("content");
		flag = intent.getStringExtra("flag");
		key = intent.getStringExtra("key");

		smallEditText = (EditText) findViewById(R.id.smallEdit);
		backBtn = (ImageButton) findViewById(R.id.back);
		largeEditText = (EditText) findViewById(R.id.largeEdit);
		largeEditContainer = (RelativeLayout) findViewById(R.id.largeEditContainer);
		totalNum = (TextView) findViewById(R.id.totalNum);

		backBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		sureBtn = (Button) findViewById(R.id.sureBtn);
		sureBtn.setVisibility(View.VISIBLE);

		title.setText(flag);

		if("nick".equals(key)){

			smallEditText.setText(content);
			smallEditText.setSelection(content.length());

		}else if("owner_signature".equals(key)){

			largeEditContainer.setVisibility(View.VISIBLE);
			smallEditText.setVisibility(View.GONE);

			largeEditText.setText(content);
			largeEditText.setSelection(content.length());

			totalNum.setText("" + (30 - content.length()));
		}

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				back();
			}
		});


		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				submitHandle();
			}

		});

		largeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				totalNum.setText("" + (30 - s.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void submitHandle() {
		// TODO Auto-generated method stub
		final String editStr = "nick".equals(key) ? smallEditText.getText().toString()
				: largeEditText.getText().toString();

		AjaxParams params = new AjaxParams();
		params.put("key", key);
		params.put("content", editStr);
		params.put("phone", Utils.userPhone);

		fh.post(Constant.url_userupdate, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				ToastUtil.toastShort(context, "修改失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				JSONObject obj = null;
				String result = null;
				try {
					obj = new JSONObject((String)t);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(obj != null){
					result = obj.optString("state");
				}

				if("true".equals(result)){
					Intent intent = new Intent();
					intent.putExtra("edit", editStr);

					setResult(RESULT_OK, intent);
					finish();

					ToastUtil.toastShort(context, "修改成功！");
				}else{
					ToastUtil.toastShort(context, "修改失败！");
				}
			}
		});


	}

}
