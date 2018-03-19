package com.youmai.hxsdk.view.text;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.IMListAdapter;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by fylder on 2017/3/7.
 */

public class ItemTextView extends CopeTextView {

    Context mContext;

    private int position;

    OnCopeListener onClickFirst;
    private boolean mIsLongClick = false;

    private boolean canShow = true;
    private boolean deleteShow = true;

    private TipView tipView;
    public float mRawX;
    public float mRawY;

    WeakReference<IMListAdapter> adapterRef;

    public ItemTextView(Context context) {
        super(context);
    }

    public ItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttrs(attrs);
        init();
    }

    public ItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CopeTextView);
        position = typedArray.getInteger(R.styleable.CopeTextView_position, 0);
        typedArray.recycle();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.GONE) {
            if (tipView != null && tipView.isShowing()) {
                tipView.dismiss();
            }
        }
    }

    public void setAdapter(IMListAdapter adapter) {
        this.adapterRef = new WeakReference<>(adapter);
    }

    private void init() {

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (adapterRef.get().isShowSelect) {
                    return true;
                }
                if (canShow) {
                    mIsLongClick = true;
                    List<TipBean> tips;

                    if (position == 0) {
                        tips = TipsType.getLShareType();
                    } else {
                        tips = TipsType.getLShareType(); //自己的
                    }
                    tipView = new TipView(mContext, tips, mRawX, mRawY);
                    tipView.setListener(new ItemListener() {
                        @Override
                        public void delete() {
                            if (onClickFirst != null) {
                                onClickFirst.delete();//回调任务
                            }
                        }

                        @Override
                        public void copy() {
                            ClipboardManager cpb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cpb != null) {
                                cpb.setPrimaryClip(ClipData.newPlainText(null, getText().toString()));//加入剪贴板
                            }
                            if (onClickFirst != null) {
                                onClickFirst.copeText();
                            }
                        }

                        @Override
                        public void collect() {
                            if (onClickFirst != null) {
                                onClickFirst.collect();
                            }
                        }

                        @Override
                        public void forward() {
                            if (onClickFirst != null) {
                                onClickFirst.forwardText(getText());//回调任务
                            }
                        }

                        @Override
                        public void read() {
                            if (onClickFirst != null) {
                                onClickFirst.read();
                            }
                        }

                        @Override
                        public void remind() {
                            if (onClickFirst != null) {
                                onClickFirst.remind();//回调任务
                            }
                        }

                        @Override
                        public void turnText() {
                            Toast.makeText(mContext, "click:转文字", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void more() {
                            if (onClickFirst != null) {
                                onClickFirst.more();//回调任务
                            }
                        }
                    });
                    tipView.show(ItemTextView.this);

                }
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                if (canShow) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        return mIsLongClick;
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mIsLongClick = false;
                        return false;
                    }
                }
                return false;
            }
        });
    }


    /**
     * 复制选项操作
     */
    private void select(View view) {
        TextView copeText = (TextView) view.findViewById(R.id.text_select_cope);
        copeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cpb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    cpb.setPrimaryClip(ClipData.newPlainText(null, getText().toString()));//加入剪贴板
                }
                if (onClickFirst != null) {
                    onClickFirst.copeText();//回调任务
                }
            }
        });
    }

    /**
     * 转发选项操作
     */
    private void forward(View view) {
        TextView forwardText = (TextView) view.findViewById(R.id.text_select_forward);
        forwardText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickFirst != null) {
                    onClickFirst.forwardText(getText());//回调任务
                }
            }
        });
    }

    /**
     * 收藏选项操作
     */
    private void collect(View view) {
        TextView forwardText = (TextView) view.findViewById(R.id.text_select_collect);
        forwardText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickFirst != null) {
                    onClickFirst.collect();//回调任务
                }
            }
        });
    }

    /**
     * 提醒选项操作
     */
    private void remind(View view) {
        TextView remindText = (TextView) view.findViewById(R.id.text_select_remind);
        remindText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickFirst != null) {
                    onClickFirst.remind();
                }
            }
        });
    }

    /**
     * 删除选项操作
     */
    private void delete(View view) {
        TextView deleteText = (TextView) view.findViewById(R.id.text_select_delete);
        View line = view.findViewById(R.id.text_select_delete_line);
        if (deleteShow) {
            deleteText.setVisibility(VISIBLE);
            line.setVisibility(VISIBLE);
            deleteText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickFirst != null) {
                        onClickFirst.delete();//回调任务
                    }
                }
            });
        } else {
            deleteText.setVisibility(GONE);
            line.setVisibility(GONE);
        }
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    /**
     * 是否显示删除选项
     * <p>
     * 问题反馈不需要删除操作
     *
     * @param deleteShow
     */
    public void setDeleteShow(boolean deleteShow) {
        this.deleteShow = deleteShow;
    }

    public void setOnClickLis(OnCopeListener l) {
        this.onClickFirst = l;
    }


    public interface OnCopeListener {

        void copeText();

        void forwardText(CharSequence str);

        void collect();

        void read();

        void remind();

        void delete();

        void more();

    }

}