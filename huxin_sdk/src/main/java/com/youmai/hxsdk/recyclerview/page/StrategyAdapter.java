package com.youmai.hxsdk.recyclerview.page;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.WebViewActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.AdvertModel;
import com.youmai.hxsdk.entity.StrategyModel;
import com.youmai.hxsdk.http.DownLoadingListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2016.08.10 09:40
 * 描述：hook 类型策略
 */
public class StrategyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = StrategyAdapter.class.getName();

    private enum HANDLER {
        TOAST
    }

    public enum UI_TYPE {
        TYPE_DEFAULT, TYPE_SHOW, TYPE_ONE, TYPE_TWO, TYPE_THREE
    }

    private Context mContext;
    private Context mActivityContext;
    private List<StrategyModel.DBean.ModelsBean> mList;

    private boolean isOnce = false;

    public StrategyAdapter(Context context) {
        mActivityContext = context;
        mContext = context.getApplicationContext();
        mList = new ArrayList<>();
    }

    public void setItem(StrategyModel.DBean.ModelsBean item) {
        mList.add(0, item);
        notifyDataSetChanged();
    }

    public void removeItemByContent(String content) {
        isOnce = true;
        for (StrategyModel.DBean.ModelsBean item : mList) {
            if (item.getContent().contains(content)) {
                mList.remove(item);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setList(List<StrategyModel.DBean.ModelsBean> list) {
        isOnce = false;
        mList.addAll(list);
        notifyDataSetChanged();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == HANDLER.TOAST.ordinal()) {
                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_73), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别

        int type = UI_TYPE.TYPE_DEFAULT.ordinal();
        StrategyModel.DBean.ModelsBean modelBean = mList.get(position);
        if (modelBean.getType().equals("1")) {// type1
            type = UI_TYPE.TYPE_ONE.ordinal();
        } else if (modelBean.getType().equals("2")) {// type2
            type = UI_TYPE.TYPE_TWO.ordinal();
        } else if (modelBean.getType().equals("3")) {// type3
            type = UI_TYPE.TYPE_THREE.ordinal();
        } else if (modelBean.getType().equals("0")) {// show
            type = UI_TYPE.TYPE_SHOW.ordinal();
        }
        return type;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (UI_TYPE.values()[viewType]) {
            case TYPE_SHOW: {
                view = inflater.inflate(R.layout.hx_activity_hook_show, parent, false);
                holder = new TypeShowViewHolder(view);
            }
            break;
            case TYPE_ONE: {
                view = inflater.inflate(R.layout.hx_hook_strategy_type_one, parent, false);
                holder = new TypeOneViewHolder(view);
            }
            break;
            case TYPE_TWO: {
                view = inflater.inflate(R.layout.hx_hook_strategy_type_two, parent, false);
                holder = new TypeTwoViewHolder(view);
            }
            break;
            case TYPE_THREE: {
                view = inflater.inflate(R.layout.hx_hook_strategy_type_three, parent, false);
                holder = new TypeThreeViewHolder(view);
            }
            break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final StrategyModel.DBean.ModelsBean item = mList.get(position);
        if (viewHolder instanceof TypeOneViewHolder) {
            strategyTypeOne((TypeOneViewHolder) viewHolder, item);
        } else if (viewHolder instanceof TypeTwoViewHolder) {
            strategyTypeTwo((TypeTwoViewHolder) viewHolder, item);
        } else if (viewHolder instanceof TypeThreeViewHolder) {
            strategyTypeThree((TypeThreeViewHolder) viewHolder, item, position);
        } else if (viewHolder instanceof TypeShowViewHolder) {
            strategyTypeShow((TypeShowViewHolder) viewHolder, item);
        }
    }

    private void strategyTypeShow(TypeShowViewHolder viewHolder, final StrategyModel.DBean.ModelsBean item) {
        try {
            Glide.with(mContext)
                    .load(item.getRemark())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop().error(R.drawable.hx_show_default_full))
                    .into(viewHolder.shop_show_image);

            viewHolder.shop_show_title.setText(item.getTitle());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(WebViewActivity.INTENT_TITLE, item.getTitle());
                    intent.putExtra(WebViewActivity.INTENT_URL, item.getContent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, WebViewActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void strategyTypeOne(TypeOneViewHolder viewHolder, final StrategyModel.DBean.ModelsBean modelCustom) {
        if (!StringUtils.isEmpty(modelCustom.getFid())) {
            String logoUrl = AppConfig.getImageUrl(mContext, modelCustom.getFid());
            Glide.with(mContext)
                    .load(logoUrl)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop().error(R.drawable.hx_icon_full_header_normal))
                    .into(viewHolder.iv_custom_logo);
        } else if (modelCustom.getCustomType() == 1) {
            viewHolder.iv_custom_logo.setImageResource(modelCustom.getDefaultId());
        }

        viewHolder.tv_custom_title.setText(modelCustom.getTitle());
        viewHolder.tv_custom_remark.setText(modelCustom.getRemark());
        viewHolder.tv_custom_btnName.setText(modelCustom.getBtnName());

        final String funType = modelCustom.getFunType();
        viewHolder.tv_custom_btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HuxinSdkManager.instance().showSdkLogin()) {
                    return;
                }
                if (funType.equals("1")) {
                    Intent intent = new Intent();
                    intent.putExtra(WebViewActivity.INTENT_TITLE, modelCustom.getTitle());
                    intent.putExtra(WebViewActivity.INTENT_URL, modelCustom.getContent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, WebViewActivity.class);
                    mContext.startActivity(intent);
                } else if (funType.equals("2")) {
                    //com.youmai.hxsdk.SettingShowActivity
                    try {
                        if (modelCustom.getContent().equals("com.youmai.huxin.app.activity.purse.PurseRechargePayActivity")) {
                            HuxinSdkManager.instance().onUmengEvent(mContext, "A_click_chzyingdao");
                        }
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClassName(mContext, modelCustom.getContent());
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void strategyTypeTwo(TypeTwoViewHolder viewHolder, final StrategyModel.DBean.ModelsBean modelThird) {
        /*String logoUrl = AppConfig.getImageUrl(mContext, modelThird.getFid());
        Glide.with(mContext)
                .load(logoUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new GlideCircleTransform(mContext))
                .error(R.drawable.hx_icon_full_header_normal)
                .into(viewHolder.iv_third_logo);
        */
        viewHolder.tv_third_title.setText(modelThird.getTitle());
        viewHolder.tv_third_remark.setText(modelThird.getRemark());

        final String funType = modelThird.getFunType();
        viewHolder.tv_third_btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (funType.equals("1")) {
                    Intent intent = new Intent();
                    intent.putExtra(WebViewActivity.INTENT_TITLE, modelThird.getTitle());
                    intent.putExtra(WebViewActivity.INTENT_URL, modelThird.getContent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, WebViewActivity.class);
                    mContext.startActivity(intent);
                } else if (funType.equals("2")) {
                    //com.youmai.hxsdk.SettingShowActivity
                }
            }
        });
    }

    private void strategyTypeThree(final TypeThreeViewHolder viewHolder, final StrategyModel.DBean.ModelsBean modelAdvert, final int position) {
        if (isOnce) {
            return;
        }

        String content = modelAdvert.getContent();
        HttpConnector.httpGet(content, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e("YW", "advert resp: " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        AdvertModel model = GsonUtil.parse(response, AdvertModel.class);
                        if (model.getD() != null && model.getD().getAdvert() != null) {
                            if (mActivityContext != null && !((Activity) mActivityContext).isFinishing()) {
                                advertDataConfig(viewHolder, model.getD().getAdvert());
                            }
                        }
                    } else {
                        mList.remove(position);
                        StrategyAdapter.this.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void advertDataConfig(TypeThreeViewHolder viewHolder, final AdvertModel.DBean.AdvertBean advertBean) {

        String logoUrl = AppConfig.getImageUrl(mContext, advertBean.getLogofid());
        Glide.with(mContext)
                .load(logoUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new GlideRoundTransform(mContext, 18))
                        .centerCrop().error(R.drawable.hx_icon_full_header_normal))
                .into(viewHolder.iv_advert_logo);

        Glide.with(mContext)
                .load(AppConfig.getImageUrl(mContext, advertBean.getFid()))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .circleCrop()
                        .centerCrop().error(R.drawable.hx_half_pic_moren))
                .into(viewHolder.iv_advert_img);

        viewHolder.tv_advert_title.setText(advertBean.getTitle());
        viewHolder.tv_advert_remark.setText(advertBean.getRemark());
        viewHolder.advert_btnName.setText(advertBean.getBtnName());

        final String funType = advertBean.getFunType();
        viewHolder.advert_btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HuxinSdkManager.instance().showSdkLogin()) {
                    return;
                }
                if (funType.equals("1")) {
                    //链接
                    Intent intent = new Intent();
                    intent.putExtra(WebViewActivity.INTENT_TITLE, advertBean.getTitle());
                    intent.putExtra(WebViewActivity.INTENT_URL, advertBean.getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, WebViewActivity.class);
                    mContext.startActivity(intent);
                } else if (funType.equals("2")) {
                    //下载APK
                    showApkDialog(advertBean);
                }
            }
        });
    }

    /**
     * 下载APK
     *
     * @param
     */
    private void showApkDialog(final AdvertModel.DBean.AdvertBean advertBean) {

        final int id = 1;
        final NotificationManager mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("apk")
                .setContentText("loading...")
                .setSmallIcon(R.drawable.img_msg);

        try {
            new FileAsyncTaskDownload(new DownLoadingListener() {
                @Override
                public void onProgress(int cur, int total) {
                    int rate = cur / total;
                    Log.e(TAG, "rate: " + rate);
                    mBuilder.setProgress(100, rate, false);
                    // Displays the progress bar for the first time.
                    mNotifyManager.notify(id, mBuilder.build());
                }

                @Override
                public void downloadFail(String err) {
                    Message message = handler.obtainMessage();
                    message.arg1 = HANDLER.TOAST.ordinal();
                    handler.sendMessage(message);
                }

                @Override
                public void downloadSuccess(String path) {
                    // When the loop is finished, updates the notification
                    mBuilder.setContentText("download complete").setProgress(0, 0, false);
                    AppUtils.installApk(mContext, path);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);

                    mNotifyManager.notify(id, mBuilder.build());

                }
            }).execute(advertBean.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TypeOneViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_custom_logo;
        TextView tv_custom_title;
        TextView tv_custom_remark;
        TextView tv_custom_btnName;

        public TypeOneViewHolder(View itemView) {
            super(itemView);
            iv_custom_logo = (ImageView) itemView.findViewById(R.id.iv_custom_logo);
            tv_custom_title = (TextView) itemView.findViewById(R.id.tv_custom_title);
            tv_custom_remark = (TextView) itemView.findViewById(R.id.tv_custom_remark);
            tv_custom_btnName = (TextView) itemView.findViewById(R.id.tv_custom_btnName);
        }
    }

    private class TypeTwoViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_third_logo;
        TextView tv_third_title;
        TextView tv_third_remark;
        TextView tv_third_btnName;

        public TypeTwoViewHolder(View itemView) {
            super(itemView);
            iv_third_logo = (ImageView) itemView.findViewById(R.id.iv_third_logo);
            tv_third_title = (TextView) itemView.findViewById(R.id.tv_third_title);
            tv_third_remark = (TextView) itemView.findViewById(R.id.tv_third_remark);
            tv_third_btnName = (TextView) itemView.findViewById(R.id.tv_third_btnName);
        }
    }

    private class TypeThreeViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_advert_logo;
        ImageView iv_advert_img;
        TextView tv_advert_title;
        TextView tv_advert_remark;
        TextView advert_btnName;

        public TypeThreeViewHolder(View itemView) {
            super(itemView);
            iv_advert_logo = (ImageView) itemView.findViewById(R.id.iv_advert_logo);
            iv_advert_img = (ImageView) itemView.findViewById(R.id.iv_advert_img);
            tv_advert_title = (TextView) itemView.findViewById(R.id.tv_advert_title);
            tv_advert_remark = (TextView) itemView.findViewById(R.id.tv_advert_remark);
            advert_btnName = (TextView) itemView.findViewById(R.id.tv_advert_btnName);
        }
    }

    private class TypeShowViewHolder extends RecyclerView.ViewHolder {

        ImageView shop_show_image;
        TextView shop_show_title;

        public TypeShowViewHolder(View itemView) {
            super(itemView);
            shop_show_image = (ImageView) itemView.findViewById(R.id.hx_shop_show_image);
            shop_show_title = (TextView) itemView.findViewById(R.id.hx_shop_show_title);
        }
    }

}
