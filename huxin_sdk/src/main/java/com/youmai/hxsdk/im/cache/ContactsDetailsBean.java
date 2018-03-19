package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import com.youmai.hxsdk.db.bean.Card;
import com.youmai.hxsdk.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录添加联系人
 * Created by Administrator on 2017/2/22.
 */
public class ContactsDetailsBean implements Parcelable, JsonFormate<ContactsDetailsBean> {
    public static final int IM_CARD_SHARE_CONTACT_TYPE = 0;
    public static final int IM_CARD_SEND_CARD_TYPE = 1;
    public static final int IM_CARD_REPLY_CARD_TYPE = 2;

    private String name;
    private String company;
    private String job;
    private List<Phone> phone_list;
    private List<Email> email_list;
    private String remarks; //备注
    private List<Address> address_list;
    private List<WebSite> website_list;
    private List<IM> im_list;
    private List<Date> date_list;

    private int contact_id;
    private String iconUrl;//头像
    private int im_type;//标记是交互名片的对象还是转发联系人的对象

    private Card card;//名片额外信息

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    private String sex;

    public ContactsDetailsBean() {
    }

    public ContactsDetailsBean(String name, String company, String job, List<Phone> phone, List<Email> email, String remarks,
                               List<Address> address, List<WebSite> website, List<IM> im, List<Date> date) {
        this(name, company, job, phone, email, remarks, address, website, im, date, 0);
    }

    public ContactsDetailsBean(String name, String company, String job, List<Phone> phone, List<Email> email, String remarks,
                               List<Address> address, List<WebSite> website, List<IM> im, List<Date> date, int contact_id) {
        this.name = name;
        this.company = company;
        this.job = job;
        this.phone_list = phone;
        this.email_list = email;
        this.remarks = remarks;
        this.address_list = address;
        this.website_list = website;
        this.im_list = im;
        this.date_list = date;
        this.contact_id = contact_id;

    }


