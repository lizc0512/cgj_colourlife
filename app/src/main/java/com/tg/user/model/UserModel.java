package com.tg.user.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.colourlife.user.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/17 10:42
 * @change
 * @chang time
 * @class describe
 */
public class UserModel extends BaseModel {
    private String gtUrl = "/app/home/login/verify";
    private String keySecretUrl = "/1.0/auth";
    private String oauthUrl = "/oauth/token";
    private String oauthUserUrl = "/oauth/user";
    private String singleDeviceUrl = "/cgjapp/single/device/login";
    private String skinUrl = "/newoa/config/skin";
    private String tsUrl = "/timestamp";
    private String mmsUrl = "/employee/sms/sendSms";
    private String findPwdUrl = "/orgms/voice/updatePassWordSMS";
    private String czyOauthUrl = "/app/authUser";
    private String singleExitUrl = "/cgjapp/single/device/logout";
    private String changePwdUrl = "/account/password";
    private String sendMMSUrl = "/app/bind/mobile/sendCode";
    private String bindMobileUrl = "/app/bind/mobile";
    private String updateInfoUrl = "/app/modifyInfo";
    private String uploadImgUrl = "/avatar";
    private String getCommunityList = "/cgjControl/userRole/getCommunityListByAccountUuid";
    private String getDoorList = "/yuncontrol/ycAccessControl/getAccessByCommunityId";
    private String getKeyList = "/yuncontrol/ycKeyPackage/getKeyPacksByCommunityId";
    private String addDoor = "/yuncontrol/ycAccessControl/add";
    private String getBuild = "/cgjControl/colourLife/getBuildByCommunityId";
    private String getUnit = "/cgjControl/colourLife/getUnitByBuild";
    private String getFloor = "/cgjControl/colourLife/getHouseByFloor";
    private String bindDoor = "/yuncontrol/ycAccessControl/accessControl/install";
    private String getKeyIdentityUrl = "/yuncontrol/ycKeyIdentity/getAllByCommunityId";
    //    private String sendKeyByPhone = "/yuncontrol/ycKey/add";
    private String sendKeyByPhone = "/yuncontrol/ycKey/addByMobileHomeLoc";
    private String sendKeyByPackge = "/yuncontrol/ycKeyPackage/putKeyPackageByMobileHomeLoc";
    private String getKeyByQrCode = "/yuncontrol/ycKeyQrcode/getQrcode";

