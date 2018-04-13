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
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 17/3/2.
 */
public class GlobalSearchAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    public static final int ADAPTER_TYPE_NORMAL = 1;
    public static final int ADAPTER_TYPE_MORE = 2;
    public static final int ADAPTER_TYPE_TITLE = 3;

    public static final int ITEM_INNER_NORMAL = 10;
    public static final int ITEM_INNER_MORE = 11;
    
    private final int ITEM_TYPE_HEAD = 1;
    private final int ITEM_TYPE_TAIL = 2;
    private final int ITEM_TYPE_NORMAL = 3;

    protected int MAX_SHOW_MORE = 3;
    private Context mContext;
    private String mQueryString;
    private ArrayList<T> mDataList = new ArrayList<>();
    private GlobalSearchAdapterListener mOnGlobalSearchAdapterListener;
    private int mAdapterType = ADAPTER_TYPE_NORMAL;
    private int mItemInnerType = ITEM_INNER_NORMAL;
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

    public String getTailTitle() {
        return mTailTitle;
    }

    public void setTailTitle(String tailTitle) {
        this.mTailTitle = tailTitle;
    }

    public int getAdapterType() {
        return mAdapterType;
    }

    public void setAdapterType(int adapterType) {
        this.mAdapterType = adapterType;
    }

    public int getItemInnerType() {
        return mItemInnerType;
    }

    public void setItemInnerType(int innerType) {
        this.mItemInnerType = innerType;
    }
    
    public void setArrayList(ArrayList<T> arrayList, String query) {
        this.mDataList = arrayList;
        this.mQueryString = query;
        notifyDataSetChanged();
    }

    public ArrayList<T> getArrayList() {
        return mDataList;
    }

    public void setArrayList(ArrayList<T> arrayList) {
        this.mDataList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == ITEM_TYPE_NORMAL) {
            View view = inflater.inflate(R.layout.global_search_item_layout, parent, false);
            return new SearchItem(view);
        } else if (viewType == ITEM_TYPE_HEAD) {
            View view = inflater.inflate(R.layout.global_search_item_head, parent, false);
            return new SearchHeadItem(view);
        } else {
            View view = inflater.inflate(R.layout.global_search_item_tail, parent, false);
            return new SearchTailItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int finalPosition = position;
        if (getAdapterType() != ADAPTER_TYPE_NORMAL) {
            finalPosition = position - 1;
        }
        if (holder instanceof GlobalSearchAdapter.SearchItem) {

            SearchItem searchItemHolder = (SearchItem) holder;
            SearchContactBean model = (SearchContactBean) mDataList.get(finalPosition);
            searchItemHolder.search_name.setText(model.getDisplayName());

            if ((mItemInnerType == GlobalSearchAdapter.ITEM_INNER_MORE) && (model.getNextBean() != null)) {
                int count = 1;
                SearchContactBean nextBean = model;
                while (nextBean.getNextBean() != null) {
                    nextBean = nextBean.getNextBean();
                    count++;
                }
                searchItemHolder.search_info.setText(String.format
                        (mContext.getString(R.string.hx_common_more_message_list_info), count));
            } else {

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
                                /*findIndex[0], findIndex[1]+1,*/start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            }

            RequestOptions mOptions = new RequestOptions()
                    .centerInside()
                    .transform(new GlideCircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.hx_index_head01)
                    .override(60, 60);
            Glide.with(mContext)
                    .load(model.getIconUrl())
                    .apply(mOptions)
                    .into(searchItemHolder.search_icon);

            searchItemHolder.search_item.setTag(finalPosition);
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
        } else if (holder instanceof GlobalSearchAdapter.SearchHeadItem) {
            SearchHeadItem searchItemHolder = (SearchHeadItem) holder;
            if (getHeadTitle() != null) {
                searchItemHolder.mTitle.setText(getHeadTitle());
            }
        } else if (holder instanceof GlobalSearchAdapter.SearchTailItem) {
            SearchTailItem searchItemHolder = (SearchTailItem) holder;
            if (getTailTitle() != null) {
                searchItemHolder.mTitle.setText(getTailTitle());
            }
            searchItemHolder.tailItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnGlobalSearchAdapterListener != null) {
                        mOnGlobalSearchAdapterListener.onMoreItemClick();
                    }
                }
            });
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
        if (mDataList != null && mDataList.size() > 0) {
            // 缩略类型，超过3条，显示头+3条+尾，小于等于3条，显示头+3条
            if (getAdapterType() == ADAPTER_TYPE_MORE) {
                if (mDataList.size() > MAX_SHOW_MORE) {
                    return MAX_SHOW_MORE + 2;
                } else {
                    return mDataList.size() + 1;
                }
            } else if (getAdapterType() == ADAPTER_TYPE_TITLE) {
                return mDataList.size() + 1;
            }
            return mDataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getAdapterType() == ADAPTER_TYPE_MORE) {
            if (position == 0) {
                return ITEM_TYPE_HEAD;
            } else if (position == MAX_SHOW_MORE + 1) {
                return ITEM_TYPE_TAIL;
            }
        } else if (getAdapterType() == ADAPTER_TYPE_TITLE) {
            if (position == 0) {
                return ITEM_TYPE_HEAD;
            }
        }

        return ITEM_TYPE_NORMAL;
    }

    public void setGlobalSearchAdapterListener(GlobalSearchAdapterListener listener) {
        mOnGlobalSearchAdapterListener = listener;
    }

    public interface GlobalSearchAdapterListener {
        void onItemClick(Object item);

        void onMoreItemClick();
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
