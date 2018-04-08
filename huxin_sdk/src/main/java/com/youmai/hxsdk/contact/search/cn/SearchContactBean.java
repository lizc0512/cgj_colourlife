package com.youmai.hxsdk.contact.search.cn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by srsm
 */

public class SearchContactBean implements Comparable<SearchContactBean>, Parcelable {
    public static final int SEARCH_TYPE_NONE = 0x0000;
    public static final int SEARCH_TYPE_NUMBER = 0x0001;
    public static final int SEARCH_TYPE_NAME = 0x0002;
    public static final int SEARCH_TYPE_SIMPLE_SPELL = 0x0004;
    public static final int SEARCH_TYPE_WHOLE_SPECL = 0x0008;
    public static final int SEARCH_TYPE_SIMPLE_T9 = 0x0010;
    public static final int SEARCH_TYPE_WHOLE_T9 = 0x0020;
    public static final int SEARCH_TYPE_INFO = 0x0040;
    public static final Creator<SearchContactBean> CREATOR = new Creator<SearchContactBean>() {
        @Override
        public SearchContactBean createFromParcel(Parcel in) {
            return new SearchContactBean(in);
        }

        @Override
        public SearchContactBean[] newArray(int size) {
            return new SearchContactBean[size];
        }
    };
    private int contactId; //id
    private String displayName;//姓名
    private String phoneNum; // 电话号码
    private String wholePinyin = "#"; // 全拼
    private String wholeT9;//全拼对应T9
    private String simplepinyin;//简拼
    private String simplepT9;//简拼对应T9
    private int iconUrl;
    private String info;
    private long infoId;
    private String searchKey;
    private int searchType = SEARCH_TYPE_NONE;
    private DuoYinZi mDuoYinzi;
    private int[] wholePinYinFindIndex;
    private SearchContactBean nextSearchContactBean;

    public SearchContactBean() {

    }

    public SearchContactBean(int contactId, String name, String number, String simplePinyin, String pinyin, String searchStr, int type) {
        this.contactId = contactId;
        this.displayName = name;
        this.phoneNum = number;
        this.simplepinyin = simplePinyin;
        this.wholePinyin = pinyin;
        this.searchKey = searchStr;
        this.searchType = type;
    }

    public SearchContactBean(SearchContactBean bean, boolean hasNext) {
        this.contactId = bean.getContactId();
        this.displayName = bean.getDisplayName();
        this.phoneNum = bean.getPhoneNum();
        this.wholePinyin = bean.getWholePinyin();
        this.wholeT9 = bean.getWholeT9();
        this.simplepinyin = bean.getSimplepinyin();
        this.simplepT9 = bean.getSimpleT9();
        this.iconUrl = bean.getIconUrl();
        this.info = bean.getInfo();
        this.infoId = bean.getInfoId();
        this.searchKey = bean.getSearchKey();
        this.searchType = bean.getSearchType();
        this.mDuoYinzi = bean.getDuoYinzi();
        this.wholePinYinFindIndex = bean.getWholePinYinFindIndex();
        if (hasNext) {
            this.nextSearchContactBean = bean.getNextBean();
        }
    }

    protected SearchContactBean(Parcel in) {
        contactId = in.readInt();
        displayName = in.readString();
        phoneNum = in.readString();
        wholePinyin = in.readString();
        wholeT9 = in.readString();
        simplepinyin = in.readString();
        simplepT9 = in.readString();
        iconUrl = in.readInt();
        info = in.readString();
        infoId = in.readLong();
        searchKey = in.readString();
        searchType = in.readInt();
        mDuoYinzi = in.readParcelable(DuoYinZi.class.getClassLoader());
        wholePinYinFindIndex = in.createIntArray();
        nextSearchContactBean = in.readParcelable(SearchContactBean.class.getClassLoader());
    }

    public long getInfoId() {
        return infoId;
    }

    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWholeT9() {
        return wholeT9;
    }

    public void setWholeT9(String wholeT9) {
        this.wholeT9 = wholeT9;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public int getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getWholePinyin() {
        return wholePinyin;
    }

    public void setWholePinyin(String wholePinyin) {
        if (wholePinyin != null) {
            this.wholePinyin = wholePinyin;
        }
    }

    public String getSimplepinyin() {
        return simplepinyin;
    }

    public void setSimplepinyin(String pinyin) {
        this.simplepinyin = pinyin;
    }

    public String getSimpleT9() {
        return simplepT9;
    }

    public void setSimpleT9(String t9) {
        this.simplepT9 = t9;
    }

    public SearchContactBean getNextBean() {
        return nextSearchContactBean;
    }

    public void setNextBean(SearchContactBean bean) {
        this.nextSearchContactBean = bean;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "contactId=" + contactId +
                ", displayName='" + displayName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", wholePinyin='" + wholePinyin + '\'' +
                ", simplepinyin='" + simplepinyin + '\'' +
                '}';
    }

    @Override
    public int compareTo(SearchContactBean another) {
        return this.wholePinyin.compareTo(another.getWholePinyin());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(contactId);
        dest.writeString(displayName);
        dest.writeString(phoneNum);
        dest.writeString(wholePinyin);
        dest.writeString(wholeT9);
        dest.writeString(simplepinyin);
        dest.writeString(simplepT9);
        dest.writeInt(iconUrl);
        dest.writeString(info);
        dest.writeLong(infoId);
        dest.writeString(searchKey);
        dest.writeInt(searchType);
        dest.writeParcelable(mDuoYinzi,flags);
        dest.writeIntArray(wholePinYinFindIndex);
        dest.writeParcelable(nextSearchContactBean, flags);
    }

    public DuoYinZi getDuoYinzi() {
        return mDuoYinzi;
    }

    public void setDuoYinzi(DuoYinZi duoYinzi) {
        this.mDuoYinzi = duoYinzi;
    }

    public int[] getWholePinYinFindIndex() {
        return wholePinYinFindIndex;
    }

    public void setWholePinYinFindIndex(int[] wholePinYinFindIndex) {
        this.wholePinYinFindIndex = wholePinYinFindIndex;
    }
}
