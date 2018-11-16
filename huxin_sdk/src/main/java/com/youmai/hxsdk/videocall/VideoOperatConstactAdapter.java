package com.youmai.hxsdk.videocall;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoOperatConstactAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<SearchContactBean> mList;
    private onClickCallBack listener;
    private ArrayList<String> uuids = new ArrayList<>();
    private int type;
    private boolean isOpenMicrophone = false;
    private boolean isOpenVideo = false;

    interface onClickCallBack {
        void onItemsClick(ArrayList<String> uuid);

        void onAssignAdmin(int position, SearchContactBean bean);
    }

    private int selectPos = -1;

    public void setSelected(int position) {
        this.selectPos = position;
        notifyDataSetChanged();
    }

    public VideoOperatConstactAdapter(Context mContext, ArrayList<SearchContactBean> mList, onClickCallBack callBack, int i) {
        this.mContext = mContext;
        this.mList = mList;
        this.type = i;
        this.listener = callBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (type == VideoOperatConstactActivity.UPDATE_ADMIN) {
            View view = inflater.inflate(R.layout.group_delete_item_layout, parent, false);
            return new PemissionViewHolder(view);
        } else if (type == VideoOperatConstactActivity.DEL_MEMBER) {
            View view = inflater.inflate(R.layout.rlv_video_delete, parent, false);
            return new GroupViewHolder(view);
        } else if (type == VideoOperatConstactActivity.QUERY_MEMBRE) {
            View view = inflater.inflate(R.layout.rlv_video_delete, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.rlv_video_setting, parent, false);
            return new SettingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final SearchContactBean model = mList.get(position);
        if (type == VideoOperatConstactActivity.UPDATE_ADMIN) {
            //设置管理员
            final VideoOperatConstactAdapter.PemissionViewHolder itemHolder = (VideoOperatConstactAdapter.PemissionViewHolder) holder;
            bindData(itemHolder.tv_contact_name, itemHolder.iv_contact_header, model);
            if (selectPos == position) {
                itemHolder.cb_collect.setChecked(true);
            } else {
                itemHolder.cb_collect.setChecked(false);
            }
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onAssignAdmin(position, model);
                    }
                }
            });
            setHighLight(model, itemHolder.tv_contact_name);

        } else if (type == VideoOperatConstactActivity.DEL_MEMBER) {
            //移除人
            final VideoOperatConstactAdapter.GroupViewHolder itemHolder = (VideoOperatConstactAdapter.GroupViewHolder) holder;
            bindData(itemHolder.tv_contact_name, itemHolder.iv_contact_header, model);
            itemHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        uuids.add(mList.get(position).getUuid());
                        listener.onItemsClick(uuids);
                    }
                }
            });

            setHighLight(model, itemHolder.tv_contact_name);
        } else if (type == VideoOperatConstactActivity.QUERY_MEMBRE) {
            //查看通话成员
            final VideoOperatConstactAdapter.GroupViewHolder itemHolder = (VideoOperatConstactAdapter.GroupViewHolder) holder;
            bindData(itemHolder.tv_contact_name, itemHolder.iv_contact_header, model);
            itemHolder.btn_remove.setVisibility(View.INVISIBLE);
            setHighLight(model, itemHolder.tv_contact_name);
        } else {
            //设置音视频
            final VideoOperatConstactAdapter.SettingViewHolder itemHolder = (VideoOperatConstactAdapter.SettingViewHolder) holder;
            bindData(itemHolder.tv_contact_name, itemHolder.iv_contact_header, model);
            itemHolder.btn_sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemHolder.btn_sound.isSelected()) {
                        itemHolder.btn_sound.setSelected(false);
                    } else {
                        itemHolder.btn_sound.setSelected(true);
                    }
                }
            });
            itemHolder.btn_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemHolder.btn_video.isSelected()) {
                        itemHolder.btn_video.setSelected(false);
                    } else {
                        itemHolder.btn_video.setSelected(true);
                    }
                }
            });
        }
    }

    /**
     * 绑定名字头像数据
     *
     * @param tvView
     * @param ivView
     * @param model
     */
    private void bindData(TextView tvView, ImageView ivView, SearchContactBean model) {
        //String url = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + model.getUsername();
        String url=model.getIconUrl();
        tvView.setText(model.getDisplayName());
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(ivView);
    }

    private void setHighLight(SearchContactBean model, TextView textView) {
        String queryString = model.getSearchKey();
        int highlightColor = ContextCompat.getColor(mContext, R.color.hxs_color_green3);
        switch (model.getSearchType()) {
            case SearchContactBean.SEARCH_TYPE_NUMBER:
                //setTVColor(model.getPhoneNum(), queryString, contactHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_INFO:
                //setTVColor(model.getInfo(), queryString, contactHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_NAME:
                setTVColor(model.getDisplayName(), queryString, textView);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL:
                if (queryString.matches("[a-zA-Z]+")) {
                    if (model.getSimplepinyin().contains(queryString)) {
                        int a = model.getSimplepinyin().indexOf(queryString);
                        int b = a + queryString.length();
                        SpannableStringBuilder builder = new SpannableStringBuilder(model.getDisplayName());
                        builder.setSpan(new ForegroundColorSpan(highlightColor),
                                a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.setText(builder);
                    }
                }
                break;
            //全拼的高亮暂不支持
            case SearchContactBean.SEARCH_TYPE_WHOLE_SPECL:
                int[] findIndex = model.getWholePinYinFindIndex();

                String displayName = model.getDisplayName();
                SpannableStringBuilder builder = new SpannableStringBuilder(displayName);

                List<Integer> integers = PinYinUtils.match2(displayName, queryString);
                int start = 0;
                int end = 0;

                if (!ListUtils.isEmpty(integers)) {
                    start = integers.get(0);
                    end = integers.get(integers.size() - 1);
                }

                builder.setSpan(new ForegroundColorSpan(highlightColor),
                        start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_T9:
                if (model.getSimpleT9().contains(queryString)) {
                    int a = model.getSimpleT9().indexOf(queryString);
                    int b = a + queryString.length();
                    SpannableStringBuilder build = new SpannableStringBuilder(model.getDisplayName());
                    build.setSpan(new ForegroundColorSpan(highlightColor),
                            a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(build);
                }
                break;
            default:
                break;
        }
    }

    private void setTVColor(String str, String subString, TextView tv) {
        int highlightColor = ContextCompat.getColor(mContext, R.color.hxs_color_green3);
        int a = str.indexOf(subString);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        if (a != -1) {
            int b = a + subString.length();
            builder.setSpan(new ForegroundColorSpan(highlightColor),
                    a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(builder);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PemissionViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_collect;
        private ImageView iv_contact_header;
        private TextView tv_contact_name;

        public PemissionViewHolder(View itemView) {
            super(itemView);
            cb_collect = itemView.findViewById(R.id.cb_collect);
            iv_contact_header = itemView.findViewById(R.id.iv_contact_header);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_contact_header;
        private TextView tv_contact_name;
        private Button btn_remove;

        public GroupViewHolder(View itemView) {
            super(itemView);
            iv_contact_header = itemView.findViewById(R.id.iv_contact_header);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            btn_remove = itemView.findViewById(R.id.btn_remove);
        }
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        private Button btn_sound;
        private Button btn_video;
        private ImageView iv_contact_header;
        private TextView tv_contact_name;

        public SettingViewHolder(View itemView) {
            super(itemView);
            iv_contact_header = itemView.findViewById(R.id.iv_contact_header);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            btn_video = itemView.findViewById(R.id.btn_video);
            btn_sound = itemView.findViewById(R.id.btn_sound);
        }
    }
}
