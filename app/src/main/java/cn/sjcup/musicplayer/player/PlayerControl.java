package cn.sjcup.musicplayer.player;

import cn.sjcup.musicplayer.activity.MainActivity;
import cn.sjcup.musicplayer.activity.MusicListActivity;

public interface PlayerControl {
    /*
     *播放
     */
    void playOrPause(MainActivity.IsPlay playState);

    void playOrPauselist(MusicListActivity.IsPlay playState);

    /*
    播放上一首
     */
    void playLast();

    /*
    播放下一首
     */
    void playNext();

    /*
    停止播放
     */
    void stopPlay();

    /*
    设置播放进度
     */
    void seekTo(int seek);

    void playById(String mid);

    //设置播放详情样式
    void setView(String mid);
}
