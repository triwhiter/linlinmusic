package cn.sjcup.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.sjcup.musicplayer.R;
import cn.sjcup.musicplayer.entity.LocalMusicAdapter;
import cn.sjcup.musicplayer.entity.LocalMusicBean;
import cn.sjcup.musicplayer.image.RoundImageView;
import cn.sjcup.musicplayer.servlet.RequestServlet;

public class MusicListActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView musicRV;
    LocalMusicAdapter adapter;
    //数据源
    List<LocalMusicBean> mDatas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        initView(); //初始化界面


        mDatas = new ArrayList<>();

        //创建适配器
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRV.setAdapter(adapter);

        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRV.setLayoutManager(layoutManager);

        //加载本地数据源
        try {
            loadLocalMusicData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadLocalMusicData() throws JSONException {
        //加载本地存储的音乐文件到集合当中
        // 获取ContentResolver
        //ContentResolver resolver = getContentResolver();
        //获取本地音乐的存储地址
        //Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //查询
        /*       Cursor cursor = resolver.query(null, null, null, null);
        //遍历
        int id = 0;
        while(cursor.moveToNext()){
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
            //封装
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path);
            mDatas.add(bean);
        }*/
/*        LocalMusicBean bean1 = new LocalMusicBean("1", "厚颜无耻", "小许", "厚颜无耻", "04:30", "path");
        LocalMusicBean bean2 = new LocalMusicBean("2", "暗恋是一个人事情", "宿阳", "像少年一样飞驰", "04:00", "path");
        LocalMusicBean bean3 = new LocalMusicBean("3", "像鱼", "庄严", "像鱼", "05:30", "path");
        mDatas.add(bean1);
        mDatas.add(bean2);
        mDatas.add(bean3);*/


        JSONArray musicList = RequestServlet.MusicList;
        for(int i = 0; i < musicList.length(); i++){
            JSONObject music_json = musicList.getJSONObject(i);
            String sid = music_json.getString("musicId");
            String name = music_json.getString("name");
            String album = music_json.getString("album");
            String time = music_json.getString("duration");
            String author = music_json.getString("author");
            mDatas.add(new LocalMusicBean(sid,name,author,album,time));
        }
        //数据变化，提示更新
        adapter.notifyDataSetChanged();
    }



    private void initView() {
        /*初始化控件的函数*/
        musicRV = findViewById(R.id.local_music_rv);
    }

    @Override
    public void onClick(View view) {

    }
}
