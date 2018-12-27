package com.tg.coloursteward.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.LinkParseUtil;
import com.youmai.hxsdk.router.APath;


/**
 * @time 2018/12/26 09:00 by lizc
 * @class describe   SDK跳转到彩管家页面的过度页面
 */
@Route(path = APath.COLOURLIFE_JUMP_EXCESSIVE)
public class BrowerJumpExcessiveActivity extends BaseActivity {
    public static final String THRIDFROMSOURCE = "thridfromsource"; //网页的来源
    public static final String THRIDLINKURL = "thridlinkurl"; //网页的链接
    public static final String THRIDTITLE = "thridtitle";  //网页的标题
    public static final String THRIDDOMAIN = "thriddomain";  //彩白条支付
    public static final String THRIDSOURCE = "thridsource";  //彩白条支付

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri schemeUri = intent.getData();
        int source = intent.getIntExtra(THRIDFROMSOURCE, 0);
        {
            if (source == 0) {
                if (null != schemeUri && "colourlifeauth".equals(schemeUri.getScheme()) && "web".equals(schemeUri.getHost())) {
                    final String linkUrl = schemeUri.getQueryParameter("linkURL");
                    Intent jumpIntent = new Intent(BrowerJumpExcessiveActivity.this, MainActivity1.class);
                    intent.putExtra(MainActivity1.JUMPOTHERURL, linkUrl);
                    startActivity(jumpIntent);
                }
            } else {
                String linkUrl = intent.getStringExtra(THRIDLINKURL);
                String title = intent.getStringExtra(THRIDTITLE);
                String domain = intent.getStringExtra(THRIDDOMAIN);
                boolean thridSource = intent.getBooleanExtra(THRIDSOURCE, false);
                if (thridSource) {
                    LinkParseUtil.jumpFromThrid(BrowerJumpExcessiveActivity.this, linkUrl, title);
                } else {
                    if (!TextUtils.isEmpty(domain)) {
                        LinkParseUtil.jumpHtmlPay(BrowerJumpExcessiveActivity.this, linkUrl, domain);
                    } else {
                        LinkParseUtil.parse(BrowerJumpExcessiveActivity.this, linkUrl, title);
                    }
                }
            }
        }
        finish();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }
}
