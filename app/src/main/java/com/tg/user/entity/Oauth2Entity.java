package com.tg.user.entity;

/**
 * Created by Administrator on 2018/7/9.
 *
 * @Description
 */

public class Oauth2Entity {

    /**
     * token_type : Bearer
     * expires_in : 1296000
     * access_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjY1MDE0ZWMzNTRhNmY4YWQ0NWU5MTI0MDQwNGE5ZjZhOTExOTMwZWU5NmQ4MGY3NWM3ZDRlYjI0YWVkMDBjMGJhYTY0YjllYTI1OThjYjUyIn0.eyJhdWQiOiIzIiwianRpIjoiNjUwMTRlYzM1NGE2ZjhhZDQ1ZTkxMjQwNDA0YTlmNmE5MTE5MzBlZTk2ZDgwZjc1YzdkNGViMjRhZWQwMGMwYmFhNjRiOWVhMjU5OGNiNTIiLCJpYXQiOjE1MjgzNDI5OTMsIm5iZiI6MTUyODM0Mjk5MywiZXhwIjoxNTI5NjM4OTkzLCJzdWIiOiIiLCJzY29wZXMiOltdfQ.uys2oBKGmx5_CDbEbuovf95ngD4ybRL1IW8DDJqb4mKOYCj9mAYW9UxFCZe-3NaEGEQZLxqPKirxIibhvzFN34094SP-8g-8mqOKL7panPKSnN8cAllhb_MElrPjHoCyAf1GvsQ_fJbRxe8sbqpoewlo3cBfHwUVVWcuNz-ZSUnLv1cS-yoSlxklVART2iAMpukHrpojnvw3MOh1XeLsJUWV-sf0Dt56ZURCTRslJwFc6rhPpjB-a2LMegvWqsx3TGmfbmGfaa4tYOHzGYa4LBB6gggNTEJoJLWvYaYuto2DhpC3h8T67qA5Lr6n5VY4g7L6CmlZuLYmdHJJiy5O09tPJ0tNymdZypGOBI05eCtL89RIN_amhQvzARbYw3jePSfvLoW0VIPScG9Ypxd71WIlVf_qWWhDX9bxkkzYxmsddriM1eVD-7flxiMWFUlxFwUAH1b_a38BVozr_xMvuClCsmZhBORhaPDqaNmE7sPxZhCV7mM-x3QntudfE0ygPoCWBbAr73MgIaLRWjuafMuS_hJiNU8EShwHJKi2ywWHsL34MHcrtRVMB4UbSnEZQtSdUFldXVejnHb_FOv4Ob5TmK9aEz6UpS-70sp0SpiPV9ZElOVZ1rE7yH4UdWnz63f_084YthKmHTjdkZPbnXlsy0nWmF9SPHZUiEnE7c4
     * refresh_token : def5020088ceadefa839baf91f7d69ed8e2778986a20185eba03bb19770021bf9a1a50883f2cdf98dc4a848b5649e5637d0dd0e3bc5d6943d38b6d6b5a31bb5e28984f433f1af13386ef73f7ffc97753b307aebaae74d618449c10404564f09e2558836900b489c903659b987719af75b27dfc0bba90f874e0dee37c5939bf786fd8edaa40be8ebaa6d94c59bfaa71b709ebc87d6487b9f4f5b2a2a18cf13f73d3c4fa9188178eeb8e1ad4639662bc901d9dd6aefb64b329a0948a5b8811d1926bd2d7d04ab41a275fb97eb15fc655043f9b72da84de4831fc7dbc8fb2ece57ab0648e951ebb6e4b800893009ce8a3db10175e00d5b6deca7802a54b307f0915989ec8ea80aab7298c18f0dd54547d9844777b233f3b071dd8665cf26b37937d6dfd3d90bcd7ccbb4086d9b1bd489f21b3fa1ed9f36897feba5d42b3b78577fd6dcd822c3e5228145794adce7f8b2b3726c4fcd6043a7ae1a835851fc9547e38e8240945
     */

    private String token_type;
    private String expires_in;
    private String access_token;
    private String refresh_token;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
