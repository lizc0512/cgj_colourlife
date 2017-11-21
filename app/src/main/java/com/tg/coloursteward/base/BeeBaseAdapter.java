package com.tg.coloursteward.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
public abstract class BeeBaseAdapter extends BaseAdapter
{
    protected LayoutInflater mInflater = null;
    protected Context mContext;

    public List dataList = new ArrayList();
    protected static final int TYPE_ITEM = 0;
    protected static final int TYPE_FOOTER = 1;
    protected static final int TYPE_HEADER = 2;

    public BeeBaseAdapter() {

    }


    public class BeeCellHolder
    {
        public int position;
    }

    public BeeBaseAdapter(Context c, List dataList)
    {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        this.dataList = dataList;
    }

    protected abstract BeeCellHolder createCellHolder(View cellView);
    protected abstract View bindData(int position, View cellView, ViewGroup parent, BeeCellHolder h);
    public abstract View createCellView();

    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    public int getItemViewType(int position)
    {
        return TYPE_ITEM;
    }

    /* (non-Javadoc)
     * @see com.miyoo.lottery.adapter.HummerBaseAdapter#getCount()
     */
    @Override
    public int getCount()
    {
        int count = dataList != null?dataList.size():0;
        return count;
    }

    /* (non-Javadoc)
     * @see com.miyoo.lottery.adapter.HummerBaseAdapter#getItem(int)
     */
    @Override
    public Object getItem(int position)
    {
        if (0 <= position && position < getCount())
        {
            return dataList.get(position);
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    protected View dequeueReuseableCellView(int position, View convertView,
                                            ViewGroup parent)
    {
        return null;
    }

    public void update(int newState)
    {
        notifyDataSetInvalidated();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View cellView, ViewGroup parent) {

        BeeCellHolder holder = null;
        if (cellView == null )
        {
            cellView = createCellView();
            holder = createCellHolder(cellView);
            if (null != holder) 
            {
            	cellView.setTag(holder);
			}
            
        }
        else
        {
            holder = (BeeCellHolder)cellView.getTag();
        }

        if(null != holder)
        {
        	holder.position = position;
        }
        
        bindData(position, cellView, parent, holder);
        return cellView;
    }
}

