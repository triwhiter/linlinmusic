package com.ctgu.linlinmusic.player;

import com.ctgu.linlinmusic.activity.MainActivity;

public interface PlayerControl {
    /*
     *播放
     */
    void playOrPause(MainActivity.IsPlay playState);

    public void playOrPauselist();


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

    //获取播放状态
    public boolean IsPlay(int play);

    public boolean IsPAUSE(int play);
}
