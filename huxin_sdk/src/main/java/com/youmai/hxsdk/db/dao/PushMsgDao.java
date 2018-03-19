package com.youmai.hxsdk.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.youmai.hxsdk.db.bean.PushMsg;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PUSH_MSG".
*/
public class PushMsgDao extends AbstractDao<PushMsg, Long> {

    public static final String TABLENAME = "PUSH_MSG";

    /**
     * Properties of entity PushMsg.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Msg_id = new Property(1, int.class, "msg_id", false, "MSG_ID");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Text = new Property(3, String.class, "text", false, "TEXT");
        public final static Property Msg_type = new Property(4, int.class, "msg_type", false, "MSG_TYPE");
        public final static Property Publish_date = new Property(5, String.class, "publish_date", false, "PUBLISH_DATE");
        public final static Property Open_type = new Property(6, int.class, "open_type", false, "OPEN_TYPE");
        public final static Property Act_type = new Property(7, int.class, "act_type", false, "ACT_TYPE");
        public final static Property Url = new Property(8, String.class, "url", false, "URL");
        public final static Property H_img = new Property(9, String.class, "h_img", false, "H_IMG");
        public final static Property V_img = new Property(10, String.class, "v_img", false, "V_IMG");
        public final static Property Btn_name = new Property(11, String.class, "btn_name", false, "BTN_NAME");
        public final static Property Is_popup = new Property(12, boolean.class, "is_popup", false, "IS_POPUP");
        public final static Property Is_click = new Property(13, boolean.class, "is_click", false, "IS_CLICK");
        public final static Property Is_token = new Property(14, boolean.class, "is_token", false, "IS_TOKEN");
        public final static Property ExtraJson = new Property(15, String.class, "extraJson", false, "EXTRA_JSON");
        public final static Property Rec_time = new Property(16, long.class, "rec_time", false, "REC_TIME");
        public final static Property Is_read = new Property(17, boolean.class, "is_read", false, "IS_READ");
    }


    public PushMsgDao(DaoConfig config) {
        super(config);
    }
    
    public PushMsgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PUSH_MSG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"MSG_ID\" INTEGER NOT NULL ," + // 1: msg_id
                "\"TITLE\" TEXT," + // 2: title
                "\"TEXT\" TEXT," + // 3: text
                "\"MSG_TYPE\" INTEGER NOT NULL ," + // 4: msg_type
                "\"PUBLISH_DATE\" TEXT," + // 5: publish_date
                "\"OPEN_TYPE\" INTEGER NOT NULL ," + // 6: open_type
                "\"ACT_TYPE\" INTEGER NOT NULL ," + // 7: act_type
                "\"URL\" TEXT," + // 8: url
                "\"H_IMG\" TEXT," + // 9: h_img
                "\"V_IMG\" TEXT," + // 10: v_img
                "\"BTN_NAME\" TEXT," + // 11: btn_name
                "\"IS_POPUP\" INTEGER NOT NULL ," + // 12: is_popup
                "\"IS_CLICK\" INTEGER NOT NULL ," + // 13: is_click
                "\"IS_TOKEN\" INTEGER NOT NULL ," + // 14: is_token
                "\"EXTRA_JSON\" TEXT," + // 15: extraJson
                "\"REC_TIME\" INTEGER NOT NULL ," + // 16: rec_time
                "\"IS_READ\" INTEGER NOT NULL );"); // 17: is_read
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PUSH_MSG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PushMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMsg_id());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(4, text);
        }
        stmt.bindLong(5, entity.getMsg_type());
 
        String publish_date = entity.getPublish_date();
        if (publish_date != null) {
            stmt.bindString(6, publish_date);
        }
        stmt.bindLong(7, entity.getOpen_type());
        stmt.bindLong(8, entity.getAct_type());
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(9, url);
        }
 
        String h_img = entity.getH_img();
        if (h_img != null) {
            stmt.bindString(10, h_img);
        }
 
        String v_img = entity.getV_img();
        if (v_img != null) {
            stmt.bindString(11, v_img);
        }
 
        String btn_name = entity.getBtn_name();
        if (btn_name != null) {
            stmt.bindString(12, btn_name);
        }
        stmt.bindLong(13, entity.getIs_popup() ? 1L: 0L);
        stmt.bindLong(14, entity.getIs_click() ? 1L: 0L);
        stmt.bindLong(15, entity.getIs_token() ? 1L: 0L);
 
        String extraJson = entity.getExtraJson();
        if (extraJson != null) {
            stmt.bindString(16, extraJson);
        }
        stmt.bindLong(17, entity.getRec_time());
        stmt.bindLong(18, entity.getIs_read() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PushMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMsg_id());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(4, text);
        }
        stmt.bindLong(5, entity.getMsg_type());
 
        String publish_date = entity.getPublish_date();
        if (publish_date != null) {
            stmt.bindString(6, publish_date);
        }
        stmt.bindLong(7, entity.getOpen_type());
        stmt.bindLong(8, entity.getAct_type());
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(9, url);
        }
 
        String h_img = entity.getH_img();
        if (h_img != null) {
            stmt.bindString(10, h_img);
        }
 
        String v_img = entity.getV_img();
        if (v_img != null) {
            stmt.bindString(11, v_img);
        }
 
        String btn_name = entity.getBtn_name();
        if (btn_name != null) {
            stmt.bindString(12, btn_name);
        }
        stmt.bindLong(13, entity.getIs_popup() ? 1L: 0L);
        stmt.bindLong(14, entity.getIs_click() ? 1L: 0L);
        stmt.bindLong(15, entity.getIs_token() ? 1L: 0L);
 
        String extraJson = entity.getExtraJson();
        if (extraJson != null) {
            stmt.bindString(16, extraJson);
        }
        stmt.bindLong(17, entity.getRec_time());
        stmt.bindLong(18, entity.getIs_read() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PushMsg readEntity(Cursor cursor, int offset) {
        PushMsg entity = new PushMsg( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // msg_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // text
            cursor.getInt(offset + 4), // msg_type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // publish_date
            cursor.getInt(offset + 6), // open_type
            cursor.getInt(offset + 7), // act_type
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // url
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // h_img
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // v_img
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // btn_name
            cursor.getShort(offset + 12) != 0, // is_popup
            cursor.getShort(offset + 13) != 0, // is_click
            cursor.getShort(offset + 14) != 0, // is_token
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // extraJson
            cursor.getLong(offset + 16), // rec_time
            cursor.getShort(offset + 17) != 0 // is_read
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PushMsg entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMsg_id(cursor.getInt(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setText(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMsg_type(cursor.getInt(offset + 4));
        entity.setPublish_date(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOpen_type(cursor.getInt(offset + 6));
        entity.setAct_type(cursor.getInt(offset + 7));
        entity.setUrl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setH_img(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setV_img(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setBtn_name(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIs_popup(cursor.getShort(offset + 12) != 0);
        entity.setIs_click(cursor.getShort(offset + 13) != 0);
        entity.setIs_token(cursor.getShort(offset + 14) != 0);
        entity.setExtraJson(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setRec_time(cursor.getLong(offset + 16));
        entity.setIs_read(cursor.getShort(offset + 17) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PushMsg entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PushMsg entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PushMsg entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
