package com.luozenglin.common;

public class MusicItem {
    private String title;
    private String singer;
    private  int length;
    private String path;
    private boolean isPlaying = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String musicTitle) {
        this.title = musicTitle;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
