package com.wc.kwinfo.events;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by wang on 2015/11/22.
 */
public class EventListAdapter extends BaseAdapter {
    static class ViewHolder{
        ImageView cover;
        TextView name;
        TextView location;
        TextView start_time;
        TextView end_time;
        TextView attending;
        TextView maybe;
        TextView declined;
    }

    private List<EventItem> eventItemArrayList = new ArrayList<>();
    private DisplayImageOptions displayImageOptions;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int[] readFlagArray; // for determining if the item should be set as read

    public EventListAdapter(List<EventItem> eventItemArrayList, Context context, int[] readFlagArray){
        this.readFlagArray = readFlagArray;
        this.eventItemArrayList = eventItemArrayList;
        // ImageLoader display option
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.event_cover)
                .showImageOnLoading(R.mipmap.event_cover)
                .cacheOnDisk(true)
                .cacheInMemory(false) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) //default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
        this.mContext = context;
        this.layoutInflater = LayoutInflater.from(this.mContext);
    }

    public void setReadFlagArray(int position){
        readFlagArray[position] = 1;
    }

    public void deleteEventItem(int eventPos){
        EventItem deletedItem = eventItemArrayList.get(eventPos);
        Iterator<EventItem> iterator = eventItemArrayList.iterator();
        while (iterator.hasNext()){
            if (iterator.next().id.equals(deletedItem.id)){
                iterator.remove();
            }
        }
    }

    @Override
    public int getCount() {
        return eventItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventItemArrayList.get(position);
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
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_event_list_child, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_event_name);
            holder.location = (TextView) convertView.findViewById(R.id.tv_event_location);
            holder.start_time = (TextView) convertView.findViewById(R.id.tv_event_start_time);
            holder.end_time = (TextView) convertView.findViewById(R.id.tv_event_end_time);
            holder.attending = (TextView) convertView.findViewById(R.id.tv_event_attending_count);
            holder.maybe = (TextView) convertView.findViewById(R.id.tv_event_maybe_count);
            holder.declined = (TextView) convertView.findViewById(R.id.tv_event_declined_count);
            holder.cover = (ImageView) convertView.findViewById(R.id.iv_event_cover);

            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
        Resources res = mContext.getResources();
        holder.name.setText(eventItemArrayList.get(position).name);
        String start_time = String.format(res.getString(R.string.tv_event_start_time),
                eventItemArrayList.get(position).startTime);
        holder.start_time.setText(start_time);
        String end_time = String.format(res.getString(R.string.tv_event_end_time),
                eventItemArrayList.get(position).endTime);
        holder.end_time.setText(end_time);
        holder.location.setText(eventItemArrayList.get(position).location);
        String attending = String.format(res.getString(R.string.tv_event_attending),
                eventItemArrayList.get(position).attendingCount);
        holder.attending.setText(attending);
        String maybe = String.format(res.getString(R.string.tv_event_maybe),
                eventItemArrayList.get(position).maybeCount);
        holder.maybe.setText(maybe);
        String declined = String.format(res.getString(R.string.tv_event_declined),
                eventItemArrayList.get(position).declinedCount);
        holder.declined.setText(declined);


        if (readFlagArray[position] == 1){
            holder.name.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.start_time.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.end_time.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.location.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.attending.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.maybe.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.declined.setTextColor(mContext.getResources().getColor(R.color.grey));
        }

        ImageLoader.getInstance().displayImage(eventItemArrayList.get(position).coverLink,
                holder.cover, displayImageOptions);

        return convertView;
    }
}
