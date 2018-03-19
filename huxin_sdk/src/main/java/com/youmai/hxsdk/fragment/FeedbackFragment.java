package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.http.IPostListener;

/**
 * Created by ZhouXin on 2016/8/5.
 */
public class FeedbackFragment extends BaseFragment implements View.OnClickListener {

    public final static String TAG = FeedbackFragment.class.getSimpleName();

    private EditText ed_body;
    private EditText ed_name;


    private Button btn_feet;

    private RadioButton rd_suggest;
    private RadioButton rd_report;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ed_body = (EditText) view.findViewById(R.id.ed_feedback);
        btn_feet = (Button) view.findViewById(R.id.btn_sendfeed);
        btn_feet.setOnClickListener(this);
        ed_name = (EditText) view.findViewById(R.id.ed_username);
        rd_suggest = (RadioButton) view.findViewById(R.id.rd_suggest);
        rd_report = (RadioButton) view.findViewById(R.id.rd_report);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sendfeed) {
            String text = ed_body.getText().toString();
            if (text != "") {
                sendFeedback();
            }

        } else {
        }
    }

    private void sendFeedback() {
        String name = ed_name.getText().toString();
        String body = ed_body.getText().toString();
        int type;
        if (rd_suggest.isChecked()) {
            type = 0;
        } else {
            type = 1;
        }
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                Toast.makeText(mAct, response, Toast.LENGTH_SHORT).show();
            }
        };
        HuxinSdkManager.instance().sendFeedback(body, callback);
    }

}
