package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.im.ImCardUtil;
import java.util.List;

/**
 * 主题历史记录
 * Created by fylder on 2017/3/2.
 */

public class NoteHistoryAdapter<T> extends RecyclerView.Adapter {

    Context context;
    private List<T> datas;
    private OnClickListener listener;

    public NoteHistoryAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 检索匹配内容
     */
    public void filter(List<T> themeList) {

        this.datas = themeList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.hx_note_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //final CacheMsgBean data = datas.get(position);
        T data = datas.get(position);
        String name="";
        if(data instanceof ImCardUtil.ImCardInfo){
            name = ((ImCardUtil.ImCardInfo) data).getCardTheme();
        }
        /*
        try {
            Method cardMothod = data.getClass().getDeclaredMethod("getCardTheme");
            name = (String) cardMothod.invoke(data);

            Method timeMothod = data.getClass().getDeclaredMethod("getCardTime");
            msgtime = (long) timeMothod.invoke(data);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
        NoteViewHolder viewHolder = (NoteViewHolder) holder;
        viewHolder.text.setText(name);
        final String finalName = name;
        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickItem(finalName);//data.getCardName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView lay;
        TextView text;

        public NoteViewHolder(View itemView) {
            super(itemView);
            lay = (CardView) itemView.findViewById(R.id.note_history_lay);
            text = (TextView) itemView.findViewById(R.id.note_history_text);
        }

    }

    public interface OnClickListener {
        void clickItem(String cardName);
    }
}
