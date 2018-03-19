package com.youmai.hxsdk.fragment;

/**
 * Created by colin on 2016/7/28.
 */

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.popup.full.FullJokesPopWindow;
import com.youmai.hxsdk.service.HuxinService;

import java.util.Random;

/**
 * 手机“摇一摇”
 */
public class ShakeFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = ShakeFragment.class.getSimpleName();


    private Button btn_shake;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_shake, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btn_shake = (Button) view.findViewById(R.id.btn_shake);

        btn_shake.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        HuxinSdkManager.instance().registerSharkListener(mSharkListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        HuxinSdkManager.instance().unregisterSharkListener();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_shake) {

            int[] soundId = {R.raw.bye, R.raw.hi, R.raw.icome, R.raw.kb, R.raw.kiss, R.raw.laugh};
            int index = new Random().nextInt(soundId.length);
            HuxinSdkManager.instance().playSound(soundId[index]);


        }
    }

    private HuxinService.SharkListener mSharkListener = new HuxinService.SharkListener() {
        @Override
        public void onShark() {

            if (isAdded()) {
                FullJokesPopWindow.instance(mAct).showAtLocation(
                        btn_shake, Gravity.LEFT | Gravity.TOP, 0, 0);
                FullJokesPopWindow.instance(mAct).getTv_jokes().setText(getJokesRandom());
            }
        }
    };

    /* 随机取段子 start by 2016.8.4 */
    public String getJokesRandom() {
        String[] jokesArray = getResources().getStringArray(R.array.hx_jokes);
        int index = new Random().nextInt(jokesArray.length);
        return jokesArray[index];
    }
}