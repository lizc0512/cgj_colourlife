package com.tg.coloursteward.view;

import java.util.ArrayList;

import com.tg.coloursteward.MipcaActivityCapture;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;

import android.app.Activity;  
import android.content.Context;  
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.PopupWindow; 

public class PopWindowView extends PopupWindow{  
    private View conentView;  
    private Intent intent;
    private Activity  context;
    private HomeService homeService;
    
    public PopWindowView(final Activity context,final ArrayList<GridViewInfo> list){
    	this.context = context;
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.popup_window, null);  
        int h = context.getWindowManager().getDefaultDisplay().getHeight();  
        int w = context.getWindowManager().getDefaultDisplay().getWidth();  
     // 设置SelectPicPopupWindow的View  
        this.setContentView(conentView);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true); 
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationPreview);  
        conentView.findViewById(R.id.rl_mail).setOnClickListener(new OnClickListener() { //邮件 
  
            @Override  
            public void onClick(View arg0) {
				AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
				mAuthTimeUtils.IsAuthTime(context, Contants.Html5.YJ, "xyj", "1", "xyj","");
            	PopWindowView.this.dismiss();  
            }  
        });  
        conentView.findViewById(R.id.rl_examination).setOnClickListener(new OnClickListener() {  //审批
        	
        	@Override  
        	public void onClick(View arg0) {
				AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
//				mAuthTimeUtils.IsAuthTime(context,Contants.Html5.SP, "sp", "0", "sp","");
				mAuthTimeUtils.IsAuthTime(context,Contants.Html5.SP, "tlmyapps", "1", "tlmyapps","");
        		PopWindowView.this.dismiss();
        	}  
        });  
        conentView.findViewById(R.id.rl_sign).setOnClickListener(new OnClickListener() { // 签到
        	
        	@Override  
        	public void onClick(View arg0) {
				AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
				mAuthTimeUtils.IsAuthTime(context,Contants.Html5.QIANDAO, "qiandao", "1", "qiandao","");
        		PopWindowView.this.dismiss();
        	}  
        });  
        conentView.findViewById(R.id.rl_saoyisao).setOnClickListener(new OnClickListener() { // 扫一扫
        	
        	@Override  
        	public void onClick(View arg0) {
        		context.startActivity(new Intent(context, MipcaActivityCapture.class));
        		PopWindowView.this.dismiss(); 
        	}  
        });  
    }  
    
    /**
     * 筛选并跳转url
     * @return
     */
      private void screeningUrl(Activity context,ArrayList<GridViewInfo> list ,String name){
    	  String url = null;
    	  String oauthType = null;
    	  String developerCode  = null;
    	  String clientCode  = null;
    	  if(list.size() > 0 ){
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).name.equals(name)){
						url = list.get(i).sso;
						oauthType = list.get(i).oauthType;
						developerCode = list.get(i).developerCode;
						clientCode = list.get(i).clientCode;
						break ;
					}
				}
				if(url != null && url.length() != 0){
					AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
					mAuthTimeUtils.IsAuthTime(context,url, clientCode, oauthType, developerCode,"");
				}
			}
      }

	 /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent, 0, 0);  
        } else {  
            this.dismiss();  
        }  
    } 
}  