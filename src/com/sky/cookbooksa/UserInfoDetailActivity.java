package com.sky.cookbooksa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sky.cookbooksa.uihelper.ListDialogHelper;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.FileUtils;
import com.sky.cookbooksa.utils.PopupWindowUtil;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.UploadUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.NumberPicker;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoDetailActivity extends BaseActivity {

    private RelativeLayout rlUserPic;
    private ImageButton backBtn;
    private TextView title;
    private ImageView userImg;

    private TextView nickValue, sexValue, addressValue, signatureValue, labelValue;

    private ArrayList<RelativeLayout> rlayouts;

    private final String[] keys = new String[]{
            "nick", "sex", "address", "owner_signature", "owner_label"
    };

    private final String[] sexStrs = new String[]{"男", "女"};

    public final int CHOICE_CITY_CODE = 200;

    private final int TAKE_PHOTO = 1000;
    private final int CROP_PHOTO = 2000;
    private final int PHOTO_GALLERY = 3000;

    private File file;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 001) {

                loadMissed();

                JSONObject result = (JSONObject) msg.obj;
                boolean state = result.optBoolean("state", false);
                String name = result.optString("name");

                if (!state) {
                    ToastUtil.toastShort(context, "头像上传失败！");
                } else {

                    Utils.userPic = name;

                    SharedPreferencesUtils.getInstance(context, null).saveSharedPreferences(Utils.USER_PIC, name);

                    Utils.userBm = currentBitmap;

                    userImg.setImageBitmap(currentBitmap);
                    ToastUtil.toastShort(context, "头像上传成功！");
                }
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.user_basic_info);

        loading("资料加载中...");

        init();

    }

    private void init() {

        rlayouts = new ArrayList<RelativeLayout>();

        rlUserPic = (RelativeLayout) findViewById(R.id.rl_userpic);
        userImg = (ImageView) findViewById(R.id.userpic);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        nickValue = (TextView) findViewById(R.id.nick_value);
        sexValue = (TextView) findViewById(R.id.sex_value);
        addressValue = (TextView) findViewById(R.id.address_value);
        signatureValue = (TextView) findViewById(R.id.signature_value);
        labelValue = (TextView) findViewById(R.id.label_value);

        rlayouts.add((RelativeLayout) findViewById(R.id.rl_nick_container));
        rlayouts.add((RelativeLayout) findViewById(R.id.rl_sex_container));
        rlayouts.add((RelativeLayout) findViewById(R.id.rl_address_container));
        rlayouts.add((RelativeLayout) findViewById(R.id.rl_signature_container));
        rlayouts.add((RelativeLayout) findViewById(R.id.rl_label_container));

        title.setText("基本信息");

        for (int i = 0; i < rlayouts.size(); i++) {
            RelativeLayout layout = rlayouts.get(i);
            layout.setOnClickListener(new MyOnClickListener(i));
        }

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                setResult(RESULT_OK);
                back();
            }
        });

        rlUserPic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ListDialogHelper listDialog = new ListDialogHelper(context,
                        new ListDialogHelper.IListDialogHelperListener() {

                            @Override
                            public void clickHandle(int position) {
                                // TODO Auto-generated method stub
                                switch (position) {
                                    case 0:

                                        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".jpg";

                                        file = FileUtils.createFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                                                + "/cookbook/camera/" + fileName);

                                        startActivityForResult(takePhoto(file), TAKE_PHOTO);

                                        break;
                                    case 1:

                                        startActivityForResult(getPhotoGallery(), PHOTO_GALLERY);

                                        break;
                                    case 2:
                                        break;
                                }
                            }
                        });

                listDialog.setItems(new String[]{"拍照", "相册", "默认图片"});
            }
        });

        loadData();
    }

    private void loadData() {
        // TODO Auto-generated method stub
        AjaxParams params = new AjaxParams();
        params.put("phone", Utils.userPhone);

        fh.post(Constant.url_getuserinfo, params, new MyAjaxCallBack());
    }

    private Bitmap currentBitmap;//当前选取上传的图片

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CHOICE_CITY_CODE) {

                String address = data.getStringExtra("city");

                addressValue.setText(address);

            } else if (requestCode == TAKE_PHOTO) {

                startActivityForResult(cropPhoto(Uri.fromFile(file)), CROP_PHOTO);

            } else if (requestCode == PHOTO_GALLERY) {

                currentBitmap = data.getParcelableExtra("data");

                uploadPic();

            } else if (requestCode == CROP_PHOTO) {

                currentBitmap = data.getParcelableExtra("data");

                uploadPic();

            } else {
                String resultStr = data.getStringExtra("edit");
                TextView resultText = (TextView) (rlayouts.get(requestCode).getChildAt(2));
                resultText.setText(resultStr);

                if (requestCode == 0) {
                    Utils.userNick = resultStr;
                    SharedPreferencesUtils.getInstance(context, null).saveSharedPreferences(Utils.USER_NICK, resultStr);
                }
            }
        }
    }

    private File saveFile;//头像本地保存路径

    //上传头像
    private void uploadPic() {

        if (currentBitmap == null) {
            return;
        }

        saveFile = FileUtils.createFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/cookbook/image/upload.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(saveFile);

            if (currentBitmap.compress(CompressFormat.JPEG, 80, fos)) {

                loading("头像上传中...");

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        HashMap<String, String> maps = new HashMap<String, String>();
                        maps.put("userId", Utils.userId);

                        UploadUtil uploadUtil = new UploadUtil();
                        JSONObject result = uploadUtil.uploadFile(saveFile.getAbsolutePath(), maps, Constant.url_uploadPhoto);

                        Message msg = Message.obtain();
                        msg.what = 001;
                        msg.obj = result;

                        handler.sendMessage(msg);
                    }
                }).start();


            } else {
                ToastUtil.toastShort(context, "头像上传失败");
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class MyAjaxCallBack extends AjaxCallBack<Object> {
        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            // TODO Auto-generated method stub
            super.onFailure(t, errorNo, strMsg);

            ToastUtil.toastShort(context, "加载数据失败=" + strMsg);
        }

        @Override
        public void onSuccess(Object t) {
            // TODO Auto-generated method stub
            super.onSuccess(t);

            JSONObject obj = null;
            try {
                obj = new JSONObject((String) t);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONArray arr = null;
            if (obj != null) {
                try {
                    arr = obj.optJSONArray("result");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (arr != null) {
                JSONObject object = arr.optJSONObject(0);
                if (object != null) {
                    nickValue.setText(StringUtil.equals(object.optString("nick", ""), "null") ? "" :
                            object.optString("nick", ""));
                    sexValue.setText(StringUtil.equals(object.optString("sex", "男"), "null") ? "" :
                            object.optString("sex", "男"));
                    addressValue.setText(StringUtil.equals(object.optString("address"), "null") ? "" :
                            object.optString("address"));
                    signatureValue.setText(StringUtil.equals(object.optString("owner_signature", ""), "null") ? "" :
                            object.optString("owner_signature", ""));
                    labelValue.setText(StringUtil.equals(object.optString("owner_label", ""), "null") ? "" :
                            object.optString("owner_label", ""));

                    fb.configLoadfailImage(R.drawable.avatar_default);
                    fb.configLoadingImage(R.drawable.avatar_default);
                    fb.display(userImg, Constant.IMAGE_DIR + object.optString("user_pic"));
                }
            }

            loadMissed();
        }
    }

    class MyOnClickListener implements OnClickListener {

        int type;

        private String content, flag;

        public MyOnClickListener(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub

            TextView contentText = (TextView) (rlayouts.get(type).getChildAt(2));
            content = contentText.getText().toString();

            switch (type) {
                case 0:
                case 3:
                case 4:
                    editHandle();
                    break;
                case 1:
                    showPopuWindow((View) rlayouts.get(type).getParent());
                    break;
                case 2:
                    goChoiceCity();
                    break;
            }
        }

        private void goChoiceCity() {
            // TODO Auto-generated method stub

            Intent intent = new Intent(context, ChoiceCityActivity.class);
            startActivityForResult(intent, CHOICE_CITY_CODE);

            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        private void editHandle() {
            // TODO Auto-generated method stub
            TextView flagText = (TextView) (rlayouts.get(type).getChildAt(1));
            flag = flagText.getText().toString();

            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra("content", content);
            intent.putExtra("flag", flag);
            intent.putExtra("key", keys[type]);

            startActivityForResult(intent, type);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    private View view;
    private NumberPicker np;
    private Button sureBtn;

    //弹出选择框
    private void showPopuWindow(View parent) {

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.sex_popdialog, null);

            np = (NumberPicker) view.findViewById(R.id.sexPicker);
            sureBtn = (Button) view.findViewById(R.id.sureBtn);

            np.setMaxValue(sexStrs.length - 1);
            np.setMinValue(0);
            np.setFocusable(true);
            np.setFocusableInTouchMode(true);
            np.setDisplayedValues(sexStrs);
            //		np.setMaxValue(23);
            //		np.setFormatter(NumberPicker.getTwoDigitFormatter());

            sureBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (!sexStrs[np.getValue()].equals(sexValue.getText().toString())) {
                        sureHandle(sexStrs[np.getValue()]);
                    }

                    PopupWindowUtil.getInstance().dismiss();
                }
            });
        }

        if ("男".equals(sexValue.getText().toString())) {
            np.setValue(1);
        } else {
            np.setValue(2);
        }

        PopupWindowUtil.getInstance().setPopupWindowSize(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(220));
        PopupWindowUtil.getInstance().setPopuWindow(view, R.style.exit_popup_animation, null);
        PopupWindowUtil.getInstance().showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }

    private void sureHandle(final String sexStr) {
        // TODO Auto-generated method stub

        AjaxParams params = new AjaxParams();
        params.put("key", "sex");
        params.put("content", sexStr);
        params.put("phone", Utils.userPhone);

        fh.post(Constant.url_userupdate, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                // TODO Auto-generated method stub
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.toastShort(context, "修改失败=" + strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                super.onSuccess(t);

                JSONObject obj = null;
                String result = null;
                try {
                    obj = new JSONObject((String) t);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (obj != null) {
                    result = obj.optString("state");
                }

                if ("true".equals(result)) {

                    sexValue.setText(sexStr);

                    ToastUtil.toastShort(context, "修改成功！");
                } else {
                    ToastUtil.toastShort(context, "修改失败！");
                }
            }
        });
    }

    //拍照
    public static Intent takePhoto(File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//照片保存位置

        return intent;
    }

    //获取相册---自带截取图片
    public static Intent getPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "false");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);

        return intent;
    }

    //截取照片
    public static Intent cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);

        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            setResult(RESULT_OK);

            back();
        }

        return super.onKeyDown(keyCode, event);
    }
}
