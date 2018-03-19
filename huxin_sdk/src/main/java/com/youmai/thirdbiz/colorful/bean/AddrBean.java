package com.youmai.thirdbiz.colorful.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class AddrBean implements Serializable {

    public int code;
    public String message;

    public content content;

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

    public static class content {
        public ArrayList<province> provinces;

        public ArrayList<province> getProvinces() {
            return provinces;
        }

        public void setProvinces(ArrayList<province> provinces) {
            this.provinces = provinces;
        }
    }

    public static class province {
        public String id;
        public String name;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public ArrayList<city> getCitys() {
            return citys;
        }

        public void setCitys(ArrayList<city> citys) {
            this.citys = citys;
        }

        public String pid;

        public ArrayList<city> citys;
    }

    public static class city {
        public String id;
        public String name;
        public String pid;
        public ArrayList<district> districts;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ArrayList<district> getDistricts() {
            return districts;
        }

        public void setDistricts(ArrayList<district> districts) {
            this.districts = districts;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
    }

    public static class district {
        public String id;
        public String name;
        public String pid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
    }

}
