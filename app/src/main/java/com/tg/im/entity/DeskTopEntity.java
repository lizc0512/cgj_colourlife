package com.tg.im.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.im.entity
 * @class describe
 * @anthor lzic QQ:510906433
 * @time 2019/11/4 11:01
 * @change
 * @chang time
 * @class describe
 */
public class DeskTopEntity {

    /**
     * code : 0
     * message : success
     * content : {"total":14,"data":[{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤分析","msg_sub_title":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25","send_time":1572034564,"expire_time":1603138564,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25","auth_type":"2","msg_id":"fb5a9f47bf324cb18f5134e61ae0a1a4","items":[],"homePushTime":"2019-10-26 04:16:04"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤分析","msg_sub_title":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-08-26&endTime=2019-09-25","send_time":1569442989,"expire_time":1600546989,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-08-26&endTime=2019-09-25","auth_type":"2","msg_id":"d1cda4e21537430b9d1df1376338b089","items":[],"homePushTime":"2019-09-26 04:23:09"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-10","send_time":1568060441,"expire_time":1599164441,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-10","auth_type":"2","msg_id":"79a1c9adf85245f6873af3e4f9fc1c7e","items":[],"homePushTime":"2019-09-10 04:20:41"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-09","send_time":1567974088,"expire_time":1599078088,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-09","auth_type":"2","msg_id":"21bebf59e44141c993119e1dd54ec539","items":[],"homePushTime":"2019-09-09 04:21:28"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-08","send_time":1567887724,"expire_time":1598991724,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-08","auth_type":"2","msg_id":"10c6f4f60d324acf8c7b28d7c47c6c1b","items":[],"homePushTime":"2019-09-08 04:22:04"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有2条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-04","send_time":1567542265,"expire_time":1598646265,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有2条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-04","auth_type":"2","msg_id":"08a9b2489cf3497ea0b414300db2ed7a","items":[],"homePushTime":"2019-09-04 04:24:25"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有2条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-03","send_time":1567522029,"expire_time":1598626029,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有2条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-03","auth_type":"2","msg_id":"95c21c275a834c52a67741465066240a","items":[],"homePushTime":"2019-09-03 22:47:09"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-07-26&endTime=2019-08-25","send_time":1566764167,"expire_time":1597868167,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-07-26&endTime=2019-08-25","auth_type":"2","msg_id":"c93f04ee1ed545f6a69a46894257eba3","items":[],"homePushTime":"2019-08-26 04:16:07"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"","msg_intro":"","msg_url":"http://www.baidu.com","send_time":1566581705,"expire_time":1597685705,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"您8月没有缺勤记录，请继续保持","url":"http://www.baidu.com","auth_type":"2","msg_id":"4cfc05e1d1dd4e79b186adce628b9496","items":[],"homePushTime":"2019-08-24 01:35:05"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"","msg_intro":"","msg_url":"www.baidu.com","send_time":1566581481,"expire_time":1597685481,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"您8月没有缺勤记录，请继续保持","url":"www.baidu.com","auth_type":"2","msg_id":"c10ef477488947d79ad38b9d9b82e4e3","items":[],"homePushTime":"2019-08-24 01:31:21"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566475524,"expire_time":1597579524,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"ba84fbd69c644f8ca27fda539dbc1090","items":[],"homePushTime":"2019-08-22 20:05:24"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566475114,"expire_time":1597579114,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"2f46b472fbf0419db0a6a2aa455e5bca","items":[],"homePushTime":"2019-08-22 19:58:34"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566473273,"expire_time":1597577273,"show_type":1,"isread":0,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"1df869a72c9b4e7fb2e3a39fc553bb6b","items":[],"homePushTime":"2019-08-22 19:27:53"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566471058,"expire_time":1597575058,"show_type":1,"isread":0,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"c4addc5e39c7486ca460d85fb15ac0ab","items":[],"homePushTime":"2019-08-22 18:50:58"}]}
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
        /**
         * total : 14
         * data : [{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤分析","msg_sub_title":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25","send_time":1572034564,"expire_time":1603138564,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25","auth_type":"2","msg_id":"fb5a9f47bf324cb18f5134e61ae0a1a4","items":[],"homePushTime":"2019-10-26 04:16:04"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤分析","msg_sub_title":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-08-26&endTime=2019-09-25","send_time":1569442989,"expire_time":1600546989,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您9月实际出勤21.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-08-26&endTime=2019-09-25","auth_type":"2","msg_id":"d1cda4e21537430b9d1df1376338b089","items":[],"homePushTime":"2019-09-26 04:23:09"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-10","send_time":1568060441,"expire_time":1599164441,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-10","auth_type":"2","msg_id":"79a1c9adf85245f6873af3e4f9fc1c7e","items":[],"homePushTime":"2019-09-10 04:20:41"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-09","send_time":1567974088,"expire_time":1599078088,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-09","auth_type":"2","msg_id":"21bebf59e44141c993119e1dd54ec539","items":[],"homePushTime":"2019-09-09 04:21:28"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有1条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-08","send_time":1567887724,"expire_time":1598991724,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有1条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-08","auth_type":"2","msg_id":"10c6f4f60d324acf8c7b28d7c47c6c1b","items":[],"homePushTime":"2019-09-08 04:22:04"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有2条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-04","send_time":1567542265,"expire_time":1598646265,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有2条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-04","auth_type":"2","msg_id":"08a9b2489cf3497ea0b414300db2ed7a","items":[],"homePushTime":"2019-09-04 04:24:25"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"考勤统计","msg_intro":"您9月有2条缺勤记录，请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-03","send_time":1567522029,"expire_time":1598626029,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤统计","title":"您9月有2条缺勤记录，请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/statistics?employeeAccount=lizhicheng01&createdDateStr=2019-09-03","auth_type":"2","msg_id":"95c21c275a834c52a67741465066240a","items":[],"homePushTime":"2019-09-03 22:47:09"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"考勤统计","msg_sub_title":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","msg_intro":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","msg_url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-07-26&endTime=2019-08-25","send_time":1566764167,"expire_time":1597868167,"show_type":1,"isread":1,"client_code":"kq","owner_account":"admin","owner_name":"考勤分析","title":"您8月实际出勤22.0天，合计缺勤0.0天,请及时核对处理。","url":"http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-07-26&endTime=2019-08-25","auth_type":"2","msg_id":"c93f04ee1ed545f6a69a46894257eba3","items":[],"homePushTime":"2019-08-26 04:16:07"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"","msg_intro":"","msg_url":"http://www.baidu.com","send_time":1566581705,"expire_time":1597685705,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"您8月没有缺勤记录，请继续保持","url":"http://www.baidu.com","auth_type":"2","msg_id":"4cfc05e1d1dd4e79b186adce628b9496","items":[],"homePushTime":"2019-08-24 01:35:05"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"","msg_intro":"","msg_url":"www.baidu.com","send_time":1566581481,"expire_time":1597685481,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"您8月没有缺勤记录，请继续保持","url":"www.baidu.com","auth_type":"2","msg_id":"c10ef477488947d79ad38b9d9b82e4e3","items":[],"homePushTime":"2019-08-24 01:31:21"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566475524,"expire_time":1597579524,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"ba84fbd69c644f8ca27fda539dbc1090","items":[],"homePushTime":"2019-08-22 20:05:24"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566475114,"expire_time":1597579114,"show_type":1,"isread":1,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"2f46b472fbf0419db0a6a2aa455e5bca","items":[],"homePushTime":"2019-08-22 19:58:34"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566473273,"expire_time":1597577273,"show_type":1,"isread":0,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"1df869a72c9b4e7fb2e3a39fc553bb6b","items":[],"homePushTime":"2019-08-22 19:27:53"},{"template_type":"2101","app_id":"kq","app_name":"考勤","app_logo":"https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png","msg_title":"msgTitile","msg_sub_title":"msg_sub_title","msg_intro":"msg_intro","msg_url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","send_time":1566471058,"expire_time":1597575058,"show_type":1,"isread":0,"client_code":"zzkj","owner_account":"admin","owner_name":"考勤统计","title":"this is title","url":"http://sign.hr.test.colourlife.com/detail/index.html#/statistics?createdDate=2019-08-17","auth_type":"1","msg_id":"c4addc5e39c7486ca460d85fb15ac0ab","items":[],"homePushTime":"2019-08-22 18:50:58"}]
         */

