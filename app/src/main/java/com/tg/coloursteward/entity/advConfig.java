package com.tg.coloursteward.entity;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by colin on 2018/3/21.
 */

public class advConfig {

    /**
     * code : 0
     * message :
     * content : {"list":{"100301":[{"plate_code":"100301","name":"banner2-test","url":"http://case.backyard.colourlife.com:4600/detail?caseId=27d905d3-89e7-4ba0-ad8e-3c3f68e07a11","img_path":"http://120.25.148.153:30020/file/20179/file-1504703773641.png"}]}}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;

    public boolean isSuceess() {
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

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public static class ContentBean {
        /**
         * list : {"100301":[{"plate_code":"100301","name":"banner2-test","url":"http://case.backyard.colourlife.com:4600/detail?caseId=27d905d3-89e7-4ba0-ad8e-3c3f68e07a11","img_path":"http://120.25.148.153:30020/file/20179/file-1504703773641.png"}]}
         */

        private ListBean list;

        public ListBean getList() {
            return list;
        }

        public void setList(ListBean list) {
            this.list = list;
        }

        public static class ListBean {
            @SerializedName("100301")
            private List<_$100301Bean> _$100301;

            public List<_$100301Bean> get_$100301() {
                return _$100301;
            }

            public void set_$100301(List<_$100301Bean> _$100301) {
                this._$100301 = _$100301;
            }

            public static class _$100301Bean {
                /**
                 * plate_code : 100301
                 * name : banner2-test
                 * url : http://case.backyard.colourlife.com:4600/detail?caseId=27d905d3-89e7-4ba0-ad8e-3c3f68e07a11
                 * img_path : http://120.25.148.153:30020/file/20179/file-1504703773641.png
                 */

                private String plate_code;
                private String name;
                private String url;
                private String img_path;

                public String getPlate_code() {
                    return plate_code;
                }

                public void setPlate_code(String plate_code) {
                    this.plate_code = plate_code;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getImg_path() {
                    return img_path;
                }

                public void setImg_path(String img_path) {
                    this.img_path = img_path;
                }
            }
        }
    }
}
