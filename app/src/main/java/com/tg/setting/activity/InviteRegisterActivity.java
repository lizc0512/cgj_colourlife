package com.tg.setting.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.entity.VersionEntity;
import com.tg.setting.model.SettingModel;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 邀请同事
 *
 * @author Administrator
 */
public class InviteRegisterActivity extends BaseActivity implements HttpResponse {
    private TextView tvVersion;
    private SettingModel settingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        tvVersion = findViewById(R.id.tv_version);
        String versionShort = BuildConfig.VERSION_NAME;
        tvVersion.setText("Android: V" + versionShort);

    }

    private void initData() {
        settingModel.getUpdate(0, "1", false, this);
    }

    private void initView() {
        settingModel = new SettingModel(this);
        findViewById(R.id.tv_version).setOnClickListener(v -> {
            showShare();
        });
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(Wechat.NAME);
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("标题");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("https://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("http://b-ssl.duitang.com/uploads/item/201711/10/20171110225150_ym2jw.jpeg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        InviteRegisterActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "分享成功");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "分享失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "关闭分享");
                    }
                });
            }
        });
        oks.show(this);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_invite_register, null);
    }

    @Override
    public String getHeadTitle() {
        return "邀请同事";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    VersionEntity versionEntity = new VersionEntity();
                    try {
                        versionEntity = GsonUtils.gsonToBean(result, VersionEntity.class);
                        int result_up = versionEntity.getContent().getResult();
                        String getVersion = versionEntity.getContent().getInfo().getVersion();
                        if (result_up == 2) {//1：最新版本，2：介于最新和最低版本之间，3：低于支持的最低版本
                            tvVersion.setText("有新版本 " + getVersion);
                        } else if (result_up == 1) {
                            tvVersion.setText("当前已经最新版本 " + BuildConfig.VERSION_NAME);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

}