    protected ContactsDetailsBean(Parcel in) {
        name = in.readString();
        company = in.readString();
        job = in.readString();
        phone_list = in.createTypedArrayList(Phone.CREATOR);
        email_list = in.createTypedArrayList(Email.CREATOR);
        remarks = in.readString();
        address_list = in.createTypedArrayList(Address.CREATOR);
        website_list = in.createTypedArrayList(WebSite.CREATOR);
        im_list = in.createTypedArrayList(IM.CREATOR);
        date_list = in.createTypedArrayList(Date.CREATOR);
        contact_id = in.readInt();
        im_type = in.readInt();
        sex = in.readString();
        iconUrl = in.readString();
        card = in.readParcelable(Card.class.getClassLoader());


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(company);
        dest.writeString(job);
        dest.writeTypedList(phone_list);
        dest.writeTypedList(email_list);
        dest.writeString(remarks);
        dest.writeTypedList(address_list);
        dest.writeTypedList(website_list);
        dest.writeTypedList(im_list);
        dest.writeTypedList(date_list);
        dest.writeInt(contact_id);
        dest.writeInt(im_type);
        dest.writeString(sex);
        dest.writeString(iconUrl);
        dest.writeParcelable(card, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContactsDetailsBean> CREATOR = new Creator<ContactsDetailsBean>() {
        @Override
        public ContactsDetailsBean createFromParcel(Parcel in) {
            return new ContactsDetailsBean(in);
        }

        @Override
        public ContactsDetailsBean[] newArray(int size) {
            return new ContactsDetailsBean[size];
        }
    };

    public int getIm_type() {
        return im_type;
    }

    public void setIm_type(int im_type) {
        this.im_type = im_type;
    }


    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setPhone(List<Phone> phone) {
        this.phone_list = phone;
    }

    public void setEmail(List<Email> email) {
        this.email_list = email;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setAddress(List<Address> address) {
        this.address_list = address;
    }

    public void setWebsite(List<WebSite> website) {
        this.website_list = website;
    }

    public void setIm(List<IM> im) {
        this.im_list = im;
    }

    public void setDate(List<Date> date) {
        this.date_list = date;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getJob() {
        return job;
    }

    public List<Phone> getPhone() {
        return phone_list;
    }

    public List<Email> getEmail() {
        return email_list;
    }

    public String getRemarks() {
        return remarks;
    }

    public List<Address> getAddress() {
        return address_list;
    }

    public List<WebSite> getWebsite() {
        return website_list;
    }

    public List<IM> getIm() {
        return im_list;
    }

    public List<Date> getDate() {
        return date_list;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public ContactsDetailsBean fromJson(String jsonStr) {
        return GsonUtil.parse(jsonStr, ContactsDetailsBean.class);
    }


    public static class Phone implements Parcelable {
        private int phone_id;   //data表_id
        private int phone_type; // 跟随系统类型
        private String phone_num;

        public Phone() {
        }


        protected Phone(Parcel in) {
            phone_id = in.readInt();
            phone_type = in.readInt();
            phone_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(phone_id);
            dest.writeInt(phone_type);
            dest.writeString(phone_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Phone> CREATOR = new Creator<Phone>() {
            @Override
            public Phone createFromParcel(Parcel in) {
                return new Phone(in);
            }

            @Override
            public Phone[] newArray(int size) {
                return new Phone[size];
            }
        };

        public int getID() {
            return phone_id;
        }

        public void setID(int phone_id) {
            this.phone_id = phone_id;
        }

        public int getType() {
            return phone_type;
        }

        public void setType(int type) {
            this.phone_type = type;
        }

        public String getPhone() {
            return phone_num;
        }

        public void setPhone(String phone) {
            this.phone_num = phone;
        }

        public int getTypeIndex() {
            int index = 0;
            switch (phone_type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    index = 0;
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    index = 1;
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    index = 2;
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    index = 3;
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    index = 4;
                    break;
            }
            return index;
        }
    }

    public static class Email implements Parcelable {
        private int email_id;   //data表_id
        private int email_type; // 跟随系统类型
        private String email_num;

        public Email() {
        }


        protected Email(Parcel in) {
            email_id = in.readInt();
            email_type = in.readInt();
            email_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(email_id);
            dest.writeInt(email_type);
            dest.writeString(email_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Email> CREATOR = new Creator<Email>() {
            @Override
            public Email createFromParcel(Parcel in) {
                return new Email(in);
            }

            @Override
            public Email[] newArray(int size) {
                return new Email[size];
            }
        };

        public int getID() {
            return email_id;
        }

        public void setID(int email_id) {
            this.email_id = email_id;
        }

        public int getType() {
            return email_type;
        }

        public void setType(int type) {
            this.email_type = type;
        }

        public String getEmail() {
            return email_num;
        }

        public void setEmail(String email) {
            this.email_num = email;
        }

        public int getTypeIndex() {
            int index = 0;
            switch (email_type) {
                case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    index = 0;
                    break;
                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    index = 1;
                    break;
                case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                    index = 2;
                    break;
            }
            return index;
        }
    }

    public static class Address implements Parcelable {
        private int address_id;     //data表_id
        private int address_type;   // 跟随系统类型
        private String address_num;

        public Address() {
        }


        protected Address(Parcel in) {
            address_id = in.readInt();
            address_type = in.readInt();
            address_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(address_id);
            dest.writeInt(address_type);
            dest.writeString(address_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Address> CREATOR = new Creator<Address>() {
            @Override
            public Address createFromParcel(Parcel in) {
                return new Address(in);
            }

            @Override
            public Address[] newArray(int size) {
                return new Address[size];
            }
        };

        public int getID() {
            return address_id;
        }

        public void setID(int address_id) {
            this.address_id = address_id;
        }

        public int getType() {
            return address_type;
        }

        public void setType(int type) {
            this.address_type = type;
        }

        public String getAddress() {
            return address_num;
        }

        public void setAddress(String address) {
            this.address_num = address;
        }

        public int getTypeIndex() {
            int index = 0;
            switch (address_type) {
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                    index = 0;
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                    index = 1;
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                    index = 2;
                    break;
            }
            return index;
        }
    }

    public static class WebSite implements Parcelable {
        private int website_id;     //data表_id
        private String website_num;

        public WebSite() {
        }


        protected WebSite(Parcel in) {
            website_id = in.readInt();
            website_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(website_id);
            dest.writeString(website_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<WebSite> CREATOR = new Creator<WebSite>() {
            @Override
            public WebSite createFromParcel(Parcel in) {
                return new WebSite(in);
            }

            @Override
            public WebSite[] newArray(int size) {
                return new WebSite[size];
            }
        };

        public int getID() {
            return website_id;
        }

        public void setID(int website_id) {
            this.website_id = website_id;
        }

        public String getWebsite_num() {
            return website_num;
        }

        public void setWebsite_num(String website_num) {
            this.website_num = website_num;
        }


    }

    public static class IM implements Parcelable {
        private int im_id;      //data表_id
        private int im_type;    // 跟随系统类型
        private String im_num;

        public IM() {
        }


        protected IM(Parcel in) {
            im_id = in.readInt();
            im_type = in.readInt();
            im_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(im_id);
            dest.writeInt(im_type);
            dest.writeString(im_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<IM> CREATOR = new Creator<IM>() {
            @Override
            public IM createFromParcel(Parcel in) {
                return new IM(in);
            }

            @Override
            public IM[] newArray(int size) {
                return new IM[size];
            }
        };

        public int getID() {
            return im_id;
        }

        public void setID(int im_id) {
            this.im_id = im_id;
        }

        public int getType() {
            return im_type;
        }

        public void setType(int type) {
            this.im_type = type;
        }

        public String getIm() {
            return im_num;
        }

        public void setIm(String im) {
            this.im_num = im;
        }

        public int getTypeIndex() {
            int index = 0;
            switch (im_type) {
                case ContactsContract.CommonDataKinds.Im.TYPE_HOME:
                    index = 0;
                    break;
                case ContactsContract.CommonDataKinds.Im.TYPE_WORK:
                    index = 1;
                    break;
                case ContactsContract.CommonDataKinds.Im.TYPE_OTHER:
                    index = 2;
                    break;
            }
            return index;
        }
    }

    public static class Date implements Parcelable {
        private int date_id;    //data表_id
        private int date_type;  // 跟随系统类型
        private String date_num;

        public Date() {
        }


        protected Date(Parcel in) {
            date_id = in.readInt();
            date_type = in.readInt();
            date_num = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(date_id);
            dest.writeInt(date_type);
            dest.writeString(date_num);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Date> CREATOR = new Creator<Date>() {
            @Override
            public Date createFromParcel(Parcel in) {
                return new Date(in);
            }

            @Override
            public Date[] newArray(int size) {
                return new Date[size];
            }
        };

        public int getID() {
            return date_id;
        }

        public void setID(int date_id) {
            this.date_id = date_id;
        }

        public int getType() {
            return date_type;
        }

        public void setType(int type) {
            this.date_type = type;
        }

        public String getDate() {
            return date_num;
        }

        public void setDate(String date) {
            this.date_num = date;
        }

        public int getTypeIndex() {
            int index = 0;
            switch (date_type) {
                case ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY:
                    index = 0;
                    break;
                case ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM:
                    index = 1;
                    break;
                case ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY:
                    index = 2;
                    break;
                case ContactsContract.CommonDataKinds.Event.TYPE_OTHER:
                    index = 3;
                    break;
            }
            return index;
        }
    }

    public void filter() {

        if (phone_list != null) {
            for (int i = 0; i < this.phone_list.size() - 1; i++) {
                for (int j = this.phone_list.size() - 1; j > i; j--) {
                    if (this.phone_list.get(j).getPhone().equals(this.phone_list.get(i).getPhone())) {
                        this.phone_list.remove(j);
                    }
                }
            }
        }
        if (email_list != null) {
            for (int i = 0; i < this.email_list.size() - 1; i++) {
                for (int j = this.email_list.size() - 1; j > i; j--) {
                    if (this.email_list.get(j).getEmail().equals(this.email_list.get(i).getEmail())) {
                        this.email_list.remove(j);
                    }
                }
            }
        }
        if (date_list != null) {
            for (int i = 0; i < this.date_list.size() - 1; i++) {
                for (int j = this.date_list.size() - 1; j > i; j--) {
                    if (this.date_list.get(j).getDate().equals(this.date_list.get(i).getDate())) {
                        this.date_list.remove(j);
                    }
                }
            }
        }
        if (im_list != null) {
            for (int i = 0; i < this.im_list.size() - 1; i++) {
                for (int j = this.im_list.size() - 1; j > i; j--) {
                    if (this.im_list.get(j).getIm().equals(this.im_list.get(i).getIm())) {
                        this.im_list.remove(j);
                    }
                }
            }
        }
        if (website_list != null) {
            for (int i = 0; i < this.website_list.size() - 1; i++) {
                for (int j = this.website_list.size() - 1; j > i; j--) {
                    if (this.website_list.get(j).getWebsite_num().equals(this.website_list.get(i).getWebsite_num())) {
                        this.website_list.remove(j);
                    }
                }
            }
        }
    }

    public String getFirstPhone() {
        String phoneStr;
        if (phone_list != null && phone_list.size() > 0) {
            phoneStr = phone_list.get(0).getPhone();
        } else {
            phoneStr = "";
        }
        return phoneStr;
    }

    public String getFirstEmail() {
        String emailStr;
        if (email_list != null && email_list.size() > 0) {
            emailStr = email_list.get(0).getEmail();
        } else {
            emailStr = "";
        }
        return emailStr;
    }

    public String getFirstAddress() {
        String addressStr;
        if (address_list != null && address_list.size() > 0) {
            addressStr = address_list.get(0).getAddress();
        } else {
            addressStr = "";
        }
        return addressStr;
    }

    public void setFirstPhone(String phoneStr) {
        List<Phone> phoneList = new ArrayList<>();
        Phone phone = new Phone();
        phone.setPhone(phoneStr);
        phoneList.add(phone);
        phone_list = phoneList;
    }

    public void setFirstEmail(String emailStr) {
        List<Email> emailList = new ArrayList<>();
        Email phone = new Email();
        phone.setEmail(emailStr);
        emailList.add(phone);
        email_list = emailList;

    }

    public void setFirstAddress(String addressStr) {
        List<Address> phoneList = new ArrayList<>();
        Address address = new Address();
        address.setAddress(addressStr);
        phoneList.add(address);
        address_list = phoneList;
    }

    @Override
    public String toString() {
        return "ContactsDetailsBean{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", phone_list=" + phone_list +
                ", email_list=" + email_list +
                ", remarks='" + remarks + '\'' +
                ", address_list=" + address_list +
                ", website_list=" + website_list +
                ", im_list=" + im_list +
                ", date_list=" + date_list +
                ", contact_id=" + contact_id +
                ", card=" + card +
                ", iconUrl='" + iconUrl + '\'' +
                ", im_type=" + im_type +
                ", sex='" + sex + '\'' +
                '}';
    }
}