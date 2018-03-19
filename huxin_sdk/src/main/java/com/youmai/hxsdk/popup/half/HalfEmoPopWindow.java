package com.youmai.hxsdk.popup.half;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.popup.SuccessPopWindow;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.recyclerview.PageIndicatorView;
import com.youmai.hxsdk.recyclerview.PageRecyclerView;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.view.full.FloatViewUtil;

import java.util.List;

/**
 * @author yw
 * @data 2016.6.27
 */
public class HalfEmoPopWindow extends PopupWindow {

    private static final int columns = 2;

    //private RelativeLayout rl_close;
    private RelativeLayout rl_emo_layout;
    private LinearLayout rl_emo;
    private PageRecyclerView mRecyclerView;
    private PageIndicatorView mCustomIndicator;
    private List<EmoInfo.EmoItem> dataList = null;
    private Context mContext;
    private String dstPhone; // 对方的手机号码


    public HalfEmoPopWindow(Context context, String phone, int width, int height) {
        super(context);
        mContext = context;
        dstPhone = phone;

        initView(context);
        initAttr(width, height);
        setAdapter();
        setListener(context);

    }

    private void initAttr(int width, int height) {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);//ViewGroup.LayoutParams.WRAP_CONTENT
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(Context context) {
        rl_emo = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hx_emo_half_view, null);
        setContentView(rl_emo);

        rl_emo_layout = (RelativeLayout) rl_emo.findViewById(R.id.rl_emo_layout);
        //rl_close = (RelativeLayout) rl_emo.findViewById(R.id.rl_close);

        mRecyclerView = (PageRecyclerView) rl_emo.findViewById(R.id.recycler_emo_view);
        mCustomIndicator = (PageIndicatorView) rl_emo.findViewById(R.id.custom_indicator);// 设置指示器

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_in);
        rl_emo_layout.setAnimation(anim);// 设置弹出窗体动画效果
    }

    private void setAdapter() {

        initData();

        // 设置指示器
        mRecyclerView.setIndicator(mCustomIndicator);
        // 设置行数和列数
        mRecyclerView.setPageSize(columns, columns + 1);
        // 设置页间距
        mRecyclerView.setPageMargin(30);
        // 设置数据
        mRecyclerView.setAdapter(mRecyclerView.new PageAdapter(dataList, new PageRecyclerView.CallBack() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.hx_phiz_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ViewHolder) holder).imgGif.setImageResource(dataList.get(position).getEmoRes());
                ((ViewHolder) holder).tv_emo_name.setText(dataList.get(position).getEmoName());
            }

            @Override
            public void onItemClickListener(View view, int position) {
                final int userId = HuxinSdkManager.instance().getUserId();
                final String content = dataList.get(position).getEmoStr();

                ReceiveListener callback = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                            
                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                String log = "发送表情成功";
                                Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                            } else {
                                String log = "ErrerNo:" + ack.getErrerNo();
                                //Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                                LogFile.inStance().toFile(log);
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    }
                };

                boolean res = HuxinSdkManager.instance().sendText(userId, dstPhone, content, callback);

                if (HalfEmoPopWindow.this.isShowing()) {
                    HalfEmoPopWindow.this.dismiss();
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                /*Toast.makeText(MainActivity.this, "删除："
                        + dataList.get(position), Toast.LENGTH_SHORT).show();
                myAdapter.remove(position);*/
            }
        }));

    }

    private void initData() {
        dataList = new EmoInfo(mContext).getEmoList();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgGif;
        private TextView tv_emo_name;

        public ViewHolder(View itemView) {
            super(itemView);
            imgGif = (ImageView) itemView.findViewById(R.id.emo_img_gif);
            tv_emo_name = (TextView) itemView.findViewById(R.id.tv_emo_gif_name);
        }
    }

//	class PaddingItemDecoration extends RecyclerView.ItemDecoration {
//		private int mSpace;
//		public PaddingItemDecoration(int space) {
//			mSpace = space;
//		}
//
//		@Override
//		public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
//								   RecyclerView.State state) {
//			super.getItemOffsets(outRect, view, parent, state);
//			outRect.left = mSpace;
//			outRect.top = mSpace;
//			outRect.right = mSpace;
//			outRect.bottom = mSpace;
//		}
//	}

    private void setListener(final Context context) {

        rl_emo.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        rl_emo.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int bottom = rl_emo.getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                }
                return true;
            }
        });

//        rl_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animation animOut = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_out);
//                rl_emo_layout.setAnimation(animOut);
//                dismiss();
//            }
//        });
    }

    private IOnEmoCompleteListener listener;

    public interface IOnEmoCompleteListener {
        void onEmoSuccess();
    }

    public void setEmoListener(IOnEmoCompleteListener mListener) {
        this.listener = mListener;
    }

}
