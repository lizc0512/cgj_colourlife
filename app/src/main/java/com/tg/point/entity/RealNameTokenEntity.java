package com.tg.point.entity;


import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * Created by hxg on 2019/5/9 11:32
 */
public class RealNameTokenEntity extends BaseContentEntity {
    /**
     * content : {"BizToken":"643EB10A-D18C-496B-8BF9-D07131539ED9","Url":"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2cca36a86d5035ae&redirect_uri=http%3A%2F%2Fopen.faceid.qq.com%2Fv1%2Fapi%2FgetCode%3FbizRedirect%3Dhttps%253A%252F%252Ffaceid.qq.com%252Fapi%252Fauth%252FgetOpenidAndSaveToken%253Ftoken%253D643EB10A-D18C-496B-8BF9-D07131539ED9&response_type=code&scope=snsapi_base&state=&component_appid=wx9802ee81e68d6dee#wechat_redirect","RequestId":"ba7c9562-2e05-43c0-a575-5e19c11a3b51"}
     * contentEncrypt :
     */

    private ContentBean content;
    private String contentEncrypt;

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
         * BizToken : 643EB10A-D18C-496B-8BF9-D07131539ED9
         * Url : https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2cca36a86d5035ae&redirect_uri=http%3A%2F%2Fopen.faceid.qq.com%2Fv1%2Fapi%2FgetCode%3FbizRedirect%3Dhttps%253A%252F%252Ffaceid.qq.com%252Fapi%252Fauth%252FgetOpenidAndSaveToken%253Ftoken%253D643EB10A-D18C-496B-8BF9-D07131539ED9&response_type=code&scope=snsapi_base&state=&component_appid=wx9802ee81e68d6dee#wechat_redirect
         * RequestId : ba7c9562-2e05-43c0-a575-5e19c11a3b51
         */

        private String BizToken;

        public String getBizToken() {
            return BizToken;
        }

        public void setBizToken(String BizToken) {
            this.BizToken = BizToken;
        }

    }
}
