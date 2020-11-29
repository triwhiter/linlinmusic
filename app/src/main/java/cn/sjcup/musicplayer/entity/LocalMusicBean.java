package cn.sjcup.musicplayer.entity;

public class LocalMusicBean {
    private String id;
    private String song;
    private String singer;
    private String album;
    private String duration;

    public LocalMusicBean(String id, String song, String singer, String album, String duration) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
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
