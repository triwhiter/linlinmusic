package com.ctgu.linlinmusic;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import com.ctgu.linlinmusic.activity.LoginActivity;


public class SplashActivity extends AppCompatActivity {

    LinearLayout wel;
    private static final long DELAY = 1000;
    private TimerTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        initView();
        initEvent();

        final Intent localIntent=new Intent(this,LoginActivity.class);//你要转向的Activity
        Timer timer=new Timer();
        TimerTask tast=new TimerTask() {
            @Override
            public void run(){
                startActivity(localIntent);//执行
            }
        };
        timer.schedule(tast,DELAY);//3秒后

    }

    private void initEvent(){
        //登录按钮
        wel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //跳转到注册界面
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        wel = this.findViewById(R.id.welcome);
    }
}
