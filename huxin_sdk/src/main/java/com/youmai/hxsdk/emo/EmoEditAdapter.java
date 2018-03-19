package com.youmai.hxsdk.emo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.EmoItem;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by colin on 2017/10/17.
 */

public class EmoEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int GIRD_COUNT = 4;
    public static final int DRI_WIDTH = 10;

    private Context mContext;

    private List<EmoItem> dataList = new ArrayList<>();

    private boolean isEdit = false;

    /*private CallBack callBack;

    public interface CallBack {
        void sucess();
    }*/


    public EmoEditAdapter(Context mContext, List<EmoItem> list) {
        this.mContext = mContext;

        EmoItem first = new EmoItem();
        dataList.add(first);
        if (!ListUtils.isEmpty(list)) {
            dataList.addAll(list);
        }

    }


    public void setList(List<EmoItem> list) {
        if (!ListUtils.isEmpty(list)) {
            dataList.clear();
            EmoItem first = new EmoItem();
            dataList.add(first);
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private void setList2(List<EmoItem> list) {
        isEdit = true;
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
        if (ListUtils.isEmpty(list)) {
            if (mListener != null) {
                mListener.onItemNull();
            }
        }
    }

    public List<EmoItem> getDataList() {
        return dataList;
    }

    /*public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }*/


    public boolean isEdit() {
        return isEdit;
    }


    public void editModel(boolean b) {
        isEdit = b;

        if (b) {
            dataList.remove(0);
        } else {
            EmoItem first = new EmoItem();
            dataList.add(0, first);
        }

        if (ListUtils.isEmpty(dataList)) {
            if (mListener != null) {
                mListener.onItemNull();
            }
        }

        notifyDataSetChanged();
    }


    public void moveToFirst() {
        final List<EmoItem> data = new ArrayList<>();
        List<EmoItem> choice = new ArrayList<>();
        List<EmoItem> noChoice = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            EmoItem item = dataList.get(i);

            if (item.isCheck()) {
                choice.add(item);
            } else {
                noChoice.add(item);
            }

        }

        dataList.clear();

        dataList.addAll(choice);
        dataList.addAll(noChoice);
        data.addAll(choice);
        data.addAll(noChoice);

        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setRank(i);
            dataList.get(i).setCheck(false);
        }


        emoKeep(new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                Toast.makeText(mContext, "整理成功", Toast.LENGTH_SHORT).show();
                HuxinSdkManager.instance().setEmoList(data);
                //EmoItem first = new EmoItem();
                //dataList.add(0, first);
                isEdit = true;
                if (mListener != null) {
                    mListener.onRefresh();
                }
                setList2(data);

                /*if (callBack != null) {
                    callBack.sucess();
                }*/
            }
        });

    }


    public void emoDel() {
        final List<EmoItem> data = new ArrayList<>();
        List<EmoItem> choice = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            EmoItem item = dataList.get(i);

            if (!item.isCheck()) {
                choice.add(item);
            }
        }

        dataList.clear();

        dataList.addAll(choice);
        data.addAll(choice);

        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setRank(i);
            dataList.get(i).setCheck(false);
        }

        emoKeep(new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                Toast.makeText(mContext, "整理成功", Toast.LENGTH_SHORT).show();

                HuxinSdkManager.instance().setEmoList(data);
                //EmoItem first = new EmoItem();
                //dataList.add(0, first);
                isEdit = true;
                if (mListener != null) {
                    mListener.onRefresh();
                }
                setList2(data);

                /*if (callBack != null) {
                    callBack.sucess();
                }*/
            }
        });

    }


    public void emoKeep(IPostListener listener) {
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();

        String content = GsonUtil.format(dataList);

        HuxinSdkManager.instance().userInfoSave(phoneNum, 1, content, listener);
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.emo_edit_item, parent, false);

        int rest = mContext.getResources().getDisplayMetrics().widthPixels - DRI_WIDTH * 4;

        int SIDE = rest / GIRD_COUNT;
        contentView.setLayoutParams(new RelativeLayout.LayoutParams(SIDE, SIDE));

        return new ThumbViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final EmoItem item = dataList.get(position);

        final ThumbViewHolder vh = (ThumbViewHolder) holder;

        final String path = AppConfig.getDownloadHost() + item.getFid();//七牛路径

        if (!isEdit && position == 0) {
            vh.img_item.setImageResource(R.drawable.hx_emo_add2);
        } else {
            Glide.with(mContext)
                    .load(path)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.hx_icon_rd).fitCenter())
                    .into(vh.img_item);

        }

        if (isEdit) {
            vh.cb_choice.setVisibility(View.VISIBLE);
        } else {
            vh.cb_choice.setVisibility(View.GONE);
        }

        if (item.isCheck()) {
            vh.cb_choice.setChecked(true);
        } else {
            vh.cb_choice.setChecked(false);
        }

        vh.cb_choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setCheck(isChecked);
                if (mContext instanceof EmoEditActivity) {
                    EmoEditActivity act = (EmoEditActivity) mContext;
                    act.checkChange();
                }
            }
        });


        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit && position == 0) {
                    Intent intent = new Intent(mContext, EmoChoiceActivity.class);
                    if (mContext instanceof EmoEditActivity) {
                        EmoEditActivity act = (EmoEditActivity) mContext;
                        act.startActivityForResult(intent, EmoEditActivity.REQUEST_CODE_EMO);
                    }
                }
            }
        });


    }


    private class ThumbViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_item;
        private CheckBox cb_choice;

        public ThumbViewHolder(View itemView) {
            super(itemView);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
            cb_choice = (CheckBox) itemView.findViewById(R.id.cb_choice);
        }
    }

    private IRefreshEmoListener mListener;

    protected interface IRefreshEmoListener {
        void onRefresh();

        void onItemNull();
    }

    public void setRefreshListener(IRefreshEmoListener listener) {
        mListener = listener;
    }

}
