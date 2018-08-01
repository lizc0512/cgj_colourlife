package com.youmai.hxsdk.group.adapter;

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

/**
 * 作者：create by YW
 * 日期：2018.04.14 14:53
 * 描述：
 */
public class DeleteContactAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<SearchContactBean> mList;

    private OnItemClickListener mOnItemClickListener;

    public DeleteContactAdapter(Context context, ArrayList<SearchContactBean> list,
                                OnItemClickListener listener) {
        mContext = context;
        mList = list;
        mOnItemClickListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.group_delete_item_layout, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final GroupViewHolder itemHolder = (GroupViewHolder) holder;
        final SearchContactBean model = mList.get(position);

        itemHolder.tv_name.setText(model.getDisplayName());
        itemHolder.cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);

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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemHolder.cb_collect.isChecked()) {
                    itemHolder.cb_collect.setChecked(false);
                } else {
                    itemHolder.cb_collect.setChecked(true);
                }

                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(position, model);
                }
            }
        });

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


    public interface OnItemClickListener {
        void onItemClick(int position, SearchContactBean bean);
    }

}
