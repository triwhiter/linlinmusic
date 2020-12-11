package com.ctgu.linlinmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.ctgu.linlinmusic.R;
import com.ctgu.linlinmusic.adapter.LocalMusicAdapter;
import com.ctgu.linlinmusic.entity.LocalMusicBean;
import com.ctgu.linlinmusic.image.RoundImageView;
import com.ctgu.linlinmusic.player.PlayerControl;
import com.ctgu.linlinmusic.player.PlayerPresenter;
import com.ctgu.linlinmusic.player.PlayerViewControl;
import com.ctgu.linlinmusic.servlet.RequestServlet;

public class MusicListActivity extends AppCompatActivity implements View.OnClickListener{
    private String account;    //账户
    public static int musicId = 0;   //歌曲id
    public int playPattern;  //播放模式
    public static JSONArray MusicList;
    public int songNum = 0;  //歌曲总数
    private static Button mPlayOrPause;
    private static Button mPlayLast;
    private static Button mPlayNext;
    private static TextView mMusicName;
    private static TextView mMusicArtist;
    private static SmartImageView mMusicPic;
    private ImageView mMenu;
    public final static String BORADCAST_ACTION_EXIT = "exit_app";//关闭活动的广播action名称
    public SearchView searchView;


    MediaPlayer mediaPlayer;

    //播放状态
    public final int PLAY_STATE_PLAY=1;   //在播
    public final int PLAY_STATE_PAUSE=2;  //暂停
    public final int PLAY_STATE_STOP=3;   //未播


    RecyclerView musicRV;
    LocalMusicAdapter adapter;
    //数据源
    List<LocalMusicBean> mDatas;
    //记录当前播放音乐的位置
    int currentPlayPosition = -1;
    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private NavigationView mNavigationView;//侧边菜单项
    private MenuItem mPreMenuItem;
    //public PlayerViewControl mPlayerViewControl = ViewControl;

    //音乐控件
    private PlayerControl playerControl = new PlayerPresenter(new MainActivity());
    public PlayerViewControl mPlayerViewControl = new PlayerViewControl() {
        @Override
        public void onPlayerStateChange(int state) {
            //根据播放状态来修改UI
            switch (state) {
                case PLAY_STATE_PLAY:
                    //播放中的话，我们要修改按钮显示为暂停
                    mPlayOrPause.setBackgroundResource(R.drawable.bofangb);
                    break;
                case PLAY_STATE_PAUSE:
                case PLAY_STATE_STOP:
                    mPlayOrPause.setBackgroundResource(R.drawable.bofang);
                    break;
            }
        }

        @Override
        public void onSeekChange(final int seek) {
        }
    };
    public static enum IsPlay{
        play, notPlay
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        initUserData();

        initView(); //初始化界面
        initEvent(); //初始化事件

        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BORADCAST_ACTION_EXIT);//为BroadcastReceiver指定一个action，即要监听的消息名字
        registerReceiver(mBoradcastReceiver,filter); //动态注册监听  静态的话 在AndroidManifest.xml中定义

        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();

        //创建适配器
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRV.setAdapter(adapter);
        initSideBarEvent();


        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRV.setLayoutManager(layoutManager);



