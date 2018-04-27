package com.youmai.hxsdk.entity;

import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.27 15:05
 * 描述：
 */

public class GroupAndMember {

    private int group_id;
    private String group_name;
    private String owner_id;
    private String group_avatar; //头像
    private String topic;
    private long info_update_time; //群资料最新的更新时间戳
    private int group_member_count;//群成员数
    private int fixtop_priority;    //非置顶为0，置顶次序依据值大小排序
    private boolean not_disturb;    //免打扰

    private String groupJson;

    @Transient
    private List<Member> memberList;

    public static class Member {
        String member_id;
        String member_name;
        String user_name;
        int member_role;  //群成员角色(0-群主，1-管理员，2-普通成员)
    }

}
