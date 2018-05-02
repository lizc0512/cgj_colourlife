package com.youmai.hxsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.youmai.hxsdk.db.bean.ContactBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONTACT_BEAN".
*/
public class ContactBeanDao extends AbstractDao<ContactBean, Long> {

    public static final String TABLENAME = "CONTACT_BEAN";

    /**
     * Properties of entity ContactBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property Uid = new Property(2, String.class, "uid", false, "UID");
        public final static Property Mobile = new Property(3, String.class, "mobile", false, "MOBILE");
        public final static Property Realname = new Property(4, String.class, "realname", false, "REALNAME");
        public final static Property Avatar = new Property(5, String.class, "avatar", false, "AVATAR");
        public final static Property Sex = new Property(6, String.class, "sex", false, "SEX");
        public final static Property Email = new Property(7, String.class, "email", false, "EMAIL");
        public final static Property IsFavorite = new Property(8, String.class, "isFavorite", false, "IS_FAVORITE");
        public final static Property JobName = new Property(9, String.class, "jobName", false, "JOB_NAME");
        public final static Property Landline = new Property(10, String.class, "landline", false, "LANDLINE");
        public final static Property OrgID = new Property(11, String.class, "orgID", false, "ORG_ID");
        public final static Property OrgName = new Property(12, String.class, "orgName", false, "ORG_NAME");
        public final static Property Username = new Property(13, String.class, "username", false, "USERNAME");
        public final static Property MemberRole = new Property(14, int.class, "memberRole", false, "MEMBER_ROLE");
        public final static Property Sign = new Property(15, String.class, "sign", false, "SIGN");
        public final static Property Is_hx = new Property(16, boolean.class, "is_hx", false, "IS_HX");
        public final static Property Pinyin = new Property(17, String.class, "pinyin", false, "PINYIN");
        public final static Property SimplePinyin = new Property(18, String.class, "simplePinyin", false, "SIMPLE_PINYIN");
    }


    public ContactBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ContactBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONTACT_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"UUID\" TEXT," + // 1: uuid
                "\"UID\" TEXT," + // 2: uid
                "\"MOBILE\" TEXT," + // 3: mobile
                "\"REALNAME\" TEXT," + // 4: realname
                "\"AVATAR\" TEXT," + // 5: avatar
                "\"SEX\" TEXT," + // 6: sex
                "\"EMAIL\" TEXT," + // 7: email
                "\"IS_FAVORITE\" TEXT," + // 8: isFavorite
                "\"JOB_NAME\" TEXT," + // 9: jobName
                "\"LANDLINE\" TEXT," + // 10: landline
                "\"ORG_ID\" TEXT," + // 11: orgID
                "\"ORG_NAME\" TEXT," + // 12: orgName
                "\"USERNAME\" TEXT," + // 13: username
                "\"MEMBER_ROLE\" INTEGER NOT NULL ," + // 14: memberRole
                "\"SIGN\" TEXT," + // 15: sign
                "\"IS_HX\" INTEGER NOT NULL ," + // 16: is_hx
                "\"PINYIN\" TEXT," + // 17: pinyin
                "\"SIMPLE_PINYIN\" TEXT);"); // 18: simplePinyin
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONTACT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ContactBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(3, uid);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(4, mobile);
        }
 
        String realname = entity.getRealname();
        if (realname != null) {
            stmt.bindString(5, realname);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(8, email);
        }
 
        String isFavorite = entity.getIsFavorite();
        if (isFavorite != null) {
            stmt.bindString(9, isFavorite);
        }
 
        String jobName = entity.getJobName();
        if (jobName != null) {
            stmt.bindString(10, jobName);
        }
 
        String landline = entity.getLandline();
        if (landline != null) {
            stmt.bindString(11, landline);
        }
 
        String orgID = entity.getOrgID();
        if (orgID != null) {
            stmt.bindString(12, orgID);
        }
 
        String orgName = entity.getOrgName();
        if (orgName != null) {
            stmt.bindString(13, orgName);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(14, username);
        }
        stmt.bindLong(15, entity.getMemberRole());
 
        String sign = entity.getSign();
        if (sign != null) {
            stmt.bindString(16, sign);
        }
        stmt.bindLong(17, entity.getIs_hx() ? 1L: 0L);
 
        String pinyin = entity.getPinyin();
        if (pinyin != null) {
            stmt.bindString(18, pinyin);
        }
 
        String simplePinyin = entity.getSimplePinyin();
        if (simplePinyin != null) {
            stmt.bindString(19, simplePinyin);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ContactBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(3, uid);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(4, mobile);
        }
 
        String realname = entity.getRealname();
        if (realname != null) {
            stmt.bindString(5, realname);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(8, email);
        }
 
        String isFavorite = entity.getIsFavorite();
        if (isFavorite != null) {
            stmt.bindString(9, isFavorite);
        }
 
        String jobName = entity.getJobName();
        if (jobName != null) {
            stmt.bindString(10, jobName);
        }
 
        String landline = entity.getLandline();
        if (landline != null) {
            stmt.bindString(11, landline);
        }
 
        String orgID = entity.getOrgID();
        if (orgID != null) {
            stmt.bindString(12, orgID);
        }
 
        String orgName = entity.getOrgName();
        if (orgName != null) {
            stmt.bindString(13, orgName);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(14, username);
        }
        stmt.bindLong(15, entity.getMemberRole());
 
        String sign = entity.getSign();
        if (sign != null) {
            stmt.bindString(16, sign);
        }
        stmt.bindLong(17, entity.getIs_hx() ? 1L: 0L);
 
        String pinyin = entity.getPinyin();
        if (pinyin != null) {
            stmt.bindString(18, pinyin);
        }
 
        String simplePinyin = entity.getSimplePinyin();
        if (simplePinyin != null) {
            stmt.bindString(19, simplePinyin);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ContactBean readEntity(Cursor cursor, int offset) {
        ContactBean entity = new ContactBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // uid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // mobile
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // realname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // avatar
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sex
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // email
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // isFavorite
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // jobName
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // landline
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // orgID
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // orgName
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // username
            cursor.getInt(offset + 14), // memberRole
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // sign
            cursor.getShort(offset + 16) != 0, // is_hx
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // pinyin
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18) // simplePinyin
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ContactBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMobile(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRealname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAvatar(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSex(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setEmail(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIsFavorite(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setJobName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLandline(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setOrgID(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setOrgName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUsername(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setMemberRole(cursor.getInt(offset + 14));
        entity.setSign(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setIs_hx(cursor.getShort(offset + 16) != 0);
        entity.setPinyin(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setSimplePinyin(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ContactBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ContactBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ContactBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
