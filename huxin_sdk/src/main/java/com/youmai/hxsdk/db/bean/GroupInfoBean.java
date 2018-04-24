package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupInfoBean implements Parcelable {

    @Id
    private Long id; //主键id

    private int group_id;
    private long info_update_time; //群资料最新的更新时间戳
    private long member_update_time; //群成员最新的更新时间戳
    private String group_name;
    private String owner_id;
    private String group_avatar; //头像
    private String topic;
    private int group_member_count;//群成员数
    private int fixtop_priority;    //非置顶为0，置顶次序依据值大小排序
    private boolean not_disturb;    //免打扰
    @Generated(hash = 1913825021)
    public GroupInfoBean(Long id, int group_id, long info_update_time,
            long member_update_time, String group_name, String owner_id,
            String group_avatar, String topic, int group_member_count,
            int fixtop_priority, boolean not_disturb) {
        this.id = id;
        this.group_id = group_id;
        this.info_update_time = info_update_time;
        this.member_update_time = member_update_time;
        this.group_name = group_name;
        this.owner_id = owner_id;
        this.group_avatar = group_avatar;
        this.topic = topic;
        this.group_member_count = group_member_count;
        this.fixtop_priority = fixtop_priority;
        this.not_disturb = not_disturb;
    }
    @Generated(hash = 1490267550)
    public GroupInfoBean() {
    }

    protected GroupInfoBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        group_id = in.readInt();
        info_update_time = in.readLong();
        member_update_time = in.readLong();
        group_name = in.readString();
        owner_id = in.readString();
        group_avatar = in.readString();
        topic = in.readString();
        group_member_count = in.readInt();
        fixtop_priority = in.readInt();
        not_disturb = in.readByte() != 0;
    }

    public static final Creator<GroupInfoBean> CREATOR = new Creator<GroupInfoBean>() {
        @Override
        public GroupInfoBean createFromParcel(Parcel in) {
            return new GroupInfoBean(in);
        }

        @Override
        public GroupInfoBean[] newArray(int size) {
            return new GroupInfoBean[size];
        }
    };

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getGroup_id() {
        return this.group_id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    public long getInfo_update_time() {
        return this.info_update_time;
    }
    public void setInfo_update_time(long info_update_time) {
        this.info_update_time = info_update_time;
    }
    public long getMember_update_time() {
        return this.member_update_time;
    }
    public void setMember_update_time(long member_update_time) {
        this.member_update_time = member_update_time;
    }
    public String getGroup_name() {
        return this.group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getOwner_id() {
        return this.owner_id;
    }
    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
    public String getGroup_avatar() {
        return this.group_avatar;
    }
    public void setGroup_avatar(String group_avatar) {
        this.group_avatar = group_avatar;
    }
    public String getTopic() {
        return this.topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public int getGroup_member_count() {
        return this.group_member_count;
    }
    public void setGroup_member_count(int group_member_count) {
        this.group_member_count = group_member_count;
    }
    public int getFixtop_priority() {
        return this.fixtop_priority;
    }
    public void setFixtop_priority(int fixtop_priority) {
        this.fixtop_priority = fixtop_priority;
    }
    public boolean getNot_disturb() {
        return this.not_disturb;
    }
    public void setNot_disturb(boolean not_disturb) {
        this.not_disturb = not_disturb;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(group_id);
        dest.writeLong(info_update_time);
        dest.writeLong(member_update_time);
        dest.writeString(group_name);
        dest.writeString(owner_id);
        dest.writeString(group_avatar);
        dest.writeString(topic);
        dest.writeInt(group_member_count);
        dest.writeInt(fixtop_priority);
        dest.writeByte((byte) (not_disturb ? 1 : 0));
    }
}
