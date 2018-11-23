package com.youmai.hxsdk.entity;


import com.youmai.hxsdk.proto.YouMaiVideo;

import java.util.List;

/**
 * Created by colin on 2018/3/21.
 */

public class VideoCall {

    private String roomName;
    private String token;
    private int groupId;
    private boolean isOwner;
    private boolean isAnchor;
    private int memberRole;
    private int videoType;
    private boolean isConference = true;
    private long msgTime; //消息时间
    private boolean state;

    private List<YouMaiVideo.RoomMemberItem> members;
    private String topic;
    private int count;


    private List<YouMaiVideo.RoomMemberItem> inviteMembers;
    private String adminId;

    public String getRoomName() {
        if (roomName == null) {
            roomName = "";
        }
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }


    public boolean isAnchor() {
        return isAnchor;
    }

    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
    }

    public int getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }


    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public List<YouMaiVideo.RoomMemberItem> getMembers() {
        return members;
    }

    public void setMembers(List<YouMaiVideo.RoomMemberItem> members) {
        this.members = members;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public boolean isConference() {
        return isConference;
    }

    public void setConference(boolean conference) {
        isConference = conference;
    }


    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


    public List<YouMaiVideo.RoomMemberItem> getInviteMembers() {
        return inviteMembers;
    }

    public void setInviteMembers(List<YouMaiVideo.RoomMemberItem> inviteMembers) {
        this.inviteMembers = inviteMembers;
    }


    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
