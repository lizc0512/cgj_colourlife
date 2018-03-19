package com.youmai.hxsdk.socket;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IMContentUtil {

    private String msgBody;
    /**
     * start from 0.
     */
    private int position = 0;
    private List<IMContentItem> contentList;


    public IMContentUtil() {
        contentList = new ArrayList<>();
        msgBody = "";
    }

    public IMContentUtil(String _msgBody) {
        contentList = new ArrayList<>();
        msgBody = _msgBody;
    }

    public void parseBody(String _msgBody) {
        msgBody = _msgBody;
        parseBody();
    }

    public void parseBody() {
        try {
            JSONArray array = new JSONArray(msgBody);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Iterator it = obj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = obj.getString(key);
                    IMContentItem item = new IMContentItem();
                    item.item = value;
                    item.itemType = IMContentType.valueOf(key.toString());
                    contentList.add(item);

                    Log.d("json", key + " val:" + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String serializeToString() {
        JSONArray arr = new JSONArray();
        try {
            for (int i = 0; i < contentList.size(); i++) {
                IMContentItem item = contentList.get(i);
                JSONObject obj = new JSONObject();
                obj.put(item.itemType.toString(), item.item);
                arr.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr.toString();
    }

    public String serializeBigFileToString() {
        JSONArray arr = new JSONArray();
        try {
            for (int i = 0; i < contentList.size(); i++) {
                IMContentItem item = contentList.get(i);
                JSONObject obj = new JSONObject();
                obj.put(item.itemType.toString(), item.item);
                obj.put(IMContentType.CONTENT_FILE_NAME.toString(), item.fileName);
                obj.put(IMContentType.CONTENT_FILE_SIZE.toString(), item.fileSize);
                arr.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr.toString();
    }

    private void addItem(IMContentType _type, String _item) {
        IMContentItem item = new IMContentItem();
        item.item = _item;
        item.itemType = _type;
        contentList.add(item);
    }

    /**
     * insert some text into IM Body.
     *
     * @param _text
     */
    public void appendText(String _text) {
        if (contentList.size() > 0) {
            IMContentItem item = contentList.get(contentList.size() - 1);
            if (item.itemType == IMContentType.CONTENT_TEXT) {
                item.item += _text;
                contentList.set(contentList.size() - 1, item);
            } else {
                addItem(IMContentType.CONTENT_TEXT, _text);
            }
        } else {
            addItem(IMContentType.CONTENT_TEXT, _text);
        }
    }

    public void appendImage(String _imageUrl) {
        addItem(IMContentType.CONTENT_IMAGE, _imageUrl);
    }

    public void appendFile(String _fileUrl) {
        addItem(IMContentType.CONTENT_FILE, _fileUrl);
    }

    public void appendUrl(String _url) {
        addItem(IMContentType.CONTENT_URL, _url);
    }

    public void appendPhone(String _phone) {
        addItem(IMContentType.CONTENT_PHONE, _phone);
    }

    public void appendSessionId(String _sessionId) {
        addItem(IMContentType.CONTEXT_SESSIONID, _sessionId);
    }

    public void appendMsgid(String _MsgId) {
        addItem(IMContentType.CONTEXT_MSGID, _MsgId);
    }

    public void appendPictureId(String _PictureId) {
        addItem(IMContentType.CONTEXT_PICTURE_ID, _PictureId);
    }

    public void appendAudioId(String _AudioId) {
        addItem(IMContentType.CONTEXT_AUDIO_ID, _AudioId);
    }

    public void appendVideoId(String _videoId) {
        addItem(IMContentType.CONTEXT_VIDEO_ID, _videoId);
    }

    public void appendBigFileId(String _fileId, String fileName, String fileSize) {
        IMContentItem item = new IMContentItem();
        item.item = _fileId;
        item.itemType = IMContentType.CONTENT_FILE;
        item.fileName = fileName;
        item.fileSize = fileSize;
        contentList.add(item);
    }

    public void appendTitle(String _Title) {
        addItem(IMContentType.CONTEXT_TITLE, _Title);
    }

    public void appendDescribe(String _desrcibe) {
        addItem(IMContentType.CONTEXT_DESCRIBE, _desrcibe);
    }

    public void appendLongitude(String _longitude) {
        addItem(IMContentType.CONTEXT_LONGITUDE, _longitude);
    }

    public void appendLaitude(String _laitude) {
        addItem(IMContentType.CONTEXT_LAITUDE, _laitude);
    }

    public void appendScale(String _scale) {
        addItem(IMContentType.CONTEXT_SCALE, _scale);
    }

    public void appendLabel(String _label) {
        addItem(IMContentType.CONTEXT_LABEL, _label);
    }

    public void appendCallState(String _call_state) {
        addItem(IMContentType.CONTEXT_CALL_STATE, _call_state);
    }

    public void appendCallDirection(String _call_direction) {
        addItem(IMContentType.CONTEXT_CALL_DIRECTION, _call_direction);
    }

    public void appendLocAnswer(String _loc_answer) {
        addItem(IMContentType.CONTEXT_LOC_ANSWER, _loc_answer);
    }

    public void appendLocAnswerOrReject(String _loc_status) {
        addItem(IMContentType.CONTEXT_ANSWER_REJECT, _loc_status);
    }

    public void appendPassword(String _password) {
        addItem(IMContentType.CONTEXT_PASSWORD, _password);
    }

    public void appendAppKey(String _appkey) {
        addItem(IMContentType.CONTEXT_APPKEY, _appkey);
    }

    public void appendDirection(String _direction) {
        addItem(IMContentType.CONTEXT_DIRECTION, _direction);
    }

    public void appendTextType(String _text_type) {
        addItem(IMContentType.CONTEXT_TEXT_TYPE, _text_type);
    }

    public void appendBarTime(String _bar_time) { //  Burn after reading time
        addItem(IMContentType.CONTEXT_BAR_TIME, _bar_time);
    }

    public void appendFileName(String _file_name) { //  Burn after reading time
        addItem(IMContentType.CONTENT_FILE_NAME, _file_name);
    }

    public void appendFileSize(String _file_size) { //  Burn after reading time
        addItem(IMContentType.CONTENT_FILE_SIZE, _file_size);
    }

    public void appendSourcePhone(String _source_phone) {
        addItem(IMContentType.CONTEXT_SOURCE_PHONE, _source_phone);
    }

    public void appendForwardCount(String _forward_count) {
        addItem(IMContentType.CONTEXT_FORWARD_COUNT, _forward_count);
    }

    public void addVideo(String videoId, String frameId, String name, String size, String time) {
        appendVideoId(videoId);
        appendPictureId(frameId);
        appendFileName(name);
        appendFileSize(size);
        appendBarTime(time);
    }

    /**
     * insert at some one.
     *
     * @param _atBody user_id;organ_id
     */
    public void appendAT(String _atBody) {
        addItem(IMContentType.CONTENT_AT, _atBody);
    }

    /**
     * set current position 0.
     */
    public void reset() {
        position = 0;
    }

    /**
     * @return if, has unread IMContent, return next contentType.
     * else return null.
     */
    public IMContentType hasNext() {
        if (contentList.size() > position) {
            return contentList.get(position).itemType;
        }
        return null;
    }

    public int readContentType(IMContentType _type, List<String> _out_data) {

        for (int i = 0; i < contentList.size() && _out_data != null; i++) {
            if (contentList.get(i).itemType == _type) {
                _out_data.add(contentList.get(i).item);
                return i;
            }
        }
        _out_data.add("");

        return 0;
    }

    /**
     * read next element. current position will actual move to next.
     *
     * @return if has return element.
     * else return null.
     */
    public String readNext() {

        if (contentList.size() > position) {

            return contentList.get(position++).item;
        }

        return null;
    }

    /**
     * remove from end. delete one char or rich element.
     *
     * @return if and only succeed delete one, return true.
     */
    @SuppressLint("NewApi")
    public boolean remove() {
        if (contentList.isEmpty()) {
            return false;
        } else {
            IMContentItem item = contentList.get(contentList.size() - 1);
            if (item.itemType != IMContentType.CONTENT_TEXT) {
                contentList.remove(contentList.size() - 1);
                return true;
            } else {// this is ContentText info.
                if (item.item.isEmpty()) {
                    contentList.remove(contentList.size() - 1);
                    return true;
                } else {
                    item.item = item.item.substring(0, item.item.length() - 1);
                    contentList.remove(contentList.size() - 1);
                    contentList.add(item);
                    return true;
                }
            }
        }
    }

    /**
     * "helllo[ContentPicture],world"
     * "[ContentPicture]helllo,world"
     *
     * @param caretPos start from 0. caret treat rich element as a whole.
     * @return if, and only remove one char or a element at caretpos, return true;
     */
    public boolean removeAtIndex(int caretPos) {
        int lookuploop = 0;
        for (int i = 0; i < contentList.size(); i++) {
            IMContentItem item = contentList.get(i);
            if (item.itemType != IMContentType.CONTENT_TEXT) {
                if (lookuploop == caretPos) {
                    // got it.
                    contentList.remove(i);
                    return true;
                }
                ++lookuploop;
            } else { // ContentText
                if (lookuploop + item.item.length() > caretPos) {
                    // it contains.
                    String newText = item.item.substring(0, caretPos - lookuploop);
                    if (item.item.length() > caretPos - lookuploop + 1) {
                        newText += item.item.substring(caretPos - lookuploop + 1);
                    }

                    item.item = newText;
                    contentList.set(i, item);
                    return true;
                } else {
                    lookuploop += item.item.length();
                }
            }

        }

        return false;
    }


    public static int getContentType(int _val, int _type) {
        _val = _val | (1 << _type);

        return _val;
    }

    public static boolean HasContentType(int _val, int _type) {
        return (_val & (1 << _type)) != 0;
    }
}
