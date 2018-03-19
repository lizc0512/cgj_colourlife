package com.youmai.thirdbiz.colorful.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by colin on 2016/11/11.
 */

public class OwnerInfo implements Parcelable {

    /*{
        "code":0, "message":"\u67e5\u8be2\u6210\u529f", "content":{
        "owner":{
            "borthdate":"", "concataddress":"", "concatperson":"", "czhouse_num":"0", "dmakedate":
            "2016-07-20", "dr":"0", "email":"", "gender":"0", "house_num":"1", "isapp":
            "N", "islock":"N", "isowner":"Y", "isverify":"N", "jg":"\u798f\u5efa", "kzhouse_num":
            "0", "marrystate":"\u5df2\u5a5a", "mz":"\u6c49", "operator":
            "\u9648\u79c0\u82b3\uff08\u53a6\u95e8\uff09", "operator_job":
            "\u7ecf\u7406", "operator_username":"chenxiuf", "owner_uuid":
            "6eaaf9e9-742c-47a3-ba0d-33f728dd3241", "ownertype":"001", "personcardno":
            " ", "personcardtype":"\u8eab\u4efd\u8bc1", "phone":"15880075710", "telephone":"", "ts":
            "2016-07-20 14:22:26", "vdef1":"\u6f58", "vdef2":"\u51e4\u6e05", "vname":
            "\u6f58\u51e4\u6e05", "zzhouse_num":"1"
        },"house":[{
            "constarea":"0.00", "dr":"0", "floor":"1", "glfdj":"0.70", "house_uuid":
            "45daced2-00d7-49fc-8a4d-48d7dfd2c4d6", "housetype_uuid":
            "52f963ee-0376-11e6-8155-e247bfe54195", "housing_type":"0", "inarea":"0.00", "islock":
            "N", "moneyareanum":"110.93", "owner_uuid":
            "6eaaf9e9-742c-47a3-ba0d-33f728dd3241", "roomno":"101", "roomtype":
            "\u4e8c\u623f\u4e00\u5385", "salearea":"0.00", "smallarea_uuid":
            "54301a8e-0ebc-4493-a230-9f5a336c6e4b", "state":"\u6b63\u5e38", "ts":
            "2016-07-20 14:22:26", "unit_uuid":"32acffbe-5265-458e-ac81-065943381f75", "usestata":
            "", "wyarea":"0.00"
        }],"bank":[],"car":[],"family":[],"history":[]}
    }*/

    private int code;
    private String message;
    private ContentBean content;

