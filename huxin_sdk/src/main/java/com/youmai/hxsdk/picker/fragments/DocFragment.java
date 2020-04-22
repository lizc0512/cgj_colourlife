package com.youmai.hxsdk.picker.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.youmai.hxsdk.picker.PickerManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.picker.adapters.FileListAdapter;
import com.youmai.hxsdk.picker.models.Document;
import com.youmai.hxsdk.picker.models.FileType;


public class DocFragment extends BaseFragment {

    private static final String TAG = DocFragment.class.getSimpleName();
    RecyclerView recyclerView;

    TextView emptyView;

    private PhotoPickerFragmentListener mListener;
    private FileListAdapter fileListAdapter;
    private FileType fileType;

    public DocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.hx_fragment_photo_picker, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoPickerFragmentListener) {
            mListener = (PhotoPickerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PhotoPickerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DocFragment newInstance(FileType fileType) {
        DocFragment photoPickerFragment = new DocFragment();
        Bundle bun = new Bundle();
        bun.putParcelable(FILE_TYPE, fileType);
        photoPickerFragment.setArguments(bun);
        return photoPickerFragment;
    }

    public FileType getFileType() {
        return getArguments().getParcelable(FILE_TYPE);
    }

    public interface PhotoPickerFragmentListener {
        // TODO: Update argument type and name

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        fileType = getArguments().getParcelable(FILE_TYPE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVisibility(View.GONE);
    }

    public void updateList(List<Document> dirs) {
        if (getView() == null)
            return;

        if (dirs.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            FileListAdapter fileListAdapter = (FileListAdapter) recyclerView.getAdapter();
            if (fileListAdapter == null) {
                fileListAdapter = new FileListAdapter(getActivity(), dirs, PickerManager.getInstance().getSelectedFiles());

                recyclerView.setAdapter(fileListAdapter);
            } else {
                fileListAdapter.setData(dirs);
                fileListAdapter.notifyDataSetChanged();
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

}
