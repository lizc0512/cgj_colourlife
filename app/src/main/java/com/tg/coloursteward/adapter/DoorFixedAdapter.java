package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.tg.coloursteward.DoorActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BeeBaseAdapter;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.util.StringUtils;
/**
 * 常用门禁Adapter
 */
public class DoorFixedAdapter extends BeeBaseAdapter{
	
	  // 屏幕宽度
    private int screenWidth = 0;
    private  boolean isadd=false;

    public boolean isadd() {
        return isadd;
    }

    public void setIsadd(boolean isadd) {
        this.isadd = isadd;
    }

    public DoorFixedAdapter(Context c, List dataList) {
        super(c, dataList);
        isadd = false;
        Display display = ((DoorActivity)c).getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
    }

    @Override
    protected BeeCellHolder createCellHolder(View cellView) {
        Holder holder = new Holder();
        holder.img_doortype = (ImageView) cellView.findViewById(R.id.img_doortype);
        holder.txt_name = (TextView) cellView.findViewById(R.id.txt_name);
        holder.rl_layout = (RelativeLayout)cellView.findViewById(R.id.rl_layout);

        holder.rl_layout
                .setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        screenWidth * 140 / 480));

        return holder;
    }


    @Override
    public int getViewTypeCount() {
        return 10000;
    }

    public int getItemViewType(int position) {
        return position % 10000;
    }


    @Override
    protected View bindData(int position, View cellView, ViewGroup parent, BeeCellHolder h) {
        DoorFixedResp doorFixedResp = (DoorFixedResp) dataList.get(position);
        Holder holder = (Holder) h;




        if (doorFixedResp!=null) {
            if (StringUtils.isNotEmpty(doorFixedResp.getName())){
                holder.txt_name.setVisibility(View.VISIBLE);
                holder.txt_name.setText(doorFixedResp.getName());
            }
            if (StringUtils.isNotEmpty(doorFixedResp.getType())) {
                holder.img_doortype.setVisibility(View.VISIBLE);
                if (doorFixedResp.getType().equals("2")) {
                    holder.img_doortype.setImageResource(R.drawable.icon_bussine);
                } else {
                    holder.img_doortype.setImageResource(R.drawable.icon_home);
                }
            }
        }
        if (position>0){
            if (doorFixedResp== null||StringUtils.isEmpty(doorFixedResp.getName())||StringUtils.isEmpty(doorFixedResp.getType())){
               if (!isadd()) {

                   holder.img_doortype.setVisibility(View.VISIBLE);
                   holder.img_doortype.setImageResource(R.drawable.btn_door_add);
                   holder.txt_name.setVisibility(View.GONE);
                   setIsadd(true);
               }else {
                   holder.img_doortype.setVisibility(View.GONE);
                   holder.txt_name.setVisibility(View.GONE);
               }

            }
        }
        return cellView;
    }


    @Override
    public View createCellView() {
        return mInflater.inflate(R.layout.door_fixed_item, null);
    }

    public class Holder extends BeeCellHolder {
        public ImageView img_doortype;
        public TextView txt_name;
        public RelativeLayout rl_layout;
    }

}

