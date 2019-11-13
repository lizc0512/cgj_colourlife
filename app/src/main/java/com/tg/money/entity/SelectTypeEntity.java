package com.tg.money.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/13 16:52
 * @change
 * @chang time
 * @class describe
 */
public class SelectTypeEntity {

    /**
     * code : 0
     * message : success
     * content : {"result":[{"general_uuid":"ceshi1","general_name":"测试关联类目1","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ceshi2","general_name":"测试关联类目2","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ceshi5","general_name":"测试关联类目5","pano":"1055586af520ea6042298cecc0689362","atid":55},{"general_uuid":"ceshi6","general_name":"测试关联类目6","pano":"10276f41d352dfcf4f698e0c76b8fd89","atid":27},{"general_uuid":"ceshi7","general_name":"测试关联类目7","pano":"10276f41d352dfcf4f698e0c76b8fd89","atid":27},{"general_uuid":"ICECFRS0-12B5-43ED-9526-D239EB049114","general_name":"彩富人生","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ICECZY00-F26F-42B8-988C-27F4AEE3292A","general_name":"E费通","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ICECZYFPSC-FSLI-J8YH-8WZY-VSQ9N5BDAWC2","general_name":"彩之云饭票商城3.0总店","pano":"9f22bdb6934141ecb7e5a4506958a51b","atid":2},{"general_uuid":"ICEEANQ0-UIYV-DC9E-V2T5-G72WX8DR356W","general_name":"E安全总店（测试）","pano":"1043c485071ba98e437a8d09388be061","atid":43},{"general_uuid":"ICEENY00-98A3-495F-A41C-2272FE6A9934","general_name":"E能源","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13},{"general_uuid":"ICEEQJ00-811D-4C6B-9EDE-8F378D624EEE","general_name":"E清洁总店","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13},{"general_uuid":"ICEESF00-5C89-4B7D-A25C-B9C1CE62D061","general_name":"天虹","pano":"10276f41d352dfcf4f698e0c76b8fd89","atid":27},{"general_uuid":"ICEETC00-1FE6-45BE-8D01-E8DAB61EB570","general_name":"易停车","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13},{"general_uuid":"ICEOA000-ED69-4335-A475-A8BEF914D44A","general_name":"鸿源天城总店","pano":"10276f41d352dfcf4f698e0c76b8fd89","atid":27},{"general_uuid":"ICEPOS00-0AC3-4C5B-86E2-C3A0E686991E","general_name":"新收费系统-test总店","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ICEPOSWY-0AC3-4C5B-86E2-C3A0E686991E","general_name":"ipos-物业","pano":"10534647ec25d92e4cf5ab3bb09765d3","atid":53},{"general_uuid":"ICEQHHQ0-RM3F-55AA-HBYL-W6CJJKOOGUSX","general_name":"Happybird-test总店","pano":"1055586af520ea6042298cecc0689362","atid":55},{"general_uuid":"ICESHJP0-BHDF-KTJR-YGE6-448LK4N97FOP","general_name":"ipos","pano":"067ac772345c412ab51f82a94f8833af","atid":8},{"general_uuid":"ICEWSQ00-D727-44DB-AA0C-FDA1124B411B","general_name":"微商圈","pano":"67789ad2f69f42b283c53e02a0ae00b0","atid":13},{"general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","general_name":"新收费系统总店","pano":"10464d48c2a8d7f4425c9cc2102b1826","atid":46},{"general_uuid":"test_uuid","general_name":"test1","pano":"9f22bdb6934141ecb7e5a4506958a51b","atid":2},{"general_uuid":"test_uuid_1","general_name":"test2","pano":"9f22bdb6934141ecb7e5a4506958a51b","atid":2}]}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;

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
        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * general_uuid : ceshi1
             * general_name : 测试关联类目1
             * pano : 067ac772345c412ab51f82a94f8833af
             * atid : 8
             */

            private String general_uuid;
            private String general_name;
            private String pano;
            private String atid;
            private int isCheck;

            public String getGeneral_uuid() {
                return general_uuid;
            }

            public void setGeneral_uuid(String general_uuid) {
                this.general_uuid = general_uuid;
            }

            public String getGeneral_name() {
                return general_name;
            }

            public void setGeneral_name(String general_name) {
                this.general_name = general_name;
            }

            public String getPano() {
                return pano;
            }

            public void setPano(String pano) {
                this.pano = pano;
            }

            public String getAtid() {
                return atid;
            }

            public void setAtid(String atid) {
                this.atid = atid;
            }

            public int getIsCheck() {
                return isCheck;
            }

            public void setIsCheck(int isCheck) {
                this.isCheck = isCheck;
            }
        }
    }
}
