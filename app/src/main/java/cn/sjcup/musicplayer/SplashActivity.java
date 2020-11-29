package cn.sjcup.musicplayer;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.sjcup.musicplayer.activity.LoginActivity;
import cn.sjcup.musicplayer.activity.RegisterActivity;


public class SplashActivity extends AppCompatActivity {

    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        initView();
        initEvent();
    }

    private void initEvent(){
        //登录按钮
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //跳转到注册界面
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        btn1 = this.findViewById(R.id.dl_btn);
    }
}
