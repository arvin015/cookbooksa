package com.sky.cookbooksa;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sky.cookbooksa.utils.RandomUtil;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.widget.LuckDrawView;
import com.sky.cookbooksa.widget.RollSurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arvin.li on 2015/7/23.
 */
public class LuckDrawActivity extends BaseActivity {

    private ImageButton backBtn;
    private TextView titleText, ruleText;
    private Button beginBtn;

    private LuckDrawView luckDrawView;
    private RollSurfaceView rollView;

    private int currentAreaId;
    private int leftNum = 3;//抽奖剩余次数

    private final float waitingTime = 1;

    private float timeAfter = -1;//距离下次抽奖时间

    private Timer timer;

    private boolean canBack = true;//是否可返回

    private final String[] areaStrs = new String[]{
            "5元红包", "OPPO MP3", "DNF 钱包",
            "谢谢参与", "10元红包", "索尼 PSP GO"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.luck_draw);

        init();
    }

    private void init() {

        backBtn = (ImageButton) findViewById(R.id.back);
        titleText = (TextView) findViewById(R.id.title);
        ruleText = (TextView) findViewById(R.id.ruleText);
        beginBtn = (Button) findViewById(R.id.beginBtn);
        luckDrawView = (LuckDrawView) findViewById(R.id.luckDrawView);
        rollView = (RollSurfaceView) findViewById(R.id.rollView);

        backBtn.setVisibility(View.VISIBLE);

        titleText.setText("抽奖");
        ruleText.setText("每轮抽奖有3次机会，每轮抽完，需等待1分钟，才可再进行下一轮抽奖。");

        beginBtnCheck();

        luckDrawView.setDefaultAngle(1080, 60);

        luckDrawView.setListener(new LuckDrawView.ILuckDrawListener() {
            @Override
            public void luckDrawEnd(int areaId) {

                canBack = true;//可以返回了

                if (leftNum > 0) {
                    beginBtn.setEnabled(true);

                    beginBtn.setBackgroundResource(R.drawable.common_yellow_shape);

                    setBeginBtnText("");
                } else {
                    setBeginBtnText(convertTime(waitingTime * 60 * 1000));

                    leftNum = 3;//重设

                    SharedPreferencesUtils.getInstance(context, "luck").saveSharedPreferences("lastTime",
                            System.currentTimeMillis() + "");

                    timeAfter = waitingTime * 60 * 1000;

                    startTimer();
                }

                if (currentAreaId != areaId) {
                    return;
                }

                if (areaId == 4) {
                    ToastUtil.toastShort(context, "很遗憾，没有中奖，再接再厉！");
                } else {
                    ToastUtil.toastShort(context, "恭喜你，获得" + areaStrs[areaId - 1] + "！");
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (canBack) {
                    back();
                } else {
                    ToastUtil.toastShort(context, "抽奖中，无法退出！");
                }

            }
        });

        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                canBack = false;//禁止返回了

                beginBtn.setEnabled(false);
                beginBtn.setBackgroundResource(R.drawable.common_grey_shape);

                currentAreaId = RandomUtil.randomNum(360 / 60) + 1;

                luckDrawView.startTurn(currentAreaId);

                leftNum--;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LuckDrawActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rollView.addText("136xxxx5103");
                    }
                });
            }
        }, 5000);
    }

    /**
     * 抽奖状态检测
     */
    private void beginBtnCheck() {

        String lastTime = SharedPreferencesUtils.getInstance(context, "luck").loadStringSharedPreference("lastTime");
        String leftNumS = SharedPreferencesUtils.getInstance(context, "luck").loadStringSharedPreference("leftNum");

        if (lastTime != null && lastTime != "") {
            timeAfter = StringUtil.TimeLeft2(waitingTime, lastTime);
        }

        if (leftNumS != null) {
            leftNum = Integer.parseInt(leftNumS);
        }

        if (timeAfter > 0) {//上次抽完，还在等待中

            setBeginBtnText(convertTime(timeAfter));

            beginBtn.setEnabled(false);
            beginBtn.setBackgroundResource(R.drawable.common_grey_shape);

            startTimer();

        } else {

            SharedPreferencesUtils.getInstance(context, "luck").saveSharedPreferences("lastTime", "");

            setBeginBtnText("");
        }
    }

    /**
     * 设置开始按钮Text
     */
    private void setBeginBtnText(String text) {
        if (StringUtil.isEmpty(text)) {
            beginBtn.setText("开始抽奖" + " x" + leftNum);
        } else {
            beginBtn.setText(Html.fromHtml("距离下次抽奖时间 <font color=\"#FF0000\">" + text + "</font>"));
        }
    }

    /**
     * 转换计时时间
     */
    private String convertTime(float time) {
        int m = (int) (time / 1000 / 60);
        int s = (int) (time % (1000 * 60) / 1000);

        return String.format("%02d:%02d", m, s);
    }

    /**
     * 开始计时
     */
    private void startTimer() {
        cancelTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LuckDrawActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeAfter -= 1000;

                        setBeginBtnText(convertTime(timeAfter));

                        if (timeAfter <= 0) {

                            cancelTimer();

                            SharedPreferencesUtils.getInstance(context, "luck").saveSharedPreferences("lastTime", "");
                            setBeginBtnText("");

                            beginBtn.setEnabled(true);
                            beginBtn.setBackgroundResource(R.drawable.common_yellow_shape);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    /**
     * 取消计时
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK & event.getRepeatCount() == 0) {

            if (!canBack) {

                ToastUtil.toastShort(context, "抽奖中，无法退出！");

                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelTimer();

        SharedPreferencesUtils.getInstance(context, "luck").saveSharedPreferences("leftNum", leftNum + "");
    }
}