        private int total;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * template_type : 2101
             * app_id : kq
             * app_name : 考勤
             * app_logo : https://xxtz.oss-cn-shenzhen.aliyuncs.com/appinfo/logo/3ee7a2dba9df43b097f03ad5bdba7fea.png
             * msg_title : 考勤分析
             * msg_sub_title : 您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。
             * msg_intro : 您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。
             * msg_url : http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25
             * send_time : 1572034564
             * expire_time : 1603138564
             * show_type : 1
             * isread : 1
             * client_code : kq
             * owner_account : admin
             * owner_name : 考勤分析
             * title : 您10月实际出勤18.0天，合计缺勤0.0天,请及时核对处理。
             * url : http://sign.hr.colourlife.com/detail/index.html#/?startTime=2019-09-26&endTime=2019-10-25
             * auth_type : 2
             * msg_id : fb5a9f47bf324cb18f5134e61ae0a1a4
             * items : []
             * homePushTime : 2019-10-26 04:16:04
             */

            private String template_type;
            private String app_id;
            private String app_name;
            private String app_logo;
            private String msg_title;
            private String msg_sub_title;
            private String msg_intro;
            private String msg_url;
            private String send_time;
            private String expire_time;
            private String show_type;
            private String isread;
            private String client_code;
            private String owner_account;
            private String owner_name;
            private String title;
            private String url;
            private String auth_type;
            private String msg_id;
            private String homePushTime;
            private int isCheckBox;
            private int isShowCheck;