    public boolean isSuccess() {
        return code == 0;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "OwnerInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }

    public static class ContentBean implements Parcelable {

        private OwnerBean owner;
        private List<HouseBean> house;

        public OwnerBean getOwner() {
            return owner;
        }

        public void setOwner(OwnerBean owner) {
            this.owner = owner;
        }

        public List<HouseBean> getHouse() {
            return house;
        }

        public void setHouse(List<HouseBean> house) {
            this.house = house;
        }


        public static class OwnerBean implements Parcelable {

            private String borthdate;
            private String concataddress;
            private String concatperson;
            private String czhouse_num;
            private String dmakedate;
            private String dr;
            private String email;
            private String gender;
            private String house_num;
            private String isapp;
            private String islock;
            private String isowner;
            private String isverify;
            private String jg;
            private String kzhouse_num;
            private String marrystate;
            private String mz;
            private String operator;
            private String operator_job;
            private String operator_username;
            private String owner_uuid;
            private String ownertype;
            private String personcardno;
            private String personcardtype;
            private String phone;
            private String telephone;
            private String ts;
            private String vdef1;
            private String vdef2;
            private String vname;
            private String zzhouse_num;


            public String getBorthdate() {
                return borthdate;
            }

            public void setBorthdate(String borthdate) {
                this.borthdate = borthdate;
            }

            public String getConcataddress() {
                return concataddress;
            }

            public void setConcataddress(String concataddress) {
                this.concataddress = concataddress;
            }

            public String getConcatperson() {
                return concatperson;
            }

            public void setConcatperson(String concatperson) {
                this.concatperson = concatperson;
            }

            public String getCzhouse_num() {
                return czhouse_num;
            }

            public void setCzhouse_num(String czhouse_num) {
                this.czhouse_num = czhouse_num;
            }

            public String getDmakedate() {
                return dmakedate;
            }

            public void setDmakedate(String dmakedate) {
                this.dmakedate = dmakedate;
            }

            public String getDr() {
                return dr;
            }

            public void setDr(String dr) {
                this.dr = dr;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getHouse_num() {
                return house_num;
            }

            public void setHouse_num(String house_num) {
                this.house_num = house_num;
            }

            public String getIsapp() {
                return isapp;
            }

            public void setIsapp(String isapp) {
                this.isapp = isapp;
            }

            public String getIslock() {
                return islock;
            }

            public void setIslock(String islock) {
                this.islock = islock;
            }

            public String getIsowner() {
                return isowner;
            }

            public void setIsowner(String isowner) {
                this.isowner = isowner;
            }

            public String getIsverify() {
                return isverify;
            }

            public void setIsverify(String isverify) {
                this.isverify = isverify;
            }

            public String getJg() {
                return jg;
            }

            public void setJg(String jg) {
                this.jg = jg;
            }

            public String getKzhouse_num() {
                return kzhouse_num;
            }

            public void setKzhouse_num(String kzhouse_num) {
                this.kzhouse_num = kzhouse_num;
            }

            public String getMarrystate() {
                return marrystate;
            }

            public void setMarrystate(String marrystate) {
                this.marrystate = marrystate;
            }

            public String getMz() {
                return mz;
            }

            public void setMz(String mz) {
                this.mz = mz;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getOperator_job() {
                return operator_job;
            }

            public void setOperator_job(String operator_job) {
                this.operator_job = operator_job;
            }

            public String getOperator_username() {
                return operator_username;
            }

            public void setOperator_username(String operator_username) {
                this.operator_username = operator_username;
            }

            public String getOwner_uuid() {
                return owner_uuid;
            }

            public void setOwner_uuid(String owner_uuid) {
                this.owner_uuid = owner_uuid;
            }

            public String getOwnertype() {
                return ownertype;
            }

            public void setOwnertype(String ownertype) {
                this.ownertype = ownertype;
            }

            public String getPersoncardno() {
                return personcardno;
            }

            public void setPersoncardno(String personcardno) {
                this.personcardno = personcardno;
            }

            public String getPersoncardtype() {
                return personcardtype;
            }

            public void setPersoncardtype(String personcardtype) {
                this.personcardtype = personcardtype;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getTelephone() {
                return telephone;
            }

            public void setTelephone(String telephone) {
                this.telephone = telephone;
            }

            public String getTs() {
                return ts;
            }

            public void setTs(String ts) {
                this.ts = ts;
            }

            public String getVdef1() {
                return vdef1;
            }

            public void setVdef1(String vdef1) {
                this.vdef1 = vdef1;
            }

            public String getVdef2() {
                return vdef2;
            }

            public void setVdef2(String vdef2) {
                this.vdef2 = vdef2;
            }

            public String getVname() {
                return vname;
            }

            public void setVname(String vname) {
                this.vname = vname;
            }

            public String getZzhouse_num() {
                return zzhouse_num;
            }

            public void setZzhouse_num(String zzhouse_num) {
                this.zzhouse_num = zzhouse_num;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.borthdate);
                dest.writeString(this.concataddress);
                dest.writeString(this.concatperson);
                dest.writeString(this.czhouse_num);
                dest.writeString(this.dmakedate);
                dest.writeString(this.dr);
                dest.writeString(this.email);
                dest.writeString(this.gender);
                dest.writeString(this.house_num);
                dest.writeString(this.isapp);
                dest.writeString(this.islock);
                dest.writeString(this.isowner);
                dest.writeString(this.isverify);
                dest.writeString(this.jg);
                dest.writeString(this.kzhouse_num);
                dest.writeString(this.marrystate);
                dest.writeString(this.mz);
                dest.writeString(this.operator);
                dest.writeString(this.operator_job);
                dest.writeString(this.operator_username);
                dest.writeString(this.owner_uuid);
                dest.writeString(this.ownertype);
                dest.writeString(this.personcardno);
                dest.writeString(this.personcardtype);
                dest.writeString(this.phone);
                dest.writeString(this.telephone);
                dest.writeString(this.ts);
                dest.writeString(this.vdef1);
                dest.writeString(this.vdef2);
                dest.writeString(this.vname);
                dest.writeString(this.zzhouse_num);
            }

            public OwnerBean() {
            }

            protected OwnerBean(Parcel in) {
                this.borthdate = in.readString();
                this.concataddress = in.readString();
                this.concatperson = in.readString();
                this.czhouse_num = in.readString();
                this.dmakedate = in.readString();
                this.dr = in.readString();
                this.email = in.readString();
                this.gender = in.readString();
                this.house_num = in.readString();
                this.isapp = in.readString();
                this.islock = in.readString();
                this.isowner = in.readString();
                this.isverify = in.readString();
                this.jg = in.readString();
                this.kzhouse_num = in.readString();
                this.marrystate = in.readString();
                this.mz = in.readString();
                this.operator = in.readString();
                this.operator_job = in.readString();
                this.operator_username = in.readString();
                this.owner_uuid = in.readString();
                this.ownertype = in.readString();
                this.personcardno = in.readString();
                this.personcardtype = in.readString();
                this.phone = in.readString();
                this.telephone = in.readString();
                this.ts = in.readString();
                this.vdef1 = in.readString();
                this.vdef2 = in.readString();
                this.vname = in.readString();
                this.zzhouse_num = in.readString();
            }

            public static final Parcelable.Creator<OwnerBean> CREATOR = new Parcelable.Creator<OwnerBean>() {
                @Override
                public OwnerBean createFromParcel(Parcel source) {
                    return new OwnerBean(source);
                }

                @Override
                public OwnerBean[] newArray(int size) {
                    return new OwnerBean[size];
                }
            };

            @Override
            public String toString() {
                return "OwnerBean{" +
                        "borthdate='" + borthdate + '\'' +
                        ", concataddress='" + concataddress + '\'' +
                        ", concatperson='" + concatperson + '\'' +
                        ", czhouse_num='" + czhouse_num + '\'' +
                        ", dmakedate='" + dmakedate + '\'' +
                        ", dr='" + dr + '\'' +
                        ", email='" + email + '\'' +
                        ", gender='" + gender + '\'' +
                        ", house_num='" + house_num + '\'' +
                        ", isapp='" + isapp + '\'' +
                        ", islock='" + islock + '\'' +
                        ", isowner='" + isowner + '\'' +
                        ", isverify='" + isverify + '\'' +
                        ", jg='" + jg + '\'' +
                        ", kzhouse_num='" + kzhouse_num + '\'' +
                        ", marrystate='" + marrystate + '\'' +
                        ", mz='" + mz + '\'' +
                        ", operator='" + operator + '\'' +
                        ", operator_job='" + operator_job + '\'' +
                        ", operator_username='" + operator_username + '\'' +
                        ", owner_uuid='" + owner_uuid + '\'' +
                        ", ownertype='" + ownertype + '\'' +
                        ", personcardno='" + personcardno + '\'' +
                        ", personcardtype='" + personcardtype + '\'' +
                        ", phone='" + phone + '\'' +
                        ", telephone='" + telephone + '\'' +
                        ", ts='" + ts + '\'' +
                        ", vdef1='" + vdef1 + '\'' +
                        ", vdef2='" + vdef2 + '\'' +
                        ", vname='" + vname + '\'' +
                        ", zzhouse_num='" + zzhouse_num + '\'' +
                        '}';
            }
        }

        public static class HouseBean implements Parcelable {
            private String constarea;
            private String dr;
            private String floor;
            private String glfdj;
            private String house_uuid;
            private String housetype_uuid;
            private String housing_type;
            private String inarea;
            private String islock;
            private String moneyareanum;
            private String owner_uuid;
            private String roomno;
            private String roomtype;
            private String salearea;
            private String smallarea_uuid;
            private String state;
            private String ts;
            private String unit_uuid;
            private String usestata;
            private String wyarea;

            public String getConstarea() {
                return constarea;
            }

            public void setConstarea(String constarea) {
                this.constarea = constarea;
            }

            public String getDr() {
                return dr;
            }

            public void setDr(String dr) {
                this.dr = dr;
            }

            public String getFloor() {
                return floor;
            }

            public void setFloor(String floor) {
                this.floor = floor;
            }

            public String getGlfdj() {
                return glfdj;
            }

            public void setGlfdj(String glfdj) {
                this.glfdj = glfdj;
            }

            public String getHouse_uuid() {
                return house_uuid;
            }

            public void setHouse_uuid(String house_uuid) {
                this.house_uuid = house_uuid;
            }

            public String getHousetype_uuid() {
                return housetype_uuid;
            }

            public void setHousetype_uuid(String housetype_uuid) {
                this.housetype_uuid = housetype_uuid;
            }

            public String getHousing_type() {
                return housing_type;
            }

            public void setHousing_type(String housing_type) {
                this.housing_type = housing_type;
            }

            public String getInarea() {
                return inarea;
            }

            public void setInarea(String inarea) {
                this.inarea = inarea;
            }

            public String getIslock() {
                return islock;
            }

            public void setIslock(String islock) {
                this.islock = islock;
            }

            public String getMoneyareanum() {
                return moneyareanum;
            }

            public void setMoneyareanum(String moneyareanum) {
                this.moneyareanum = moneyareanum;
            }

            public String getOwner_uuid() {
                return owner_uuid;
            }

            public void setOwner_uuid(String owner_uuid) {
                this.owner_uuid = owner_uuid;
            }

            public String getRoomno() {
                return roomno;
            }

            public void setRoomno(String roomno) {
                this.roomno = roomno;
            }

            public String getRoomtype() {
                return roomtype;
            }

            public void setRoomtype(String roomtype) {
                this.roomtype = roomtype;
            }

            public String getSalearea() {
                return salearea;
            }

            public void setSalearea(String salearea) {
                this.salearea = salearea;
            }

            public String getSmallarea_uuid() {
                return smallarea_uuid;
            }

            public void setSmallarea_uuid(String smallarea_uuid) {
                this.smallarea_uuid = smallarea_uuid;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getTs() {
                return ts;
            }

            public void setTs(String ts) {
                this.ts = ts;
            }

            public String getUnit_uuid() {
                return unit_uuid;
            }

            public void setUnit_uuid(String unit_uuid) {
                this.unit_uuid = unit_uuid;
            }

            public String getUsestata() {
                return usestata;
            }

            public void setUsestata(String usestata) {
                this.usestata = usestata;
            }

            public String getWyarea() {
                return wyarea;
            }

            public void setWyarea(String wyarea) {
                this.wyarea = wyarea;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.constarea);
                dest.writeString(this.dr);
                dest.writeString(this.floor);
                dest.writeString(this.glfdj);
                dest.writeString(this.house_uuid);
                dest.writeString(this.housetype_uuid);
                dest.writeString(this.housing_type);
                dest.writeString(this.inarea);
                dest.writeString(this.islock);
                dest.writeString(this.moneyareanum);
                dest.writeString(this.owner_uuid);
                dest.writeString(this.roomno);
                dest.writeString(this.roomtype);
                dest.writeString(this.salearea);
                dest.writeString(this.smallarea_uuid);
                dest.writeString(this.state);
                dest.writeString(this.ts);
                dest.writeString(this.unit_uuid);
                dest.writeString(this.usestata);
                dest.writeString(this.wyarea);
            }

            public HouseBean() {
            }

            protected HouseBean(Parcel in) {
                this.constarea = in.readString();
                this.dr = in.readString();
                this.floor = in.readString();
                this.glfdj = in.readString();
                this.house_uuid = in.readString();
                this.housetype_uuid = in.readString();
                this.housing_type = in.readString();
                this.inarea = in.readString();
                this.islock = in.readString();
                this.moneyareanum = in.readString();
                this.owner_uuid = in.readString();
                this.roomno = in.readString();
                this.roomtype = in.readString();
                this.salearea = in.readString();
                this.smallarea_uuid = in.readString();
                this.state = in.readString();
                this.ts = in.readString();
                this.unit_uuid = in.readString();
                this.usestata = in.readString();
                this.wyarea = in.readString();
            }

            public static final Parcelable.Creator<HouseBean> CREATOR = new Parcelable.Creator<HouseBean>() {
                @Override
                public HouseBean createFromParcel(Parcel source) {
                    return new HouseBean(source);
                }

                @Override
                public HouseBean[] newArray(int size) {
                    return new HouseBean[size];
                }
            };

            @Override
            public String toString() {
                return "HouseBean{" +
                        "constarea='" + constarea + '\'' +
                        ", dr='" + dr + '\'' +
                        ", floor='" + floor + '\'' +
                        ", glfdj='" + glfdj + '\'' +
                        ", house_uuid='" + house_uuid + '\'' +
                        ", housetype_uuid='" + housetype_uuid + '\'' +
                        ", housing_type='" + housing_type + '\'' +
                        ", inarea='" + inarea + '\'' +
                        ", islock='" + islock + '\'' +
                        ", moneyareanum='" + moneyareanum + '\'' +
                        ", owner_uuid='" + owner_uuid + '\'' +
                        ", roomno='" + roomno + '\'' +
                        ", roomtype='" + roomtype + '\'' +
                        ", salearea='" + salearea + '\'' +
                        ", smallarea_uuid='" + smallarea_uuid + '\'' +
                        ", state='" + state + '\'' +
                        ", ts='" + ts + '\'' +
                        ", unit_uuid='" + unit_uuid + '\'' +
                        ", usestata='" + usestata + '\'' +
                        ", wyarea='" + wyarea + '\'' +
                        '}';
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.owner, flags);
            dest.writeTypedList(this.house);
        }

        public ContentBean() {
        }

        protected ContentBean(Parcel in) {
            this.owner = in.readParcelable(OwnerBean.class.getClassLoader());
            this.house = in.createTypedArrayList(HouseBean.CREATOR);
        }

        public static final Parcelable.Creator<ContentBean> CREATOR = new Parcelable.Creator<ContentBean>() {
            @Override
            public ContentBean createFromParcel(Parcel source) {
                return new ContentBean(source);
            }

            @Override
            public ContentBean[] newArray(int size) {
                return new ContentBean[size];
            }
        };

        @Override
        public String toString() {
            return "ContentBean{" +
                    "owner=" + owner +
                    ", house=" + house +
                    '}';
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeParcelable(this.content, flags);
    }

    public OwnerInfo() {
    }

    protected OwnerInfo(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.content = in.readParcelable(ContentBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<OwnerInfo> CREATOR = new Parcelable.Creator<OwnerInfo>() {
        @Override
        public OwnerInfo createFromParcel(Parcel source) {
            return new OwnerInfo(source);
        }

        @Override
        public OwnerInfo[] newArray(int size) {
            return new OwnerInfo[size];
        }
    };

}