    public UserModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param geetest_challenge
     * @param geetest_validate
     * @param geetest_seccode
     * @param httpResponse      极验二次校验
     */
    public void postGt(int what, String geetest_challenge, String geetest_validate, String geetest_seccode, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("geetest_challenge", geetest_challenge);
        params.put("geetest_validate", geetest_validate);
        params.put("geetest_seccode", geetest_seccode);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, gtUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param httpResponse auth获取key和secret
     */
    public void postKeyAndSecret(int what, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 1, keySecretUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * @param what
     * @param username
     * @param passwordMD5
     * @param type         1：账号密码登录，2：短信验证码登录，3：手机号码密码登录，4：彩之云授权登录
     * @param httpResponse 获取oauthToken
     */
    public void postOauthToken(int what, String username, String passwordMD5, String type, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("password", passwordMD5);
        params.put("client_id", "3");
        params.put("client_secret", Contants.URl.CLIENT_SECRET);
        params.put("grant_type", "password");
        params.put("scope", "*");
        params.put("type", type);
        params.put("token", DES.TOKEN);
        params.put("app_id", DES.APP_ID);
        params.put("app_client_secret", DES.TOKEN);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param refresh_token
     * @param httpResponse  刷新oauthToken
     */
    public void postReOauthToken(int what, String refresh_token, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("client_id", "3");
        params.put("client_secret", Contants.URl.CLIENT_SECRET);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refresh_token);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * @param what
     * @param access_token
     * @param httpResponse 获取用户信息
     */
    public void getOauthUser(int what, String access_token, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUserUrl), RequestMethod.GET);
        request.addHeader("Authorization", "Bearer " + access_token);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param type
     * @param httpResponse 单设备登录
     */
    public void postSingleDevice(int what, String type, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("login_type", type);//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", BuildConfig.VERSION_NAME);//APP版本号
        params.put("device_code", TokenUtils.getUUID(mContext));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(mContext));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 3, singleDeviceUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * @param what
     * @param corpId
     * @param httpResponse 获取皮肤包
     */
    public void postSkin(int what, String corpId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_id", corpId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, skinUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param httpResponse 获取时间值
     */
    public void getTs(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, tsUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * @param what
     * @param oa
     * @param name
     * @param mobile
     * @param type
     * @param httpResponse 发送短信接口
     */
    public void postSendMms(int what, String oa, String name, String mobile, String type, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", oa);
        params.put("realname", name);
        params.put("mobile", mobile);
        params.put("work_type", type);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, mmsUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param username
     * @param verify_code
     * @param newPassword
     * @param httpResponse 找回登录密码
     */
    public void putFindPwd(int what, String username, String verify_code, String newPassword, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", username);
        params.put("code", verify_code);
        params.put("password", newPassword);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, findPwdUrl), RequestMethod.PUT);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                            String message = HttpTools.getMessageString(result);
                            callback(message);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param code
     * @param httpResponse 彩之云授权登录接口
     */
    public void getCzyLogin(int what, String code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code);
        params.put("token", DES.TOKEN);
        params.put("app_id", DES.APP_ID);
        params.put("client_secret", DES.TOKEN);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, czyOauthUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param device_code
     * @param httpResponse 单设备退出
     */
    public void postSingleExit(int what, String device_code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("device_code", device_code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 3, singleExitUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * @param what
     * @param username
     * @param pwdold
     * @param pwdnew
     * @param httpResponse 修改登录密码
     */
    public void putChangePwd(int what, String username, String pwdold, String pwdnew, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("oldpassword", pwdold);
        params.put("newpassword", pwdnew);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, changePwdUrl), RequestMethod.PUT);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param mobile
     * @param httpResponse 获取绑定手机号的验证码
     */
    public void postSendMms(int what, String mobile, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, sendMMSUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param mobile
     * @param code
     * @param httpResponse 绑定手机号
     */
    public void postBindMobile(int what, String mobile, String code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("code", code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, bindMobileUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param gender
     * @param email
     * @param httpResponse 更新用户信息
     */
    public void postUpdateInfo(int what, String gender, String email, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("gender", gender);
        params.put("email", email);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, updateInfoUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void postUploadImg(int what, String imgUrl, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        BasicBinary binary = new FileBinary(new File(imgUrl));
        params.put("uid", UserInfo.employeeAccount);
        File file = new File(imgUrl);
        params.put("fileName", new FileBody(file));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 8, uploadImgUrl), RequestMethod.POST);
        request.add("fileName", binary);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 查询管理小区
     */
    public void getCommunityList(int what, String accountUuid, int pageNum, int pageSize, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("accountUuid", accountUuid);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getCommunityList), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else if (code == 1) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        httpResponse.OnHttpResponse(what, "");
                    }
                } else {
                    httpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * 门禁
     */
    public void getDoorList(int what, String communityId, int pageNum, int pageSize, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getDoorList), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }


    public void getKeyList(int what, String communityId, int pageNum, int pageSize, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getKeyList), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 新增门禁
     */
    public void addDoor(int what, String communityUuid, String accessName, String unitUuid, String isUnit, String unitName, String buildUuid,
                        String buildName, String location, String content, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);//小区id
        params.put("accessName", accessName);//门禁名字
        params.put("unitUuid", unitUuid); //单元或者楼栋或者外围门id
        params.put("isUnit", isUnit);//门类型，0为大门，1为单元门
        params.put("unitName", unitName);//单元名，选单元门时需要
        params.put("buildUuid", buildUuid);//楼栋id，选单元门时需要
        params.put("buildName", buildName);//楼栋名字，选单元门时需要
        params.put("location", location);//位置，选单元门时需要 //位置信息用于app显示门禁时判断位置
        params.put("content", content);//门禁描述
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, addDoor), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 根据小区获取楼栋
     */
    public void getBuild(int what, String communityId, int pageNum, int pageSize, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getBuild), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 根据楼栋获取单元
     */
    public void getUnit(int what, String buildUuid, int pageNum, int pageSize, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("buildUuid", buildUuid);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getUnit), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 根据楼层获取房屋信息
     */
    public void getFloor(int what, String unitUuid, int pageNum, int pageSize, Integer floorNum, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("unitUuid", unitUuid);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("floorNum", floorNum);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getFloor), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 绑定门禁
     */
    public void bindDoor(int what, String accessId, String mac, String cipherId, String model, String protocolVersion, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("accessId", accessId);
        params.put("mac", mac);
        params.put("cipherId", cipherId);
        params.put("model", model);
        params.put("protocolVersion", protocolVersion);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, bindDoor), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 钥匙身份
     */
    public void getIdentity(int what, String communityId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityId", communityId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getKeyIdentityUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 发送钥匙
     */
    public void sendKeyByPhone(int what, String accessId, List<Map<String, String>> mobileAndHomeLocs, String name, String identityId,
                               String startDate, String endDate,int keyType, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("accessId", accessId);
        params.put("mobileAndHomeLocs", mobileAndHomeLocs);
        params.put("name", name);
        params.put("identityId", identityId);
        if (keyType!=4){
            params.put("startDate", startDate);
            params.put("endDate", endDate);
        }

        String jsonParams = GsonUtils.gsonString(params);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, sendKeyByPhone), RequestMethod.POST);
        request.setDefineRequestBodyForJson(jsonParams);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }


    public void sendKeyByPackageName(int what, String accessId, List<Map<String, String>> mobileAndHomeLocs, String identityId,
                                     String startDate, String endDate,int keyType, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("packageId", accessId);
        params.put("mobileAndHomeLocs", mobileAndHomeLocs);
        params.put("identityId", identityId);
        if (keyType!=4){
            params.put("startDate", startDate);
            params.put("endDate", endDate);
        }
        String jsonParams = GsonUtils.gsonString(params);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, sendKeyByPackge), RequestMethod.POST);
        request.setDefineRequestBodyForJson(jsonParams);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void sendKeyByQrCode(int what, String accessId, String communityUuid, String content, String shareExplain, String identityId,
                                String overTime, String startTime, String endTime, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("accessId", accessId);
        params.put("content", content);
        params.put("shareExplain", shareExplain);
        params.put("identityId", identityId);
        params.put("overTime", overTime);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("communityUuid", communityUuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getKeyByQrCode), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

}
