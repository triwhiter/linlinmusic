package cn.sjcup.musicplayer.entity;

import com.loopj.android.image.SmartImageView;

public class LocalMusicBean {
    private String id;
    private String song;
    private String singer;
    private String album;
    private String picture;
    private String duration;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public LocalMusicBean(String id, String song, String singer, String album, String picture, String duration) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.picture = picture;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalMusicBean() {
    }
}
