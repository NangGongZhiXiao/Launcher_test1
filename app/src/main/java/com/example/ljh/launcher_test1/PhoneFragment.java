package com.example.ljh.launcher_test1;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 知晓 on 2016/11/13.
 */
public class PhoneFragment extends Fragment {
    private GridView gv_dial;                    // 数字键盘的GridView
    private TextView tv_phone_number;            // 显示电话号码的tv
    private TextView tvCall;                     // 拨打电话的textview
    public String[] dialStr;                     // 数字键盘的数据
    private boolean dialIsHide;                  // 判断拨号键盘是否隐藏
    private Context context;
    private ListView callRecordListView;         // 最近联系人的listview
    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库的handler
    private List<CallRecord> callRecords;
    private CallAdapter callAdapter;


    /**
     * 数字键盘的监听
     */
    private AdapterView.OnItemClickListener dialListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String str = tv_phone_number.getText().toString();
            str = str + ((TextView)view).getText().toString();
            tv_phone_number.setText(str);
        }

    };

    /**
     *  点击事件的监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = tv_phone_number.getText().toString();
            switch (v.getId()) {
                case R.id.tv_phone_number:
                    // 当点击输入框时显示拨号键盘
                    if(dialIsHide) {
                        startHideDialAnimation(false);
                        gv_dial.setVisibility(View.VISIBLE);
                        tvCall.setVisibility(View.VISIBLE);
                        dialIsHide = false;
                    }
                    break;
                case R.id.imv_delete_number:
                    tv_phone_number.setText("");
                    break;
                case R.id.imv_back_number:
                    if(str.length() != 0){
                        Log.i("ljh", "this is back image ");
                        str = str.substring(0, str.length() - 1);
                        tv_phone_number.setText(str);
                    }
                    break;
                case R.id.tv_call:
                    if(str == ""){
                        Toast.makeText(context, "你要打电话给空气吗-。-", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    Uri uri = Uri.parse("tel:" + str);
                    intent.setData(uri);
                    context.startActivity(intent);
                    break;
            }
        }
    };

    /**
     *  设置触摸事件的监听
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // 当滑动listview时隐藏拨号键盘
                    if(!dialIsHide) {
                        gv_dial.setVisibility(View.INVISIBLE);
                        tvCall.setVisibility(View.INVISIBLE);
                        startHideDialAnimation(true);
                        dialIsHide = true;
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dialStr = new String[]{"1","2","3","4","5","6","7","8","9","*","0","#"};

        View newsLayout = inflater.inflate(R.layout.phone, container, false);
        context = newsLayout.getContext();

        // 配置拨号键盘
        gv_dial = (GridView) newsLayout.findViewById(R.id.gv_dial);
        gv_dial.setAdapter(new MyAdapter(newsLayout.getContext()));
//        gv_dial.setOnItemClickListener(dialListener);
        tv_phone_number = (TextView) newsLayout.findViewById(R.id.tv_phone_number);
        tv_phone_number.setOnClickListener(clickListener);
        ImageView imgDelete = (ImageView) newsLayout.findViewById(R.id.imv_delete_number);
        ImageView imgBack = (ImageView) newsLayout.findViewById(R.id.imv_back_number);
        tvCall = (TextView) newsLayout.findViewById(R.id.tv_call);
        imgDelete.setOnClickListener(clickListener);
        imgBack.setOnClickListener(clickListener);
        tvCall.setOnClickListener(clickListener);

        // 获取卡的信息
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // 如果SIM卡可用则执行
        if(telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY){
            tvCall.setText(telephonyManager.getNetworkOperatorName());
        }
        else{
            tvCall.setBackgroundColor(0xff696969);
            tvCall.setText("SIM卡不可用");
        }

        // 最近联系人
        callRecordListView = (ListView) newsLayout.findViewById(R.id.lv_call_record);
        callRecordListView.setAdapter(callAdapter);
        callRecordListView.setOnTouchListener(touchListener);
        // 查询数据库通话记录的信息
        asyncQueryHandler = new MyAsyncQueryHandler(context.getContentResolver());
        // 需要查询的列
        String[] projection = {
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls._ID
        };
        // 开始查询
        asyncQueryHandler.startQuery(0, null, CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

        return newsLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *  通话记录信息类
     */
    public class CallRecord {
        private int id;                         // id
        private String name;                    // 名字
        private String number;                  // 电话号码
        private String date;                    // 通话时间
        private int callType;                   // 通话类型，1、来电，2、拨出，3、未接
        private int callCount;                  // 通话次数


        private int getId() {
            return id;
        }

        private void setId(int id) {
            this.id = id;
        }

        private String getName() {
            return name;
        }

        public int getCallCount() {
            return callCount;
        }

        public String getNumber() {
            return number;
        }

        public String getDate() {
            return date;
        }

        public int getCallType() {
            return callType;
        }

        public void setCallCount(int callCount) {
            this.callCount = callCount;
        }

        public void setCallType(int callType) {
            this.callType = callType;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }


    /**
     * 数字拨号键盘的adapter
     */
    public class MyAdapter extends BaseAdapter {
        private Context context;
        public MyAdapter(Context context) {
            super();
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.dial_background);
            textView.setText(dialStr[position]);
            textView.setTextSize(45);
            textView.setTextColor(Color.BLACK);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = tv_phone_number.getText().toString();
                    str = str + ((TextView)v).getText().toString();
                    tv_phone_number.setText(str);
                }
            });

            return textView;
        }

        public final int getCount() {
            return dialStr.length;
        }

        public final Object getItem(int position) {
            return dialStr[position];
        }

        public final long getItemId(int position) {
            return position;
        }


    }

    /**
     *  最近联系人的adapter
     */
    public class CallAdapter extends BaseAdapter{
        private Context context;
        private List<CallRecord> callRecords;
        private LayoutInflater inflater;
        public CallAdapter(Context context, List<CallRecord> callRecords) {
            this.context = context;
            this.callRecords = callRecords;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return callRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return callRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.call_record_item, null);
                viewHolder = new ViewHolder();
                viewHolder.callType = (ImageView) convertView.findViewById(R.id.imv_call_type);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_call_name);
                viewHolder.number = (TextView) convertView.findViewById(R.id.tv_call_number);
                viewHolder.date = (TextView) convertView.findViewById(R.id.tv_call_date);
                viewHolder.callBack = (ImageView) convertView.findViewById(R.id.imv_call_back);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final CallRecord callRecord = callRecords.get(position);
            switch (callRecord.getCallType()) {
                case 1:
                    viewHolder.callType.setBackgroundResource(R.drawable.delete);
                    break;
                case 2:
                    viewHolder.callType.setBackgroundResource(R.drawable.delete);
                    break;
                case 3:
                    viewHolder.callType.setBackgroundResource(R.drawable.delete);
                    break;
            }
            viewHolder.name.setText(callRecord.getName());
            viewHolder.number.setText(callRecord.getNumber());
            viewHolder.date.setText(callRecord.date);

            // 设置回拨的点击的监听事件
            viewHolder.callBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callRecord.getNumber()));
                    try{
                    context.startActivity(intent);
                    }catch (SecurityException e) {
                        e.printStackTrace();
                        Log.e("ljh", "没有拨打电话的权限！！！");
                    }
                }
            });

            return convertView;
        }

        private class ViewHolder{
            ImageView callType;
            ImageView callBack;
            TextView name;
            TextView number;
            TextView date;
        }
    }

    /**
     *  异步查询最经联系人信息
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Log.i("ljh", "this is onQueryComplete " + cursor);
            if (cursor != null && cursor.getCount() > 0) {
                callRecords = new ArrayList<CallRecord>();
                SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
                Date date;
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    date = new Date(cursor.getLong(cursor
                            .getColumnIndex(CallLog.Calls.DATE)));
                    String number = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.NUMBER));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls.TYPE));
                    String cachedName = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
                    int id = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls._ID));

                    CallRecord callRecord = new CallRecord();
                    callRecord.setId(id);
                    callRecord.setNumber(number);
                    callRecord.setName(cachedName);
                    // 如果名字为空则显示为电话号码
                    if (null == cachedName || "".equals(cachedName)) {
                        callRecord.setName(number);
                    }
                    callRecord.setCallType(type);
                    callRecord.setDate(sfd.format(date));

                    callRecords.add(callRecord);
                }
                Log.i("ljh", "the call records size is " + callRecords.size());
                // 如果有最近联系人则将他添加到listview中
                if (callRecords.size() > 0) {
                    callAdapter  = new CallAdapter(context, callRecords);
                    callRecordListView.setAdapter(callAdapter);
                }
            }
            super.onQueryComplete(token, cookie, cursor);
        }
    }

    /**
     * 隐藏拨号键盘的动画
     * @param isHide 隐藏还是显示
     */
    private void startHideDialAnimation(boolean isHide) {
        // 获得拨号键盘的宽高
        int dialHeight = gv_dial.getHeight();
        int callHeight = tvCall.getHeight();

        TranslateAnimation animation;
        // 开始隐藏或者显示的动画
        if(isHide) {
            animation = new TranslateAnimation(0, 0, 0, dialHeight + callHeight);
            animation.setDuration(300L);
            gv_dial.startAnimation(animation);
            tvCall.startAnimation(animation);
        }
        else {
            animation = new TranslateAnimation(0, 0, dialHeight + callHeight, 0);
            animation.setDuration(300L);
            gv_dial.startAnimation(animation);
            tvCall.startAnimation(animation);
        }
    }

}
