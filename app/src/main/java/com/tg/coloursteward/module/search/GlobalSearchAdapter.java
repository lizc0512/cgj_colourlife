package com.tg.coloursteward.module.search;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.module.contact.utils.PinYinUtils;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 17/3/2.
 */
public class GlobalSearchAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> mDataList = new ArrayList<>();
    private GlobalSearchAdapterListener mOnGlobalSearchAdapterListener;
    private String mHeadTitle;
    private String mTailTitle;

    public GlobalSearchAdapter(Context context) {
        mContext = context;
    }

    public String getHeadTitle() {
        return mHeadTitle;
    }

    public void setHeadTitle(String headTitle) {
        this.mHeadTitle = headTitle;
    }


    public void setTailTitle(String tailTitle) {
        this.mTailTitle = tailTitle;
    }


    public void setList(List<T> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.global_search_item_layout, parent, false);
        return new SearchItem(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchItem searchItemHolder = (SearchItem) holder;
        SearchContactBean model = (SearchContactBean) mDataList.get(position);
        searchItemHolder.search_name.setText(model.getDisplayName());

        searchItemHolder.search_info.setText(model.getPhoneNum());
        String queryString = model.getSearchKey();
        int highlightColor = ContextCompat.getColor(mContext, R.color.hxs_color_green3);
        switch (model.getSearchType()) {
            case SearchContactBean.SEARCH_TYPE_NUMBER:
                setTVColor(model.getPhoneNum(), queryString, searchItemHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_INFO:
                setTVColor(model.getInfo(), queryString, searchItemHolder.search_info);
                break;
            case SearchContactBean.SEARCH_TYPE_NAME:
                setTVColor(model.getDisplayName(), queryString, searchItemHolder.search_name);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL:
                if (queryString.matches("[a-zA-Z]+")) {
                    if (model.getSimplepinyin().contains(queryString)) {
                        int a = model.getSimplepinyin().indexOf(queryString);
                        int b = a + queryString.length();
                        SpannableStringBuilder builder = new SpannableStringBuilder(model.getDisplayName());
                        builder.setSpan(new ForegroundColorSpan(highlightColor),
                                a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        searchItemHolder.search_name.setText(builder);
                    }
                }
                break;
            //全拼的高亮暂不支持
            case SearchContactBean.SEARCH_TYPE_WHOLE_SPECL:
                int[] findIndex = model.getWholePinYinFindIndex();
                String displayName = model.getDisplayName();
                SpannableStringBuilder builder = new SpannableStringBuilder(displayName);

                List<Integer> integers = PinYinUtils.match2(displayName, queryString);
                Log.e("YW", "i begin");
                int start = 0;
                int end = 0;

                if (!ListUtils.isEmpty(integers)) {
                    start = integers.get(0);
                    end = integers.get(integers.size() - 1);
                }

                Log.e("YW", "start: " + start + "\tend: " + end);
                for (Integer i : integers) {
                    Log.e("YW", "i: " + i);
                }

                builder.setSpan(new ForegroundColorSpan(highlightColor),
                        start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                searchItemHolder.search_name.setText(builder);
                break;
            case SearchContactBean.SEARCH_TYPE_SIMPLE_T9:
                if (model.getSimpleT9().contains(queryString)) {
                    int a = model.getSimpleT9().indexOf(queryString);
                    int b = a + queryString.length();
                    SpannableStringBuilder build = new SpannableStringBuilder(model.getDisplayName());
                    build.setSpan(new ForegroundColorSpan(highlightColor),
                            a, b, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    searchItemHolder.search_name.setText(build);
                }
                break;
            default:
                break;
        }

        Glide.with(mContext)
                .load(model.getIconUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .placeholder(R.drawable.contacts_common_default_user_bg)
                        .error(R.drawable.contacts_common_default_user_bg))
                .into(searchItemHolder.search_icon);

        searchItemHolder.search_item.setTag(position);
        searchItemHolder.search_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int finalPosition = (Integer) v.getTag();
                T contacts = mDataList.get(finalPosition);
                if (mOnGlobalSearchAdapterListener != null) {
                    mOnGlobalSearchAdapterListener.onItemClick(contacts);
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

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    public void setGlobalSearchAdapterListener(GlobalSearchAdapterListener listener) {
        mOnGlobalSearchAdapterListener = listener;
    }

    public interface GlobalSearchAdapterListener {
        void onItemClick(Object item);
    }

    public class SearchItem extends RecyclerView.ViewHolder {
        ImageView search_icon;
        TextView search_name;
        TextView search_info;
        RelativeLayout search_item;

        public SearchItem(View itemView) {
            super(itemView);
            search_icon = (ImageView) itemView.findViewById(R.id.global_search_icon);
            search_name = (TextView) itemView.findViewById(R.id.global_search_name);
            search_info = (TextView) itemView.findViewById(R.id.global_search_info);
            search_item = (RelativeLayout) itemView.findViewById(R.id.global_search_item);
        }
    }

    public class SearchHeadItem extends RecyclerView.ViewHolder {
        TextView mTitle;
        RelativeLayout headItem;

        public SearchHeadItem(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.global_search_item_head_title);
            headItem = (RelativeLayout) itemView.findViewById(R.id.global_search_item_head);
        }
    }

    public class SearchTailItem extends RecyclerView.ViewHolder {
        TextView mTitle;
        RelativeLayout tailItem;

        public SearchTailItem(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.global_search_item_tail_title);
            tailItem = (RelativeLayout) itemView.findViewById(R.id.global_search_item_tail);
        }
    }
}
