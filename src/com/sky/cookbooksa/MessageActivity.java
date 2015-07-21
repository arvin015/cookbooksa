package com.sky.cookbooksa;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sky.cookbooksa.entity.MessageInfo;
import com.sky.cookbooksa.uihelper.ConfirmDialogHelper;
import com.sky.cookbooksa.uihelper.ConfirmDialogHelper.IConfirmDialogListener;
import com.sky.cookbooksa.uihelper.ListDialogHelper;
import com.sky.cookbooksa.uihelper.ListDialogHelper.IListDialogHelperListener;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageActivity extends BaseActivity {

    private TextView titleText;
    private ImageButton backBtn;
    private ListView msgListView;
    private TextView emptyTipText;

    private List<MessageInfo> msgList;

    private FinalDb finalDb;

    private MessageAdapter msgAdapter;

    private ListDialogHelper listDialog;
    private ConfirmDialogHelper confirmDialog;

    private final int GET_NEW_MSG = 1;
    private final int UPDATE_MSG_STATE = 2;
    private final int DELETE_MSG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message);

        init();
    }

    private void init() {

        finalDb = FinalDb.create(context);

        msgList = new ArrayList<MessageInfo>();

        titleText = (TextView) findViewById(R.id.title);
        emptyTipText = (TextView) findViewById(R.id.emptyTipText);
        backBtn = (ImageButton) findViewById(R.id.back);
        msgListView = (ListView) findViewById(R.id.messageList);

        titleText.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);

        titleText.setText("消息");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                setResult(Activity.RESULT_OK);

                back();
            }
        });

        loadMsg();
    }

    private void loadMsg() {

        //从本地加载消息
        msgList.addAll(finalDb.findAllByWhere(MessageInfo.class, "flag=0"));

        msgAdapter = new MessageAdapter();
        msgListView.setAdapter(msgAdapter);

        msgListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                Log.d("print", "onItemClick====");

                MessageInfo msgInfo = msgList.get(position);

                //未读消息处理
                if (msgInfo.getRead() == 0) {
                    markRead(msgInfo);
                }
            }
        });

        msgListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub

                final MessageInfo msgInfo = msgList.get(position);

                listDialog = new ListDialogHelper(context, new IListDialogHelperListener() {

                    @Override
                    public void clickHandle(int pos) {
                        // TODO Auto-generated method stub

                        if (msgInfo.getRead() == 1) {//已读处理
                            pos = 1;
                        }

                        if (pos == 0) {
                            markRead(msgInfo);
                        } else if (pos == 1) {
                            confirmDialog = new ConfirmDialogHelper(context);
                            confirmDialog.setListener(new IConfirmDialogListener() {

                                @Override
                                public void sureHandler() {
                                    // TODO Auto-generated method stub

                                    deleteMsg(msgInfo);
                                }
                            });
                            confirmDialog.setTipText("确定要删除此消息吗？");
                        }
                    }
                });

                String[] items;
                if (msgInfo.getRead() == 0) {
                    items = new String[]{"标记为已读", "删除"};
                } else {
                    items = new String[]{"删除"};
                }

                listDialog.setItems(items);

                return true;
            }
        });

        loadMsgFromNet();
    }

    //删除消息
    private void deleteMsg(MessageInfo msgInfo) {
        msgList.remove(msgInfo);

        if (msgInfo.getRead() == 1) {
            finalDb.delete(msgInfo);
        }

        //通知服务器删除消息
        AjaxParams params = new AjaxParams();
        params.put("msgId", msgInfo.getId() + "");
        fh.post(Constant.url_deletemessagebymsgid, params,
                new MyAjaxCallBack(DELETE_MSG));

        msgAdapter.notifyDataSetChanged();
    }

    //标记已读
    private void markRead(MessageInfo msgInfo) {
        Utils.newMsgNum--;//未读消息数减一

        msgInfo.setRead(1);//标记为已读

        finalDb.save(msgInfo);//保存到本地SQLite

        //通知服务器消息已读
        AjaxParams params = new AjaxParams();
        params.put("msgId", msgInfo.getId() + "");
        fh.post(Constant.url_updatemessagestate, params,
                new MyAjaxCallBack(UPDATE_MSG_STATE));

        msgAdapter.notifyDataSetChanged();//更新列表
    }

    //从网上获取消息
    private void loadMsgFromNet() {
        // TODO Auto-generated method stub
        AjaxParams params = new AjaxParams();
        params.put("userId", Utils.userId);
        fh.post(Constant.url_getnewmessagesbyuserid,
                params, new MyAjaxCallBack(GET_NEW_MSG));
    }

    class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return msgList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            MessageInfo msgInfo = msgList.get(position);

            ViewHolder viewHolder = null;
            if (convertView == null) {

                viewHolder = new ViewHolder();

                convertView = LayoutInflater.from(context).inflate(R.layout.message_item, null);

                viewHolder.msgImg = (ImageView) convertView.findViewById(R.id.msgImg);
                viewHolder.msgTitle = (TextView) convertView.findViewById(R.id.msgTitleText);
                viewHolder.msgTime = (TextView) convertView.findViewById(R.id.msgTimeText);
                viewHolder.msgContent = (TextView) convertView.findViewById(R.id.msgContentText);
                viewHolder.newMsgFlagImg = (ImageView) convertView.findViewById(R.id.newMsgFlag);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //设置数据
            viewHolder.msgTitle.setText(msgInfo.getTitle());
            viewHolder.msgTime.setText(msgInfo.getCreateTime());
            viewHolder.msgContent.setText(msgInfo.getContent());

            if (msgInfo.getRead() == 0) {
                viewHolder.newMsgFlagImg.setVisibility(View.VISIBLE);
            } else {
                viewHolder.newMsgFlagImg.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView msgImg, newMsgFlagImg;
            TextView msgTitle, msgTime, msgContent;
        }

    }

    class MyAjaxCallBack extends AjaxCallBack<Object> {

        int type = GET_NEW_MSG;

        public MyAjaxCallBack(int type) {
            this.type = type;
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            // TODO Auto-generated method stub
            super.onFailure(t, errorNo, strMsg);

            ToastUtil.toastShort(context, "操作失败！=" + strMsg);
        }

        @Override
        public void onSuccess(Object t) {
            // TODO Auto-generated method stub
            super.onSuccess(t);

            JSONObject resultObj = null;
            try {
                resultObj = new JSONObject(String.valueOf(t));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (resultObj == null) {
                return;
            }

            switch (type) {
                case GET_NEW_MSG:

                    msgList.addAll(0, MessageInfo.getMsgListFromJson(resultObj.optJSONArray("result")));
                    msgAdapter.notifyDataSetChanged();

                    if (msgList.size() <= 0) {
                        emptyTipText.setVisibility(View.VISIBLE);
                    }

                    break;
                case UPDATE_MSG_STATE:
                    break;
                case DELETE_MSG:
                    break;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_OK);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
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