        //设置点击事件
        setEventListener();
    }

    //初始化用户信息
    private void initUserData(){
        Intent intent = getIntent();

        String userStr = intent.getStringExtra("result");
        JSONObject userData = RequestServlet.getJSON(userStr);
        if (userData != null){
            account = userData.optString("account");
            musicId = userData.optInt("music_id");
            playPattern = userData.optInt("pattern");
        }


    }

    private void setEventListener() {
        // 设置每一项的点击事件
        adapter.setOnItemClinkListener(new LocalMusicAdapter.OnItemClinkListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition = position;
                LocalMusicBean musicBean = mDatas.get(position);

/*                //设置底部
                singerTv.setText(musicBean.getSinger());
                songTv.setText(musicBean.getSong());*/

                //停止播放
                playerControl.stopPlay();
                //重置播放器
                try {
                    JSONObject musicInfo = RequestServlet.getMusicList().getJSONObject(musicId);
                    String name = musicInfo.optString("name");
                    String author = musicInfo.optString("author");
                    String img = musicInfo.optString("img");
                    mMusicPic.setImageUrl(RequestServlet.IMG+img,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
                    mMusicName.setText(name);
                    mMusicArtist.setText(author);
                } catch (Exception e) {
                    e.printStackTrace();
                }




                //跳转到注册界面
                Intent intent=new Intent(MusicListActivity.this, MainActivity.class);
                intent.putExtra("musicId", musicBean.getId());
                startActivity(intent);


            }
        });

    }



    private void loadLocalMusicData() throws JSONException {

        for(int i = 0; i < songNum; i++){
            JSONObject music_json = MusicList.getJSONObject(i);
            String sid = music_json.getString("musicId");
            String name = music_json.getString("name");
            String album = music_json.getString("album");
            String picture = music_json.getString("img");
            String time = music_json.getString("duration");
            String author = music_json.getString("author");
            mDatas.add(new LocalMusicBean(sid,name,author,album,picture,time));
        }
        //数据变化，提示更新
        adapter.notifyDataSetChanged();
    }

    private void searchMusicData(String key) throws JSONException {
        mDatas.clear();

        for(int i = 0; i < songNum; i++){
            JSONObject music_json = MusicList.getJSONObject(i);
            if(music_json.getString("name").indexOf(key)!=-1){
                String sid = music_json.getString("musicId");
                String name = music_json.getString("name");
                String album = music_json.getString("album");
                String picture = music_json.getString("img");
                String time = music_json.getString("duration");
                String author = music_json.getString("author");
                mDatas.add(new LocalMusicBean(sid,name,author,album,picture,time));
            }

        }
        //数据变化，提示更新
        adapter.notifyDataSetChanged();
    }



    private void initView() {
        /*初始化控件的函数*/
        musicRV = this.findViewById(R.id.local_music_rv);
        mPlayOrPause = (Button) this.findViewById(R.id.play_or_pause_btn);
        mPlayLast= (Button) this.findViewById(R.id.play_last_btn);
        mPlayNext = (Button) this.findViewById(R.id.play_next_btn);
        mMusicName = (TextView) this.findViewById(R.id.text_view_name1);
        mMusicArtist = (TextView) this.findViewById(R.id.text_view_artist1);
        mMusicPic = (SmartImageView) this.findViewById(R.id.siv_icon1);
        mMenu = this.findViewById(R.id.menu_button);
        searchView= this.findViewById(R.id.searchView);

        Intent intent = getIntent();
        if (intent.getStringExtra("musicIdback") != null){
        musicId = Integer.parseInt(intent.getStringExtra("musicIdback"));
            try {
                JSONObject musicInfo = RequestServlet.getMusicList().getJSONObject(musicId);
                String name = musicInfo.optString("name");
                String author = musicInfo.optString("author");
                String img = musicInfo.optString("img");
                mMusicPic.setImageUrl(RequestServlet.IMG+img,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
                mMusicName.setText(name);
                mMusicArtist.setText(author);
                if(playerControl.IsPlay(PLAY_STATE_PLAY)){
                    mPlayerViewControl.onPlayerStateChange(1);
                }

                else {
                    mPlayerViewControl.onPlayerStateChange(2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //获取音乐列表
        getMusicListThread();
    }

    //初始化事件
    private void initEvent(){

        //播放/暂停按钮
        mPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ! playerControl.IsPlay(PLAY_STATE_STOP)){
                    if(playerControl!=null){

                        if(playerControl.IsPlay(PLAY_STATE_PLAY)){

                            playerControl.playOrPauselist();
                            mPlayerViewControl.onPlayerStateChange(2);
                        }

                        else {

                            playerControl.playOrPauselist();
                            mPlayerViewControl.onPlayerStateChange(1);
                        }
                    }
                }
            }
        });

        //播放上一首
        mPlayLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerControl!=null){
                    if ( !playerControl.IsPlay(PLAY_STATE_STOP) ){
                        playerControl.playLast();
                        if (musicId==0)musicId=songNum-1;
                        else musicId = musicId-1;
                        try {
                            JSONObject musicInfo = RequestServlet.getMusicList().getJSONObject(musicId);
                            String name = musicInfo.optString("name");
                            String author = musicInfo.optString("author");
                            String img = musicInfo.optString("img");
                            mMusicPic.setImageUrl(RequestServlet.IMG+img,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
                            mMusicName.setText(name);
                            mMusicArtist.setText(author);
                            mPlayerViewControl.onPlayerStateChange(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                }
            }
        });

        //播放下一首
        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerControl!=null){
                    if ( !playerControl.IsPlay(PLAY_STATE_STOP) ){
                        playerControl.playNext();
                        int tid;
                        if (musicId>=songNum-1)musicId=0;
                        else musicId = musicId+1;
                        try {
                            JSONObject musicInfo = RequestServlet.getMusicList().getJSONObject(musicId);
                            String name = musicInfo.optString("name");
                            String author = musicInfo.optString("author");
                            String img = musicInfo.optString("img");
                            mMusicPic.setImageUrl(RequestServlet.IMG+img,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
                            mMusicName.setText(name);
                            mMusicArtist.setText(author);
                            mPlayerViewControl.onPlayerStateChange(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
        });


        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout != null)
                    mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    try {
                        searchMusicData(query);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //mListView.clearTextFilter();
                }
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    try {
                        searchMusicData(newText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //mListView.clearTextFilter();
                }
                return false;
            }
        });



        //返回
        mMusicPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!playerControl.IsPlay(PLAY_STATE_STOP))
                    finish();
            }
        });




    }

    private void initSideBarEvent() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.list_dra);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            //当抽屉的位置发生变化时调用
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            //当抽屉已经处于完全打开的状态时调用
            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(MusicListActivity.this, "我真是一个小可爱！", Toast.LENGTH_SHORT).show();
                mDrawerLayout = findViewById(R.id.list_dra);
                RoundImageView roundImageView= findViewById(R.id.sb_avater);
                mNavigationView = findViewById(R.id.list_nav);
                roundImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        //跳转到注册界面
                        Intent intent=new Intent(MusicListActivity.this, MyCenterActivity.class);
                        startActivity(intent);
                    }
                });
                setNavigationViewItemClickListener();
            }
            //当抽屉已经完全关闭状态时调用
            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(MusicListActivity.this, "舒服，来首歌！", Toast.LENGTH_SHORT).show();
            }
            //抽屉滑动状态改变时调用
            //状态值STATE_IDLE：闲置、STATE_DRAGGING：拖拽、STATE_SETTLING：固定的
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    public void setNavigationViewItemClickListener() {
        //设置侧滑监听事件
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            //区别每一个item做的监听事件
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (null != mPreMenuItem) {
                    mPreMenuItem.setChecked(false);
                }
                //item.getItemId()是被点击item的ID
                switch (item.getItemId()) {
                    case R.id.my_self:
                        Intent intent1=new Intent(MusicListActivity.this,MyCenterActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.change_pw:
                        Intent intent2=new Intent(MusicListActivity.this,ChangePwdActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.loginout:
                        //退出按钮
                        Toast.makeText(MusicListActivity.this, "正在保存信息…", Toast.LENGTH_SHORT).show();
                        saveDataToDB();
                        break;


                    default:
                        break;
                }
                item.setChecked(true);
                //关闭抽屉即关闭侧换此时已经跳转到其他界面，自然要关闭抽屉
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mPreMenuItem = item;
                return false;
            }
        });
    }


    //保存数据到数据库里
    private void saveDataToDB(){
        new Thread() {
            public void run () {
                try {
                    JSONObject result = RequestServlet.savePlayerInformation(account, musicId, playPattern);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    handler1.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler1 = new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                if (msg.what == 1) {
                    JSONObject result = (JSONObject) msg.obj;
                    stop();
                    MusicListActivity.this.finish();
                    Toast.makeText(MusicListActivity.this, "已退出", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //获取音乐列表
    private void getMusicListThread(){
        new Thread(){
            @Override
            public void run() {
                try{
                    JSONArray result = RequestServlet.getMusicList();
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = result;
                    handler2.sendMessage(msg);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler handler2 = new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                if (msg.what == 2) {
                    MusicList = (JSONArray) msg.obj;
                    songNum = MusicList.length();
                    System.out.println("smsmsmms"+songNum);
                    SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = ((SharedPreferences) userInfo).edit();//获取Editor
                    //得到Editor后，写入需要保存的数据
                    editor.putInt("num", songNum);

                    editor.commit();//提交修改

                    //根据用户数据和歌曲列表初始化有关歌曲的界面
//                    setMusicView(MainActivity.IsPlay.notPlay);
                    //加载本地数据源
                    loadLocalMusicData();

                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void stop() {

        playerControl.stopPlay();

        onBackPressed();
        //stopService(musicIntent);
    }

    private BroadcastReceiver mBoradcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BORADCAST_ACTION_EXIT)){//发来关闭action的广播
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
    //返回按钮 退出系统
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(BORADCAST_ACTION_EXIT);
        sendBroadcast(intent);//发送退出系统广播  每个接收器都会收到 调动finish（）关闭activity
        finish();

    }


    @Override
    public void onClick(View view) {

    }
}