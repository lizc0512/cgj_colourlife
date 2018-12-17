package com.youmai.hxsdk.entity;

import com.youmai.hxsdk.proto.YouMaiVideo;

public class SingleVideoCall {
    private String roomName;
    private String token;
    private int videoType;
    private int memberRole;
    private boolean muteAgree;

    public boolean isMuteAgree() {
        return muteAgree;
    }

    public void setMuteAgree(boolean muteAgree) {
        this.muteAgree = muteAgree;
    }

    private YouMaiVideo.RoomMemberItem memberItem;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public int getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }

    public YouMaiVideo.RoomMemberItem getMemberItem() {
        return memberItem;
    }

    public void setMemberItem(YouMaiVideo.RoomMemberItem memberItem) {
        this.memberItem = memberItem;
    }
}
