package com.tg.coloursteward;


import com.tg.coloursteward.adapter.DownloadAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.view.dialog.ToastFactory;

/**
 * 我的下载
 * @author Administrator
 *
 */
public class DownloadManagerActivity extends BaseActivity {
	File directory = new File(Contants.DOWN.DOWNLOAD_DIRECT);
	private DownloadAdapter mAdapter;
	private SwipeMenuListView mListView;
	private List<String> items = null;
	private List<String> paths = null;// 存放当前目录所有文件路径
	private RelativeLayout mRela_download_noinfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}
	private void initUI() {
		mRela_download_noinfo = (RelativeLayout) findViewById(R.id.rela_download_noinfo);
		mListView = (SwipeMenuListView) findViewById(R.id.download_listview);
		getFileDir(directory);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);
		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				Tools.delAllFile(paths.get(position));
				items.remove(position);
				paths.remove(position);
				mAdapter.notifyDataSetChanged();
				if (items.size() == 0) {
					mListView.setVisibility(View.GONE);
					mRela_download_noinfo.setVisibility(View.VISIBLE);
				} else {
					mListView.setVisibility(View.VISIBLE);
					mRela_download_noinfo.setVisibility(View.GONE);
				}
				return false;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = paths.get(position);
				Log.e("setOnItemClickListener", "path:" + path);
				File file = new File(path);
				try{
					Intent fileIntent = Tools.getFileIntent(file);
					startActivity(fileIntent);
					Intent.createChooser(fileIntent, "请选择对应的软件打开该附件！");
				}catch (ActivityNotFoundException e) {
				// TODO: handle exception
					ToastFactory.showToast(DownloadManagerActivity.this,"附件不能打开，请下载相关软件！");
				}
		}
		});
	}
	private void getFileDir(File f) {
		if (items == null) {
			items = new ArrayList<String>();
			paths = new ArrayList<String>();
		} else {
			items.clear();
			paths.clear();
		}
		// 排序
		File[] files = sortFiles(f);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				items.add(file.getName());
				paths.add(file.getPath());
			}
			mAdapter = new DownloadAdapter(this, items, paths);
			mListView.setAdapter(mAdapter);
		}
		if (items.size() == 0) {
			mListView.setVisibility(View.GONE);
			mRela_download_noinfo.setVisibility(View.VISIBLE);
		} else {
			mListView.setVisibility(View.VISIBLE);
			mRela_download_noinfo.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @param f
	 *            按修改时间排序文件夹下文件并返回数组
	 * @return
	 */
	private File[] sortFiles(File f) {
		File[] files = f.listFiles();
		if (files != null) {
			Pair[] pairs = new Pair[files.length];
			for (int i = 0; i < files.length; i++)
				pairs[i] = new Pair(files[i]);

			// Sort them by timestamp.
			Arrays.sort(pairs);

			// Take the sorted pairs and extract only the file part, discarding
			// the
			// timestamp if modify meanwhile.
			for (int i = 0; i < files.length; i++)
				files[i] = pairs[i].f;
		}
		return files;
	}

	class Pair implements Comparable<Pair> {
		public long t;
		public File f;

		public Pair(File file) {
			f = file;
			t = file.lastModified();
		}

		public int compareTo(Pair p) {
			long u = p.t;
			return t < u ? -1 : t == u ? 0 : 1;
		}
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_download_manager, null);
	}

	@Override
	public String getHeadTitle() {
		return "我的下载";
	}

}
