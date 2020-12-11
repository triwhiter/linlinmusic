package com.ctgu.linlinmusic.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctgu.linlinmusic.R;

public class MyCenterActivity extends Activity {
    private TextView maccount;//姓名
    private TextView level;//等级
    private ImageView btn_my_center; // 箭头 → 详情
    private ImageView mlove_music;
    private TextView love_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center);
//        initUserData();   //初始化用户信息

        initView();   //初始化界面

        initEvent();  //初始化事件
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicListActivity.BORADCAST_ACTION_EXIT);//为BroadcastReceiver指定一个action，即要监听的消息名字
        registerReceiver(mBoradcastReceiver,filter); //动态注册监听  静态的话 在AndroidManifest.xml中定义
    }
//    private void initUserData(){
//        Intent intent = getIntent();
//
//        String userStr = intent.getStringExtra("result");
//        JSONObject userData = RequestServlet.getJSON(userStr);
//        account = userData.optString("account");
//        System.out.println("打印账户姓名"+account);
//    }

    private void initEvent() {
        btn_my_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MyCenterActivity.this,xxx.class 跳转到xxx详情页面
                Intent intent = new Intent(MyCenterActivity.this,MyInfoActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = getSharedPreferences("userInfo",Activity.MODE_PRIVATE);
        String account = preferences.getString("account","Visitor");
        int num = preferences.getInt("num",1);
        System.out.println("kkkan"+account);
//        此处是名称
        maccount.setText(account);
//        此处是等级
        level.setText("level 2");
        love_num.setText("共"+num+"首歌曲");
//        此处是MyCenterActivity.this,xxx.class 跳转到歌曲列表
        mlove_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCenterActivity.this,MusicListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        love_num =this.findViewById(R.id.love_num);
        maccount = this.findViewById(R.id.account);
        level = this.findViewById(R.id.level);
        btn_my_center = this.findViewById(R.id.btn_my_center);
        mlove_music = this.findViewById(R.id.love_adv);
    }

    private BroadcastReceiver mBoradcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MusicListActivity.BORADCAST_ACTION_EXIT)){//发来关闭action的广播
                finish();
            }
        }
    };
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mBoradcastReceiver); //取消监听
    }
}
