package com.youmai.hxsdk.module.videocall;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoSelectConstactAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<SearchContactBean> mList;
    private VideoSelectConstactAdapter.OnItemClickListener mOnItemClickListener;
    private boolean isSelectAll = false;
    private Map<String, SearchContactBean> cacheMap = new HashMap<>();
    private int groupSize;
    private int mType;

    private ArrayList<String> selectedList;

    public void clickSelectAll() {
        if (!isSelectAll) {
            isSelectAll = true;
            addAllCacheMap();
            notifyDataSetChanged();
        } else {
            removeAllCacheMap();
            isSelectAll = false;
            notifyDataSetChanged();
        }
        if (null != mOnItemClickListener) {
            mOnItemClickListener.updateUI(cacheMap);
        }
    }

    public void setGroupSize(int size) {
        this.groupSize = size;
    }

    public VideoSelectConstactAdapter(Context context, ArrayList<SearchContactBean> list,
                                      VideoSelectConstactAdapter.OnItemClickListener listener,
                                      int type) {
        this.mContext = context;
        this.mList = list;
        this.mType = type;
        this.mOnItemClickListener = listener;
    }


    public VideoSelectConstactAdapter(Context context, ArrayList<SearchContactBean> list,
                                      ArrayList<String> selectedList,
                                      VideoSelectConstactAdapter.OnItemClickListener listener,
                                      int type) {
        this.mContext = context;
        this.mList = list;
        this.selectedList = selectedList;
        this.mType = type;
        this.mOnItemClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.group_delete_item_layout, parent, false);
        return new VideoSelectConstactAdapter.GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final VideoSelectConstactAdapter.GroupViewHolder itemHolder = (VideoSelectConstactAdapter.GroupViewHolder) holder;
        final SearchContactBean model = mList.get(position);
        itemHolder.tv_name.setText(model.getDisplayName());

        if (cacheMap.containsKey(mList.get(position).getUuid())) {
            itemHolder.cb_collect.setChecked(true);
        } else {
            itemHolder.cb_collect.setChecked(false);
        }
        if (selectedList != null && selectedList.contains(model.getUuid())) {
            itemHolder.cb_collect.setButtonDrawable(R.drawable.contact_select_def);
        } else {
            itemHolder.cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (mType) {
                        case VideoSelectConstactActivity.VIDEO_TRAIN:  //培训
                            updateCacheMap(model);
                            //根据群成员总数刷新是不是全选
                            if (cacheMap.size() == groupSize - selectedList.size()) {
                                isSelectAll = true;
                                if (null != mOnItemClickListener) {
                                    mOnItemClickListener.updateCheckBoxIsTrue();
                                }
                            }
                            //改变cb背景
                            if (itemHolder.cb_collect.isChecked()) {
                                itemHolder.cb_collect.setChecked(false);
                                //只要是有未选中的item就直接去刷新全选按钮的状态
                                isSelectAll = false;
                                if (null != mOnItemClickListener) {
                                    mOnItemClickListener.updateCheckBoxIsFlase();
                                }

                            } else {
                                itemHolder.cb_collect.setChecked(true);

                            }
                            //每次点击去更新activity的UI
                            if (null != mOnItemClickListener) {
                                mOnItemClickListener.updateUI(cacheMap);
                            }
                            break;
                        case VideoSelectConstactActivity.VIDEO_MEETING:  //视频会议添加
                            if (cacheMap.size() + selectedList.size() < VideoSelectConstactActivity.VIDEO_MEETING_MAX + 1) {
                                updateCacheMap(model);
                                if (cacheMap.size() == groupSize - selectedList.size()) {
                                    isSelectAll = true;
                                    if (null != mOnItemClickListener) {
                                        mOnItemClickListener.updateCheckBoxIsTrue();
                                    }
                                }
                                //改变cb背景
                                if (itemHolder.cb_collect.isChecked()) {
                                    itemHolder.cb_collect.setChecked(false);
                                    //只要是有未选中的item就直接去刷新全选按钮的状态
                                    isSelectAll = false;
                                    if (null != mOnItemClickListener) {
                                        mOnItemClickListener.updateCheckBoxIsFlase();
                                    }
                                } else {
                                    itemHolder.cb_collect.setChecked(true);

                                }

                            } else {
                                if (itemHolder.cb_collect.isChecked()) {
                                    itemHolder.cb_collect.setChecked(false);
                                    updateCacheMap(model);
                                } else {
                                    Toast.makeText(mContext, "只能选9个人", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //每次点击去更新activity的UI
                            if (null != mOnItemClickListener) {
                                mOnItemClickListener.updateUI(cacheMap);
                            }
                            break;
                    }

                }
            });

        }
        String url = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + model.getUsername();
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(itemHolder.iv_header);

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
                setTVColor(model.getDisplayName(), queryString, itemHolder.tv_name);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL:
                if (queryString.matches("[a-zA-Z]+")) {
                    if (model.getSimplepinyin().contains(queryString)) {
                        int a = model.getSimplepinyin().indexOf(queryString);
                        int b = a + queryString.length();
                        SpannableStringBuilder builder = new SpannableStringBuilder(model.getDisplayName());
                        builder.setSpan(new ForegroundColorSpan(highlightColor),
                                a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        itemHolder.tv_name.setText(builder);
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
                itemHolder.tv_name.setText(builder);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_T9:
                if (model.getSimpleT9().contains(queryString)) {
                    int a = model.getSimpleT9().indexOf(queryString);
                    int b = a + queryString.length();
                    SpannableStringBuilder build = new SpannableStringBuilder(model.getDisplayName());
                    build.setSpan(new ForegroundColorSpan(highlightColor),
                            a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    itemHolder.tv_name.setText(build);
                }
                break;
            default:
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_collect;
        ImageView iv_header;
        TextView tv_name;

        GroupViewHolder(View itemView) {
            super(itemView);
            cb_collect = itemView.findViewById(R.id.cb_collect);
            iv_header = itemView.findViewById(R.id.iv_contact_header);
            tv_name = itemView.findViewById(R.id.tv_contact_name);
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

    public interface OnItemClickListener {
        void updateUI(Map<String, SearchContactBean> chaMap);

        void updateCheckBoxIsTrue();


        void updateCheckBoxIsFlase();
    }

    private void updateCacheMap(SearchContactBean contact) {
        if (cacheMap.containsKey(contact.getUuid())) {
            cacheMap.remove(contact.getUuid());
        } else {
            cacheMap.put(contact.getUuid(), contact);
        }
    }

    private void addAllCacheMap() {
        cacheMap.clear();
        for (SearchContactBean bean : mList) {
            if (!selectedList.contains(bean.getUuid()))
                cacheMap.put(bean.getUuid(), bean);
        }
    }

    private void removeAllCacheMap() {
        cacheMap.clear();
    }
}
