package com.youmai.hxsdk.entity.cn;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

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

    private int contactId; //id
    private String displayName;//姓名
    private String phoneNum; // 电话号码
    private String wholePinyin = "#"; // 全拼
    private String wholeT9;//全拼对应T9
    private String simplepinyin;//简拼
    private String simplepT9;//简拼对应T9
    private String iconUrl;
    private String info; //App 应用信息 url
    private long infoId;
    private String searchKey;
    private int searchType = SEARCH_TYPE_NONE;
    private DuoYinZi mDuoYinzi;
    private int[] wholePinYinFindIndex;
    private SearchContactBean nextSearchContactBean;
    private List<String> indexPinyin;

    //联系人 信息
    private String username; //收藏联系人查询员工详情使用

    //app 应用信息
    private String oauthType = "";
    private String developerCode = "";
    private String clientCode = "";

    public SearchContactBean() {

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
        this.indexPinyin = bean.indexPinyin;
        this.username = bean.getUsername();
        this.oauthType = bean.getOauthType();
        this.developerCode = bean.getDeveloperCode();
        this.clientCode = bean.getClientCode();
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
        iconUrl = in.readString();
        info = in.readString();
        infoId = in.readLong();
        searchKey = in.readString();
        searchType = in.readInt();
        mDuoYinzi = in.readParcelable(DuoYinZi.class.getClassLoader());
        wholePinYinFindIndex = in.createIntArray();
        nextSearchContactBean = in.readParcelable(SearchContactBean.class.getClassLoader());
        indexPinyin = in.createStringArrayList();
        username = in.readString();
        oauthType = in.readString();
        developerCode = in.readString();
        clientCode = in.readString();
    }

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
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

    public void setIndexPinyin(List<String> indexPinyin) {
        this.indexPinyin = indexPinyin;
    }

    public List<String> getIndexPinyin() {
        return indexPinyin;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getOauthType() {
        return oauthType;
    }

    public void setOauthType(String oauthType) {
        this.oauthType = oauthType;
    }

    public String getDeveloperCode() {
        return developerCode;
    }

    public void setDeveloperCode(String developerCode) {
        this.developerCode = developerCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    @Override
    public String toString() {
        return "SearchContactBean{" +
                "contactId=" + contactId +
                ", displayName='" + displayName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", wholePinyin='" + wholePinyin + '\'' +
                ", wholeT9='" + wholeT9 + '\'' +
                ", simplepinyin='" + simplepinyin + '\'' +
                ", simplepT9='" + simplepT9 + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", info='" + info + '\'' +
                ", infoId=" + infoId +
                ", searchKey='" + searchKey + '\'' +
                ", searchType=" + searchType +
                ", mDuoYinzi=" + mDuoYinzi +
                ", wholePinYinFindIndex=" + Arrays.toString(wholePinYinFindIndex) +
                ", nextSearchContactBean=" + nextSearchContactBean +
                ", indexPinyin=" + indexPinyin +
                ", username='" + username + '\'' +
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
        dest.writeString(iconUrl);
        dest.writeString(info);
        dest.writeLong(infoId);
        dest.writeString(searchKey);
        dest.writeInt(searchType);
        dest.writeParcelable(mDuoYinzi, flags);
        dest.writeIntArray(wholePinYinFindIndex);
        dest.writeParcelable(nextSearchContactBean, flags);
        dest.writeString(username);
        dest.writeString(oauthType);
        dest.writeString(developerCode);
        dest.writeString(clientCode);
    }

}
