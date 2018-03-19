package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SettingShowActivity;
import com.youmai.hxsdk.activity.ShowResultActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.dialog.HxShowDialog;
import com.youmai.hxsdk.entity.ShowModel;
import com.youmai.hxsdk.fragment.SettingShowFragment;

import java.util.ArrayList;
import java.util.List;


public class SettingShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum UI_Type {
        FROM_CAMERA, FROM_PICTURE
    }

    private Context mContext;
    private List<ShowModel.DBean.ModelsBean> mList = new ArrayList<>();
    private SettingShowFragment mSettingShowFragment;

    public SettingShowAdapter(Context context, SettingShowFragment fragment) {
        this.mContext = context;
        mSettingShowFragment = fragment;
        mList.add(new ShowModel.DBean.ModelsBean());
    }


    public void setList(List<ShowModel.DBean.ModelsBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public int getItemViewType(int position) {  //给Item划分类别
        int type = UI_Type.FROM_CAMERA.ordinal();
        if (position != 0) {
            type = UI_Type.FROM_PICTURE.ordinal();
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (SettingShowAdapter.UI_Type.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case FROM_CAMERA: {
                view = inflater.inflate(R.layout.hx_camera_item, parent, false);
                holder = new SettingShowAdapter.CameraViewHolder(view);
            }
            break;
            case FROM_PICTURE: {
                view = inflater.inflate(R.layout.hx_show_item, parent, false);
                holder = new SettingShowAdapter.ImageViewHolder(view);
            }
            break;

        }
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ShowModel.DBean.ModelsBean item = mList.get(position);

        if (viewHolder instanceof ImageViewHolder) {
            int roundPx = mContext.getResources().getDimensionPixelOffset(R.dimen.hx_show_round);

            final int id = item.getId();
            final String fid = item.getFid();
            final int fileType = item.getFileType();
            final String url = AppConfig.getImageUrl(mContext, fid);

            ImageView imageView = ((ImageViewHolder) viewHolder).imgShow;

            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.hx_pic_showmodel_bg)  //占位图片
                            .error(R.drawable.hx_pic_showmodel_bg))        //下载失败)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowResultActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("fid", fid);
                    intent.putExtra("fileType", fileType);
                    intent.putExtra("url", url);

                    mSettingShowFragment.getActivity().startActivityForResult(intent, SettingShowActivity.REQ_SHOW_DETAIL);

//                    Activity act = mSettingShowFragment.getActivity();
//                    if (!(act instanceof HuxinFragmentActivity)) {
//                        mSettingShowFragment.getActivity().finish();
//                    }

                }

            });
        }
    }

    private class CameraViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        RelativeLayout tvCamera;

        private CameraViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvCamera = (RelativeLayout) itemView.findViewById(R.id.rl_camera);
            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //自定义按钮事件
                    HxShowDialog hxShowDialog = new HxShowDialog(mContext);
                    HxShowDialog.HxPshowDialogCallback callback =
                            new HxShowDialog.HxPshowDialogCallback() {
                                @Override
                                public void onCallCamera() {
                                    mSettingShowFragment.fromCamera();
                                }

                                @Override
                                public void onCallPhotos() {
                                    mSettingShowFragment.fromPhoto();
                                }

                                @Override
                                public void onCallMovie() {
                                    mSettingShowFragment.fromMovie();
                                }
                            };

                    hxShowDialog.setHxPshowDialog(callback);
                    hxShowDialog.show();
                }
            });
        }

    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imgShow;

        private ImageViewHolder(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgShow = (ImageView) itemView.findViewById(R.id.img_show);
        }
    }
}


