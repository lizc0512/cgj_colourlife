package com.tg.setting.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Utils;
import com.tg.setting.activity.CardSenderPhoneActivity;
import com.tg.setting.activity.KeySendKeyPhoneActivity;
import com.tg.setting.entity.KeyBuildEntity;
import com.tg.setting.entity.KeyFloorEntity;
import com.tg.setting.entity.KeyPopEntity;
import com.tg.setting.entity.KeyUnitEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class KeyChoiceRoomDialog extends Dialog implements HttpResponse, View.OnClickListener {

    private static final int INDEX_TAB_BUILD = 0;
    private static final int INDEX_TAB_UNIT = 1;
    private static final int INDEX_TAB_ROOM = 2;
    private int pageSize = 50;
    private TextView textViewBuild;
    private TextView textViewUnit;
    private TextView textViewRoom;

    private View indicator;
    private SwipeRecyclerView rv_ver;
    private int tabIndex = INDEX_TAB_BUILD;
    private UserModel userModel;
    private int buildPage = 1;
    private int unitPage = 1;
    private int roomPage = 1;
    private String communityUuid;
    private String buildName;
    private String buildUuid;
    private String unitName;
    private String unitUuid;
    private String roomName;
    private String roomUuid;
    private KeyChoiceRoomAdapter vAdapter;
    private Context mContext;
    private List<KeyPopEntity> mList = new ArrayList<>();

    public KeyChoiceRoomDialog(Context context, String communityUuid) {
        super(context, R.style.bottom_dialog);
        mContext = context;
        this.communityUuid = communityUuid;
        init(context);
    }

    public KeyChoiceRoomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public KeyChoiceRoomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_address_selector, null);
        setContentView(view);
        initView(view);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = Utils.dip2px(context, 300);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * 根据小区获取楼栋
     */
    private void getBuild() {
        userModel.getBuild(1, communityUuid, buildPage, pageSize, this);
    }

    /**
     * 根据楼栋获取单元
     */
    private void getUnit() {
        userModel.getUnit(2, buildUuid, unitPage, pageSize, this);
    }

    /**
     * 根据楼层获取房屋信息
     */
    private void getFloor() {
        userModel.getFloor(3, unitUuid, roomPage, pageSize, 0, this);
    }

    private void initView(View view) {
        textViewBuild = (TextView) view.findViewById(R.id.textViewBuild);
        textViewUnit = (TextView) view.findViewById(R.id.textViewUnit);
        textViewRoom = (TextView) view.findViewById(R.id.textViewRoom);
        indicator = view.findViewById(R.id.indicator);
        rv_ver = view.findViewById(R.id.rv_ver);
        textViewBuild.setOnClickListener(this);
        textViewUnit.setOnClickListener(this);
        textViewRoom.setOnClickListener(this);
        updateIndicator();
        userModel = new UserModel(mContext);
        rv_ver.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        vAdapter = new KeyChoiceRoomAdapter();
        rv_ver.setAdapter(vAdapter);
        rv_ver.useDefaultLoadMore();
        rv_ver.setLoadMoreListener(() -> {
            switch (tabIndex) {
                case INDEX_TAB_BUILD:
                    buildPage++;
                    getBuild();
                    break;
                case INDEX_TAB_UNIT:
                    unitPage++;
                    getUnit();
                    break;
                case INDEX_TAB_ROOM:
                    roomPage++;
                    getFloor();
                    break;
            }
        });
        getBuild();
    }


    private List<KeyBuildEntity.ContentBeanX.ContentBean> buildLists = new ArrayList<>();
    private int buildTotal = 0;
    private List<KeyUnitEntity.ContentBeanX.ContentBean> unitLists = new ArrayList<>();
    private int unitTotal = 0;
    private List<KeyFloorEntity.ContentBeanX.ContentBean> roomLists = new ArrayList<>();
    private int roomTotal = 0;

    private void inintBuildData() {
        mList.clear();
        vAdapter.notifyDataSetChanged();
        for (KeyBuildEntity.ContentBeanX.ContentBean c : buildLists) {
            KeyPopEntity bean = new KeyPopEntity();
            bean.setSelect(false);
            bean.setId(c.getBuildingUuid());
            bean.setName(c.getBuildingName());
            mList.add(bean);
        }
        boolean dataEmpty = mList.size() == 0;
        boolean hasMore = buildTotal > mList.size();
        rv_ver.loadMoreFinish(dataEmpty, hasMore);
        vAdapter.notifyDataSetChanged();
    }


    private void inintUnitdData() {
        mList.clear();
        vAdapter.notifyDataSetChanged();
        for (KeyUnitEntity.ContentBeanX.ContentBean c : unitLists) {
            KeyPopEntity bean = new KeyPopEntity();
            bean.setSelect(false);
            bean.setId(c.getUnitUuid());
            bean.setName(c.getUnitName());
            bean.setFloorNum(c.getFloorNum());
            mList.add(bean);
        }
        boolean dataEmpty = mList.size() == 0;
        boolean hasMore = unitTotal > mList.size();
        rv_ver.loadMoreFinish(dataEmpty, hasMore);
        vAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            if (mContext instanceof KeySendKeyPhoneActivity) {
                ((KeySendKeyPhoneActivity) mContext).setRoom(buildName);
            }
            if (mContext instanceof CardSenderPhoneActivity) {
                ((CardSenderPhoneActivity) mContext).setRoom(buildName);
            }
            dismiss();
        }
    }

    private void inintRoomData() {
        mList.clear();
        vAdapter.notifyDataSetChanged();
        for (KeyFloorEntity.ContentBeanX.ContentBean c : roomLists) {
            KeyPopEntity bean = new KeyPopEntity();
            bean.setSelect(false);
            bean.setName(c.getRoomNo());
            mList.add(bean);
        }
        boolean dataEmpty = mList.size() == 0;
        boolean hasMore = roomTotal > mList.size();
        rv_ver.loadMoreFinish(dataEmpty, hasMore);
        vAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            if (mContext instanceof KeySendKeyPhoneActivity) {
                ((KeySendKeyPhoneActivity) mContext).setRoom(buildName + unitName);
            }
            if (mContext instanceof CardSenderPhoneActivity) {
                ((CardSenderPhoneActivity) mContext).setRoom(buildName + unitName);
            }
            dismiss();
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == buildPage) {
                            buildLists.clear();
                        }
                        KeyBuildEntity entity = GsonUtils.gsonToBean(result, KeyBuildEntity.class);
                        KeyBuildEntity.ContentBeanX content = entity.getContent();
                        buildTotal = content.getTotalRecord();
                        buildLists.addAll(content.getContent());
                        tabIndex = INDEX_TAB_BUILD;
                        inintBuildData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == unitPage) {
                            unitLists.clear();
                        }
                        KeyUnitEntity entity = GsonUtils.gsonToBean(result, KeyUnitEntity.class);
                        KeyUnitEntity.ContentBeanX content = entity.getContent();
                        unitLists.addAll(content.getContent());
                        unitTotal = content.getTotalRecord();
                        tabIndex = INDEX_TAB_UNIT;
                        inintUnitdData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == roomPage) {
                            roomLists.clear();
                        }
                        KeyFloorEntity entity = GsonUtils.gsonToBean(result, KeyFloorEntity.class);
                        KeyFloorEntity.ContentBeanX content = entity.getContent();
                        roomLists.addAll(content.getContent());
                        tabIndex = INDEX_TAB_ROOM;
                        roomTotal = content.getTotalRecord();
                        inintRoomData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        updateTabsVisibility();
        updateIndicator();
    }


    private void updateTabsVisibility() {
        textViewBuild.setVisibility(buildLists.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        textViewUnit.setVisibility(unitLists.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        textViewRoom.setVisibility(roomLists.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateIndicator() {
        switch (tabIndex) {
            case INDEX_TAB_BUILD:
                buildIndicatorAnimatorTowards(textViewBuild).start();
                break;
            case INDEX_TAB_UNIT:
                buildIndicatorAnimatorTowards(textViewUnit).start();
                break;
            case INDEX_TAB_ROOM:
                buildIndicatorAnimatorTowards(textViewRoom).start();
                break;
        }
    }

    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = indicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);
        return set;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewBuild:  //重新渲染数据
                tabIndex = INDEX_TAB_BUILD;
                updateIndicator();
                inintBuildData();
                break;
            case R.id.textViewUnit:
                tabIndex = INDEX_TAB_UNIT;
                updateIndicator();
                inintUnitdData();
                break;
            case R.id.textViewRoom:
                tabIndex = INDEX_TAB_ROOM;
                updateIndicator();
                inintRoomData();
                break;
        }
    }


    public class KeyChoiceRoomAdapter extends RecyclerView.Adapter<KeyChoiceRoomAdapter.DefaultViewHolder> {

        @NonNull
        @Override
        public KeyChoiceRoomAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_time_ver, parent, false);
            return new KeyChoiceRoomAdapter.DefaultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull KeyChoiceRoomAdapter.DefaultViewHolder holder, int position) {
            KeyPopEntity keyPopEntity = mList.get(position);
            holder.tv_ver.setText(keyPopEntity.getName());
            holder.rl_item.setOnClickListener(v -> {
                switch (tabIndex) {
                    case INDEX_TAB_BUILD:  //楼栋
                        buildName = keyPopEntity.getName();
                        buildUuid = keyPopEntity.getId();
                        textViewBuild.setText(buildName);
                        textViewUnit.setText("请选择");
                        textViewRoom.setText("请选择");
                        getUnit();
                        break;
                    case INDEX_TAB_UNIT:  //单元
                        unitName = keyPopEntity.getName();
                        unitUuid = keyPopEntity.getId();
                        textViewUnit.setText(unitName);
                        textViewRoom.setText("请选择");
                        getFloor();
                        break;
                    case INDEX_TAB_ROOM:  //房间号
                        roomName = keyPopEntity.getName();
                        roomUuid = keyPopEntity.getId();
                        //选择结束
                        if (mContext instanceof KeySendKeyPhoneActivity) {
                            ((KeySendKeyPhoneActivity) mContext).setRoom(buildName + unitName + roomName);
                        }
                        if (mContext instanceof CardSenderPhoneActivity) {
                            ((CardSenderPhoneActivity) mContext).setRoom(buildName + unitName + roomName);
                        }
                        dismiss();
                        break;
                }
            });
            boolean checked = false;
            switch (tabIndex) {
                case INDEX_TAB_BUILD:
                    checked = keyPopEntity.getId().equals(buildUuid) ? true : false;
                    break;
                case INDEX_TAB_UNIT:
                    checked = keyPopEntity.getId().equals(unitUuid) ? true : false;
                    break;
            }
            if (checked) {
                Drawable door = mContext.getResources().getDrawable(R.drawable.ic_key_address_select);
                holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, door, null);
                holder.tv_ver.setTextColor(Color.parseColor("#1CA1F4"));
            } else {
                holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                holder.tv_ver.setTextColor(Color.parseColor("#333B46"));
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class DefaultViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout rl_item;
            TextView tv_ver;

            DefaultViewHolder(View itemView) {
                super(itemView);
                rl_item = itemView.findViewById(R.id.rl_item);
                tv_ver = itemView.findViewById(R.id.tv_ver);
            }
        }
    }
}