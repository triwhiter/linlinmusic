package cn.sjcup.musicplayer.player;

import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.sjcup.musicplayer.activity.MainActivity;
import cn.sjcup.musicplayer.activity.MusicListActivity;
import cn.sjcup.musicplayer.servlet.RequestServlet;

public class PlayerPresenter implements PlayerControl {

    private static MediaPlayer mMediaPlayer = null;

    private String ADDRESS = RequestServlet.ADDRESS;
    private PlayerViewControl mViewController = null;
    private MainActivity mMainActivity = null;
/*    private MusicListActivity mMusicListActivity = null;*/

    //播放状态
    public final int PLAY_STATE_PLAY=1;   //在播
    public final int PLAY_STATE_PAUSE=2;  //暂停
    public final int PLAY_STATE_STOP=3;   //未播

    public int mCurrentState = PLAY_STATE_STOP;   //默认状态是停止播放

    private Timer mTimer;
    private SeekTimeTask mTimeTask;

    public PlayerPresenter(MainActivity activity){
        mMainActivity = activity;
    }

/*    public PlayerPresenter(MusicListActivity musicListActivity){
        mMusicListActivity = musicListActivity;
    }*/

    @Override
    public void playOrPause(MainActivity.IsPlay playState) {
        if(mViewController == null){
            this.mViewController = mMainActivity.mPlayerViewControl;
        }

        if (mCurrentState == PLAY_STATE_STOP || playState == MainActivity.IsPlay.play) {
            try {
                if (mMediaPlayer == null)
                    mMediaPlayer = new MediaPlayer();
                //指定播放路径
                mMediaPlayer.setDataSource(ADDRESS + mMainActivity.playAddress);
                //准备播放
                mMediaPlayer.prepareAsync();
                //播放
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mCurrentState = PLAY_STATE_PLAY;
                startTimer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mCurrentState == PLAY_STATE_PLAY) {
            //如果当前的状态为播放，那么就暂停
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mCurrentState = PLAY_STATE_PAUSE;
                stopTimer();
            }
        } else if (mCurrentState == PLAY_STATE_PAUSE) {
            //如果当前的状态为暂停，那么继续播放
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mCurrentState = PLAY_STATE_PLAY;
                startTimer();
            }
        }

        mViewController.onPlayerStateChange(mCurrentState);
    }

    @Override
    public void playLast() {
        // 顺序播放
        if (mMainActivity.playPattern == mMainActivity.PLAY_IN_ORDER) {
            if (mMainActivity.musicId == 0) {
                mMainActivity.musicId = mMainActivity.songNum-1;
                mMainActivity.setMusicView(MainActivity.IsPlay.play);
            } else {
                mMainActivity.musicId = mMainActivity.musicId - 1;
                mMainActivity.setMusicView(MainActivity.IsPlay.play);
            }
        }

        //随机播放
        else if (mMainActivity.playPattern == mMainActivity.PLAY_RANDOM) {
            mMainActivity.musicId = ( mMainActivity.musicId+(int)(1+Math.random()*(20-1))) % mMainActivity.songNum ;
            mMainActivity.setMusicView(MainActivity.IsPlay.play);
        }
        //单曲循环
        else if(mMainActivity.musicId==mMainActivity.PLAY_SINGLE){
            mMainActivity.setMusicView(MainActivity.IsPlay.play);
        }
    }

    @Override
    public void playNext() {
        // 顺序播放
        if (mMainActivity.playPattern == mMainActivity.PLAY_IN_ORDER) {

            mMainActivity.musicId = (mMainActivity.musicId + 1) % mMainActivity.songNum;
            mMainActivity.setMusicView(MainActivity.IsPlay.play);

        }
        //随机播放
        else if (mMainActivity.playPattern == mMainActivity.PLAY_RANDOM) {
            mMainActivity.musicId = (mMainActivity.musicId+(int)(1+Math.random()*(20-1+1))) % mMainActivity.songNum ;
            mMainActivity.setMusicView(MainActivity.IsPlay.play);
        }
        //单曲循环
        else if(mMainActivity.playPattern == mMainActivity.PLAY_SINGLE){
            mMainActivity.setMusicView(MainActivity.IsPlay.play);
        }
    }

    @Override
    public void stopPlay() {
        if (mMediaPlayer != null ) {
            mMediaPlayer.stop();
            mCurrentState= PLAY_STATE_STOP;
            stopTimer();
            //更新播放状态
            if (mViewController != null) {
                mViewController.onPlayerStateChange(mCurrentState);
            }
            mMediaPlayer.release();//释放资源
            mMediaPlayer=null;
        }
    }

    @Override
    public void seekTo(int seek) {
        //0~100之间
        //需要做一个转换，得到的seek其实是一个百分比
        if (mMediaPlayer != null) {
            //getDuration()获取音频时长
            int tarSeek=(int)(seek*1f/100*mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(tarSeek);
        }
    }

    @Override
    public void playById(String mid) {
        int id = Integer.valueOf(mid).intValue() - 1;
        if (mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        //指定播放路径
        try {
            try {
                mMediaPlayer.setDataSource(ADDRESS + RequestServlet.getMusicList().getJSONObject(id).getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //准备播放
        mMediaPlayer.prepareAsync();
        //播放
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
        mCurrentState = PLAY_STATE_PLAY;

    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer=new Timer();
        }
        if (mTimeTask == null) {
            mTimeTask = new SeekTimeTask();
        }
        mTimer.schedule(mTimeTask,0,500);
    }
    private void stopTimer() {
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask=null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer=null;
        }
    }

    private class SeekTimeTask extends TimerTask {

        @Override
        public void run() {
            //获取当前的播放进度
            if (mMediaPlayer != null && mViewController!=null) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                //记录百分比
                int curPosition=(int)(currentPosition*1.0f/mMediaPlayer.getDuration()*100);
                if(curPosition<=100) {
                    mViewController.onSeekChange(curPosition);
                }
            }
        }
    }
}
