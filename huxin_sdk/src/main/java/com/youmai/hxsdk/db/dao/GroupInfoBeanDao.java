package com.youmai.hxsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.youmai.hxsdk.db.bean.GroupInfoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP_INFO_BEAN".
*/
public class GroupInfoBeanDao extends AbstractDao<GroupInfoBean, Long> {

    public static final String TABLENAME = "GROUP_INFO_BEAN";

    /**
     * Properties of entity GroupInfoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Group_id = new Property(1, int.class, "group_id", false, "GROUP_ID");
        public final static Property Info_update_time = new Property(2, long.class, "info_update_time", false, "INFO_UPDATE_TIME");
        public final static Property Member_update_time = new Property(3, long.class, "member_update_time", false, "MEMBER_UPDATE_TIME");
        public final static Property Group_name = new Property(4, String.class, "group_name", false, "GROUP_NAME");
        public final static Property Owner_id = new Property(5, String.class, "owner_id", false, "OWNER_ID");
        public final static Property Group_avatar = new Property(6, String.class, "group_avatar", false, "GROUP_AVATAR");
        public final static Property Topic = new Property(7, String.class, "topic", false, "TOPIC");
        public final static Property Group_member_count = new Property(8, int.class, "group_member_count", false, "GROUP_MEMBER_COUNT");
        public final static Property Fixtop_priority = new Property(9, int.class, "fixtop_priority", false, "FIXTOP_PRIORITY");
        public final static Property Not_disturb = new Property(10, boolean.class, "not_disturb", false, "NOT_DISTURB");
    }


    public GroupInfoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public GroupInfoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_INFO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"GROUP_ID\" INTEGER NOT NULL ," + // 1: group_id
                "\"INFO_UPDATE_TIME\" INTEGER NOT NULL ," + // 2: info_update_time
                "\"MEMBER_UPDATE_TIME\" INTEGER NOT NULL ," + // 3: member_update_time
                "\"GROUP_NAME\" TEXT," + // 4: group_name
                "\"OWNER_ID\" TEXT," + // 5: owner_id
                "\"GROUP_AVATAR\" TEXT," + // 6: group_avatar
                "\"TOPIC\" TEXT," + // 7: topic
                "\"GROUP_MEMBER_COUNT\" INTEGER NOT NULL ," + // 8: group_member_count
                "\"FIXTOP_PRIORITY\" INTEGER NOT NULL ," + // 9: fixtop_priority
                "\"NOT_DISTURB\" INTEGER NOT NULL );"); // 10: not_disturb
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_INFO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GroupInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGroup_id());
        stmt.bindLong(3, entity.getInfo_update_time());
        stmt.bindLong(4, entity.getMember_update_time());
 
        String group_name = entity.getGroup_name();
        if (group_name != null) {
            stmt.bindString(5, group_name);
        }
 
        String owner_id = entity.getOwner_id();
        if (owner_id != null) {
            stmt.bindString(6, owner_id);
        }
 
        String group_avatar = entity.getGroup_avatar();
        if (group_avatar != null) {
            stmt.bindString(7, group_avatar);
        }
 
        String topic = entity.getTopic();
        if (topic != null) {
            stmt.bindString(8, topic);
        }
        stmt.bindLong(9, entity.getGroup_member_count());
        stmt.bindLong(10, entity.getFixtop_priority());
        stmt.bindLong(11, entity.getNot_disturb() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GroupInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGroup_id());
        stmt.bindLong(3, entity.getInfo_update_time());
        stmt.bindLong(4, entity.getMember_update_time());
 
        String group_name = entity.getGroup_name();
        if (group_name != null) {
            stmt.bindString(5, group_name);
        }
 
        String owner_id = entity.getOwner_id();
        if (owner_id != null) {
            stmt.bindString(6, owner_id);
        }
 
        String group_avatar = entity.getGroup_avatar();
        if (group_avatar != null) {
            stmt.bindString(7, group_avatar);
        }
 
        String topic = entity.getTopic();
        if (topic != null) {
            stmt.bindString(8, topic);
        }
        stmt.bindLong(9, entity.getGroup_member_count());
        stmt.bindLong(10, entity.getFixtop_priority());
        stmt.bindLong(11, entity.getNot_disturb() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public GroupInfoBean readEntity(Cursor cursor, int offset) {
        GroupInfoBean entity = new GroupInfoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // group_id
            cursor.getLong(offset + 2), // info_update_time
            cursor.getLong(offset + 3), // member_update_time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // group_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // owner_id
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // group_avatar
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // topic
            cursor.getInt(offset + 8), // group_member_count
            cursor.getInt(offset + 9), // fixtop_priority
            cursor.getShort(offset + 10) != 0 // not_disturb
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GroupInfoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGroup_id(cursor.getInt(offset + 1));
        entity.setInfo_update_time(cursor.getLong(offset + 2));
        entity.setMember_update_time(cursor.getLong(offset + 3));
        entity.setGroup_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOwner_id(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGroup_avatar(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTopic(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setGroup_member_count(cursor.getInt(offset + 8));
        entity.setFixtop_priority(cursor.getInt(offset + 9));
        entity.setNot_disturb(cursor.getShort(offset + 10) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GroupInfoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GroupInfoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GroupInfoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}