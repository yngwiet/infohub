package com.wc.kwinfo.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wc.kwinfo.R;

import java.util.ArrayList;

/**
 * Created by wang on 2015/10/23.
 */
public class MyGridViewAdapter extends BaseAdapter {

    private static final String TAG = MyGridViewAdapter.class.getSimpleName();

    static class ViewHolder{
        ImageView categoryCover;
        TextView categoryName;
    }

    public static class GridItem{
        String name;
        int imageRes;
        GridItem(String name, int imageRes){
            this.name = name;
            this.imageRes = imageRes;
        }
    }

    private LayoutInflater layoutInflater;
    private ArrayList<GridItem> gridData = new ArrayList<>();
    private Context mContext;

    public MyGridViewAdapter(ArrayList<GridItem> gridData, Context context){
        this.mContext = context;
        this.gridData = gridData;
        this.layoutInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return gridData.size();
    }

    @Override
    public Object getItem(int position) {
        return gridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            Log.e(TAG, "gridview position: " + position + " convertView null");
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_grid_child, null);
            holder.categoryName = (TextView) convertView.findViewById(R.id.tv_news_category_name);
            holder.categoryCover = (ImageView) convertView.findViewById(R.id.iv_news_category_cover);
            convertView.setTag(holder);
        } else{
            Log.d(TAG, "gridview position :"+position+" convertView not null");
            holder = (ViewHolder) convertView.getTag();
        }
        holder.categoryName.setText(gridData.get(position).name);
        holder.categoryCover.setImageResource(gridData.get(position).imageRes);
        return convertView;
    }
}
