package com.youmai.hxsdk.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.utils.StringUtils;


/**
 * Created by colin on 2016/7/19.
 */
public class BaseFragment extends Fragment {

    protected Activity mAct;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAct = getActivity();
    }



}
