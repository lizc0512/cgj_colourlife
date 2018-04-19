package com.youmai.hxsdk.entity.cn;

import android.os.Parcel;
import android.os.Parcelable;
import com.youmai.hxsdk.db.bean.Contact;

import java.util.ArrayList;
import java.util.List;

import static com.youmai.hxsdk.entity.cn.CNPinyinFactory.PRE_CHAR;

/**
 * Created by you on 2017/9/7.
 */
public class CNPinyin <T extends CN> implements Parcelable, Comparable<CNPinyin<T>> {

    private char[] CHARS = {'↑', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static List<Character> characters = new ArrayList<>();
    private List<String> headerFilter = new ArrayList<>();

    {
        headerFilter.add(new Contact("↑##@@**0 搜索").chinese());
        headerFilter.add(new Contact("↑##@@**1 组织架构").chinese());
        headerFilter.add(new Contact("↑##@@**2 我的部门").chinese());
        headerFilter.add(new Contact("↑##@@**3 手机联系人").chinese());
        headerFilter.add(new Contact("↑##@@**4 群聊").chinese());
        headerFilter.add(new Contact("↑##@@**5 收藏联系人").chinese());

        for (char ch: CHARS) {
            characters.add(ch);
        }
    }

    protected CNPinyin(Parcel in) {
        headerFilter = in.createStringArrayList();
        firstChar = (char) in.readInt();
        firstChars = in.readString();
        pinyins = in.createStringArray();
        pinyinsTotalLength = in.readInt();
    }

    public static final Creator<CNPinyin> CREATOR = new Creator<CNPinyin>() {
        @Override
        public CNPinyin createFromParcel(Parcel in) {
            return new CNPinyin(in);
        }

        @Override
        public CNPinyin[] newArray(int size) {
            return new CNPinyin[size];
        }
    };

    public List<String> getHeaderFilter() {
        return headerFilter;
    }

    /**
     * 对应首字首拼音字母
     */
    char firstChar;
    /**
     * 所有字符中的拼音首字母
     */
    String firstChars;
    /**
     * 对应的所有字母拼音
     */
    String[] pinyins;

    /**
     * 拼音总长度
     */
    int pinyinsTotalLength;

    public T data;

    CNPinyin(T data) {
        this.data = data;
    }

    public char getFirstChar() {
        return firstChar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("--firstChar--").append(firstChar).append("--pinyins:");
        for (String str : pinyins) {
            sb.append(str);
        }
        return sb.toString();
    }

    int compareValue() {
        if (!characters.contains(firstChar)) {
            return 'Z' + 1;
        }
        /*if (firstChar == DEF_CHAR) {
            return 'Z' + 1;
        }*/

        if (firstChar == PRE_CHAR && headerFilter.contains(data.chinese())) {
            return 'A' - 1;
        }
        return firstChar;
    }

    @Override
    public int compareTo(CNPinyin<T> tcnPinyin) {
        int compare = compareValue() - tcnPinyin.compareValue();
        if (compare == 0) {
            String chinese1 = data.chinese();
            String chinese2 = tcnPinyin.data.chinese();
            return chinese1.compareTo(chinese2);
        } else {

        }
        return compare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(headerFilter);
        dest.writeInt((int) firstChar);
        dest.writeString(firstChars);
        dest.writeStringArray(pinyins);
        dest.writeInt(pinyinsTotalLength);
    }
}
