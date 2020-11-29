package cn.sjcup.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.sjcup.musicplayer.R;
import cn.sjcup.musicplayer.SplashActivity;
import cn.sjcup.musicplayer.image.RoundImageView;

public class SideMenuActivity extends Activity {
    RoundImageView roundImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_bar);
        initView();
        initEvent();
    }

    private void initEvent(){

        roundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //跳转到注册界面
                Intent intent=new Intent(SideMenuActivity.this, MyCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        roundImageView = findViewById(R.id.sb_avater);
    }
}
