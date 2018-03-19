
package com.tg.coloursteward.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;


public class SignCountFragment extends Fragment {

    private static final String TAG = SignCountFragment.class.getSimpleName();

    private String title;

    public static SignCountFragment newInstance(String title) {

        SignCountFragment fragment = new SignCountFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().size() != 0) {
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initView(view);
    }


    private void initView(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

    }
}
