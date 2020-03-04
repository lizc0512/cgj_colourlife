package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.view.RoundImageView;

import java.util.List;

public class FamilyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<FamilyInfo> list;
    private FamilyInfo item;
    private Context context;
    private OnClickCallBack onClickCallBack;

    public FamilyAdapter(Context con, List<FamilyInfo> list) {
        this.list = list;
        this.context = con;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = list.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).sortLetters.charAt(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.family_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        item = list.get(position);
        ((ViewHolder) holder).tvFamily.setText(item.name);
        if (list.get(position).type.equals("org")) {
            ((ViewHolder) holder).rivHead.setVisibility(View.GONE);
        } else if (list.get(position).type.equals("user")) {
            ((ViewHolder) holder).rivHead.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).rivHead.setCircleShape();
            GlideUtils.loadImageDefaultDisplay(context, item.avatar, ((ViewHolder) holder).rivHead,
                    R.drawable.moren_geren, R.drawable.moren_geren);
        }
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            ((ViewHolder) holder).tvCatalog.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).tvCatalog.setVisibility(View.GONE);
        }
        ((ViewHolder) holder).ll_home_contact.setOnClickListener(v -> {
            if (null != onClickCallBack) {
                onClickCallBack.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private RoundImageView rivHead;
        private TextView tvFamily;
        private View tvCatalog;
        private LinearLayout ll_home_contact;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            rivHead = itemView.findViewById(R.id.iv_image);
            tvFamily = itemView.findViewById(R.id.tv_family);
            tvCatalog = itemView.findViewById(R.id.catalog);
            ll_home_contact = itemView.findViewById(R.id.ll_home_contact);
        }
    }

    public void setOnClickCallBack(OnClickCallBack callBack) {
        this.onClickCallBack = callBack;
    }

    public interface OnClickCallBack {
        void onClick(int position);
    }
}
