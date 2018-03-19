package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-10 10:08
 * Description:
 */
public class IMEmotionAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private EmoInfo emoInfo;
    private OnItemClickListener onItemClickListener = null;

    public IMEmotionAdapter(Context context, EmoInfo emoInfo) {
        mContext = context;
        this.emoInfo = emoInfo;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public EmoInfo getEmoInfo() {
        return emoInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_fragment_im_emotion_item, parent, false);
        return new EmotionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        EmoInfo.EmoItem emoItem = emoInfo.getEmoList().get(position);
        final EmotionItemViewHolder emotionItemViewHolder = (EmotionItemViewHolder) holder;

        int m100 = DisplayUtil.dip2px(mContext, 100);

        Glide.with(mContext)
                .asGif()
                .load(emoItem.getEmoTRes())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(m100, m100))
                .into(emotionItemViewHolder.emotionIV);

        emotionItemViewHolder.emotionTV.setText(emoItem.getEmoName());

        emotionItemViewHolder.itemLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(emotionItemViewHolder.itemLy, position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return emoInfo.getEmoList().size();
    }


    class EmotionItemViewHolder extends RecyclerView.ViewHolder {

        protected ImageView emotionIV;
        protected TextView emotionTV;
        protected View itemLy;

        public EmotionItemViewHolder(View itemView) {
            super(itemView);
            itemLy = itemView.findViewById(R.id.item_ly);
            emotionIV = (ImageView) itemView.findViewById(R.id.emotion_iv);
            emotionTV = (TextView) itemView.findViewById(R.id.emotion_tv);
        }
    }
}