            public String getTemplate_type() {
                return template_type;
            }

            public void setTemplate_type(String template_type) {
                this.template_type = template_type;
            }

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getApp_name() {
                return app_name;
            }

            public void setApp_name(String app_name) {
                this.app_name = app_name;
            }

            public String getApp_logo() {
                return app_logo;
            }

            public void setApp_logo(String app_logo) {
                this.app_logo = app_logo;
            }

            public String getMsg_title() {
                return msg_title;
            }

            public void setMsg_title(String msg_title) {
                this.msg_title = msg_title;
            }

            public String getMsg_sub_title() {
                return msg_sub_title;
            }

            public void setMsg_sub_title(String msg_sub_title) {
                this.msg_sub_title = msg_sub_title;
            }

            public String getMsg_intro() {
                return msg_intro;
            }

            public void setMsg_intro(String msg_intro) {
                this.msg_intro = msg_intro;
            }

            public String getMsg_url() {
                return msg_url;
            }

            public void setMsg_url(String msg_url) {
                this.msg_url = msg_url;
            }

            public String getSend_time() {
                return send_time;
            }

            public void setSend_time(String send_time) {
                this.send_time = send_time;
            }

            public String getExpire_time() {
                return expire_time;
            }

            public void setExpire_time(String expire_time) {
                this.expire_time = expire_time;
            }

            public String getShow_type() {
                return show_type;
            }

            public void setShow_type(String show_type) {
                this.show_type = show_type;
            }

            public String getIsread() {
                return isread;
            }

            public void setIsread(String isread) {
                this.isread = isread;
            }

            public String getClient_code() {
                return client_code;
            }

            public void setClient_code(String client_code) {
                this.client_code = client_code;
            }

            public String getOwner_account() {
                return owner_account;
            }

            public void setOwner_account(String owner_account) {
                this.owner_account = owner_account;
            }

            public String getOwner_name() {
                return owner_name;
            }

            public void setOwner_name(String owner_name) {
                this.owner_name = owner_name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAuth_type() {
                return auth_type;
            }

            public void setAuth_type(String auth_type) {
                this.auth_type = auth_type;
            }

            public String getMsg_id() {
                return msg_id;
            }

            public void setMsg_id(String msg_id) {
                this.msg_id = msg_id;
            }

            public String getHomePushTime() {
                return homePushTime;
            }

            public void setHomePushTime(String homePushTime) {
                this.homePushTime = homePushTime;
            }

            public int getIsCheckBox() {
                return isCheckBox;
            }

            public void setIsCheckBox(int isCheckBox) {
                this.isCheckBox = isCheckBox;
            }

            public int getIsShowCheck() {
                return isShowCheck;
            }

            public void setIsShowCheck(int isShowCheck) {
                this.isShowCheck = isShowCheck;
            }
        }
    }
}
