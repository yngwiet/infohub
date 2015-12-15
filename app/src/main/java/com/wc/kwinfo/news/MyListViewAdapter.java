package com.wc.kwinfo.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.wc.kwinfo.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wang on 2015/10/18.
 */
public class MyListViewAdapter extends BaseAdapter {
    private static final String TAG = MyListViewAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<RSSReader.Item> mList;
    private DisplayImageOptions displayImageOptions;
    private int[] readFlagArray; // for determining if the item should be set as read
    public MyListViewAdapter(List<RSSReader.Item> list, Context context, int[] readFlagArray){
        this.mList = list;
        this.mContext = context;
        this.layoutInflater = LayoutInflater.from(this.mContext);
        this.readFlagArray = readFlagArray;
        // ImageLoader display option
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.news_cover)
                .showImageOnLoading(R.mipmap.news_cover)
                .cacheOnDisk(true)
                .cacheInMemory(false) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) //default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
    }

    public void setReadFlagArray(int position){
        readFlagArray[position] = 1;
    }

    public void deleteNewsItem(int newsPos){
        RSSReader.Item deletedItem = mList.get(newsPos);
        Iterator<RSSReader.Item> iterator = mList.iterator();
        while (iterator.hasNext()){
            if (iterator.next().link.equals(deletedItem.link)){
                iterator.remove();
            }
        }
    }

    public static class ViewHolder{
        public TextView title;
        public TextView content;
        public ImageView cover;
        public String link;
    }

    @Override
    public int getCount() {
        // how many lines will be in ListView
        // will mList be null ??
        return this.mList.size();
    }

    @Override
    public Object getItem(int position) {

        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // use view holder pattern
        ViewHolder holder;
//        if (convertView == null){
//            Log.e(TAG, "listview position: "+position+" convertView null");
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_list_child, null);
            holder.title = (TextView) convertView.findViewById(R.id.tv_news_title);
            holder.content = (TextView) convertView.findViewById(R.id.tv_news_content);
            holder.cover = (ImageView) convertView.findViewById(R.id.iv_news_cover);
            convertView.setTag(holder);
//        } else{
//            Log.d(TAG, "listview position :"+position+" convertView not null");
//            holder = (ViewHolder) convertView.getTag();
//        }
        if (holder.title != null){
            holder.title.setText(mList.get(position).title);
            if (readFlagArray[position] == 1){
                holder.title.setTextColor(mContext.getResources().getColor(R.color.grey));
            }
        }
        if (holder.content != null){
            holder.content.setText(mList.get(position).description);
            if (readFlagArray[position] == 1){
                holder.content.setTextColor(mContext.getResources().getColor(R.color.grey));
            }
        }
        holder.link = mList.get(position).link;
        ImageLoader.getInstance().displayImage(mList.get(position).imgLink, holder.cover, displayImageOptions);
        return convertView;
    }
}
