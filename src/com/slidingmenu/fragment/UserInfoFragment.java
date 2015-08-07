package com.slidingmenu.fragment;

import net.tsz.afinal.FinalBitmap;

import com.sky.cookbooksa.CollectActivity;
import com.sky.cookbooksa.FootprintActivity;
import com.sky.cookbooksa.IUserLoadedCallback;
import com.sky.cookbooksa.NearbyMapActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.UserInfoDetailActivity;
import com.sky.cookbooksa.UserLoginActivity;
import com.sky.cookbooksa.uihelper.ImageDialogHelper;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonGridView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class UserInfoFragment extends Fragment {

    private MainActivity act;

    private View view;//缓存页面

    private RelativeLayout rlUserPic;
    private Button goLogin;
    private CommonGridView gridview;
    private TextView userNick;
    private ImageView userPic;

    private int screenWidth;

    private final int MY_INFO = 0;
    private final int MY_LOVE = 1;
    private final int MY_STEP = 2;
    private final int MY_KITCHEN = 3;
    private final int MY_TALK = 4;
    private final int NEARBY_PEOPLE = 5;

    private Intent intent;

    private FinalBitmap fb;

    private int LOADCODE = 100;
    private int USERCODE = 200;

    private ImageDialogHelper imageDialogHelper;

    private IUserLoadedCallback userLoadedCallback;

    private String[] name_options = new String[]{
            "我的资料", "我的收藏", "我的足迹", "我的私房菜", "我的说说", "附近的人"
    };

    private int[] img_options = new int[]{
            R.drawable.info_icon, R.drawable.love_icon, R.drawable.history_icon, R.drawable.kitchens_icon,
            R.drawable.talk_icon, R.drawable.nearby_icon
    };

    public UserInfoFragment() {
    }

    public UserInfoFragment(MainActivity act) {
        this.act = act;

        userLoadedCallback = act;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.user_center, container, false);

            init();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);//先移除
        }

        return view;
    }

    private void init() {
        // TODO Auto-generated method stub

        fb = FinalBitmap.create(act);
        fb.configLoadingImage(R.drawable.avatar_default);
        fb.configLoadfailImage(R.drawable.avatar_default);

        screenWidth = DisplayUtil.screenWidth;

        rlUserPic = (RelativeLayout) view.findViewById(R.id.rl_userpic);
        gridview = (CommonGridView) view.findViewById(R.id.usergridview);
        goLogin = (Button) view.findViewById(R.id.gologin);
        userNick = (TextView) view.findViewById(R.id.usernick);
        userPic = (ImageView) view.findViewById(R.id.userpic);

        gridview.setAdapter(new OptionAdapter());

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                if (StringUtil.isEmpty(Utils.userPhone)) {
                    ToastUtil.toastShort(act, "请先登录！");
                    return;
                }

                if (intent == null) {
                    intent = new Intent();
                }

                switch (arg2) {
                    case MY_INFO:
                        intent.setClass(act, UserInfoDetailActivity.class);
                        break;
                    case MY_LOVE:
                        intent.setClass(act, CollectActivity.class);
                        break;
                    case MY_STEP:
                        intent.setClass(act, FootprintActivity.class);
                        break;
                    case MY_KITCHEN:
                        break;
                    case MY_TALK:
                        break;
                    case NEARBY_PEOPLE:
                        intent.setClass(act, NearbyMapActivity.class);
                        break;
                }

                if (arg2 == MY_INFO) {
                    startActivityForResult(intent, USERCODE);
                } else {
                    startActivity(intent);
                }

                act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        userPic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Log.d("print", "path=" + Constant.IMAGE_DIR + Utils.userPic);

                imageDialogHelper = new ImageDialogHelper(act, Constant.IMAGE_DIR + Utils.userPic);
                imageDialogHelper.showDialog();
            }
        });

        goLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(act, UserLoginActivity.class);
                startActivityForResult(intent, LOADCODE);
                act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });

        loadLoginData();
    }

    //获取登录信息
    private void loadLoginData() {
        if (Utils.isLoaded) {

            userNick.setVisibility(View.VISIBLE);
            rlUserPic.setVisibility(View.VISIBLE);

            userNick.setText(Utils.userNick);
            fb.configLoadfailImage(R.drawable.avatar_default);

            fb.display(userPic, Constant.IMAGE_DIR + Utils.userPic);

            goLogin.setVisibility(View.GONE);
        } else {
            userNick.setVisibility(View.GONE);
            rlUserPic.setVisibility(View.GONE);

            goLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == LOADCODE) {

                loadLoginData();

                userLoadedCallback.userLoaded();//通知主程序加载用户消息

            } else if (requestCode == USERCODE) {

                userNick.setText(Utils.userNick);

                if (Utils.userBm != null) {
                    userPic.setImageBitmap(Utils.userBm);

                    Utils.userBm = null;
                }
            }
        }
    }

    class OptionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name_options.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return name_options[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            ViewHolder viewHolder;

            if (arg1 == null) {
                viewHolder = new ViewHolder();
                arg1 = LayoutInflater.from(act).inflate(R.layout.user_grid_item, null);

                viewHolder.optionsContainer = (FrameLayout) arg1.findViewById(R.id.ll_option_container);
                viewHolder.optionImg = (ImageView) arg1.findViewById(R.id.option_image);
                viewHolder.optionName = (TextView) arg1.findViewById(R.id.option_name);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        screenWidth / 3 - 30, screenWidth / 3 + 10);
                viewHolder.optionsContainer.setLayoutParams(params);

                arg1.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) arg1.getTag();
            }

            viewHolder.optionImg.setBackgroundResource(img_options[arg0]);
            viewHolder.optionName.setText(name_options[arg0]);

            return arg1;
        }

        class ViewHolder {
            FrameLayout optionsContainer;
            ImageView optionImg;
            TextView optionName;
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
