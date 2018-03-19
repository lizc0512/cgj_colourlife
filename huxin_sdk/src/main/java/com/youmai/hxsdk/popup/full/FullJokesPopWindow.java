package com.youmai.hxsdk.popup.full;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * @author yw
 * @data 2016.6.27
 */
public class FullJokesPopWindow extends PopupWindow {

    private RelativeLayout rl_close;
    private RelativeLayout rl_jokes_layout;
    private LinearLayout rl_jokes;
    private AppCompatTextView tv_jokes;
    private TextView tv_joke_exchange, tv_joke_share;
    private Context mContext;

    private static FullJokesPopWindow instance = null;

    public static FullJokesPopWindow instance(Context context) {
        if (instance == null) {
            instance = new FullJokesPopWindow(context.getApplicationContext());
        }
        return instance;
    }

    public FullJokesPopWindow(Context context) {
        super(context);
        mContext = context;
        initView(context);
        initAttr();
        setListener(context);
    }

    public void initAttr() {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //this.setHeight(DisplayUtil.dip2px(mContext, 380f));
        this.setFocusable(false);// true设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(Context context) {
        rl_jokes = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hx_jokes_view, null);
        setContentView(rl_jokes);

        rl_jokes_layout = (RelativeLayout) rl_jokes.findViewById(R.id.rl_jokes_layout);
        rl_close = (RelativeLayout) rl_jokes.findViewById(R.id.rl_close);
        tv_jokes = (AppCompatTextView) rl_jokes.findViewById(R.id.tv_jokes);
        tv_joke_exchange = (TextView) rl_jokes.findViewById(R.id.tv_joke_exchange);
        tv_joke_share = (TextView) rl_jokes.findViewById(R.id.tv_joke_share);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_in);
        rl_jokes_layout.setAnimation(anim);// 设置弹出窗体动画效果
    }

    private void setListener(final Context context) {

        rl_jokes.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        rl_jokes.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int bottom = rl_jokes.getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animOut = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_out);
                rl_jokes_layout.setAnimation(animOut);
                dismiss();
            }
        });

        tv_joke_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onJokesNext();
                }
            }
        });

        tv_joke_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onJokesShare();
                }
            }
        });
    }

    public LinearLayout getRl_jokes() {
        return rl_jokes;
    }

    public AppCompatTextView getTv_jokes() {
        return tv_jokes;
    }

    private IOnJokesCompleteListener mListener;

    public interface IOnJokesCompleteListener {
        void onJokesNext();
        void onJokesShare();
    }

    public void setJokesListener(IOnJokesCompleteListener listener) {
        this.mListener = listener;
    }

}
