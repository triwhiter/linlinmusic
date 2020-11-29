package cn.sjcup.musicplayer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.sjcup.musicplayer.R;

public class MyInfoActivity extends Activity {
    private TextView maccount;//昵称
    private TextView mage;//听歌的歌龄
    private TextView mintro;//简介
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

//
        initView();   //初始化界面
//
        initEvent();  //初始化事件
    }

    private void initEvent() {
        //获取用户昵称
        SharedPreferences preferences = getSharedPreferences("userInfo",Activity.MODE_PRIVATE);
        String account = preferences.getString("account","Visitor");
        maccount.setText(account);
        //
        mage.setText("2 年");
        //
        mintro.setText("这个人很懒，什么都没有留下！");
    }

    private void initView() {
        maccount = this.findViewById(R.id.info_account);
        mage = this.findViewById(R.id.info_age);
        mintro = this.findViewById(R.id.intro_con);
    }
}
