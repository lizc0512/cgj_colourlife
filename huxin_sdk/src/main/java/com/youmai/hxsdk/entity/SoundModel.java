package com.youmai.hxsdk.entity;

/**
 * Created by youmai on 17/2/12.
 */

public class SoundModel {
    private String name;
    private String url;
    private int time;
    private boolean isCheck;
    private boolean isEditEnabled;
    private boolean isPlayer;
    public boolean isEditEnabled() {
        return isEditEnabled;
    }

    public void setEditEnabled(boolean editEnabled) {
        isEditEnabled = editEnabled;
    }


    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    private int soundId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SoundModel{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", time=" + time +
                ", soundId=" + soundId +
                '}';
    }
}
