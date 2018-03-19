package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.youmai.hxsdk.R;

import pl.droidsonroids.gif.GifImageView;


public class GifViewFragment extends BaseFragment {
    public final static String TAG = GifViewFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_gif, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String url_test2 = "http://upload.gezila.com/data/20150403/89491428024781.gif";
        String url_test3 = "http://i.imgur.com/1ALnB2s.gif";

        ImageView img_test1 = (ImageView) view.findViewById(R.id.img_test1);
        ImageView img_test2 = (ImageView) view.findViewById(R.id.img_test2);
        ImageView img_test3 = (ImageView) view.findViewById(R.id.img_test3);
        ImageView img_test4 = (ImageView) view.findViewById(R.id.img_test4);
        ImageView img_test5 = (ImageView) view.findViewById(R.id.img_test5);




        GifImageView img_gif = (GifImageView) view.findViewById(R.id.img_gif);
        img_gif.setImageResource(R.drawable.hx_kb);




        /*Glide.with(mAct)
                .load(test)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(img_test5);*/

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
