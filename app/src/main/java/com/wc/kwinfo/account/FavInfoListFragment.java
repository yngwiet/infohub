package com.wc.kwinfo.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.wc.kwinfo.R;
import com.wc.kwinfo.events.DescriptionFragment;
import com.wc.kwinfo.events.EventItem;
import com.wc.kwinfo.events.EventListAdapter;
import com.wc.kwinfo.news.MyListViewAdapter;
import com.wc.kwinfo.news.RSSReader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wang on 2015/11/21.
 */
public class FavInfoListFragment extends Fragment {
    private static final String TAG = FavInfoListFragment.class.getSimpleName();
    private Context mContext;
    private ProgressDialog progressDialog;
    private View newsListView;
    private View eventListView;
    private MyListViewAdapter newsAdapter;
    private EventListAdapter eventsAdapter;
    private OnFavNewsItemLongClickListener onFavNewsItemLongClickListener;
    private onFavEventItemClickListener onFavEventItemClickListener;
    private onFavEventItemLongClickListener onFavEventItemLongClickListener;
    private List<ParseObject> newsObjects;
    private List<ParseObject> eventsObjects;

    public interface OnFavNewsItemLongClickListener{
        void onFavNewsItemLongClicked(int position, String objectId, String type);
    }

    public interface onFavEventItemClickListener{
        void onFavEventItemClicked(String description);
    }

    public interface onFavEventItemLongClickListener {
        void onFavEventItemLongClickListener(int position, String objectId, String type);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        mContext = getContext();
//        progressDialog = ProgressDialog.show(mContext, null, "Loading...", true, true);
        Bundle bundle = getArguments();
        int id = bundle.getInt("id");
        switch (id){
            case 0: // favourite news
                if (newsObjects == null){
                    newsListView = inflater.inflate(R.layout.activity_news, container, false);
                    ListView lv_news = (ListView) newsListView.findViewById(R.id.lv_news);
                    TextView tv_noNews = (TextView) newsListView.findViewById(R.id.tv_no_items);
                    WebView webView = (WebView) newsListView.findViewById(R.id.webview_news);
                    showFavouriteNews(lv_news, tv_noNews, webView);
                }
                return newsListView;
            case 1:
                if (eventsObjects == null){
                    eventListView = inflater.inflate(R.layout.fragment_fav_events_list, container, false);
                    ListView lv_events = (ListView) eventListView.findViewById(android.R.id.list);
                    TextView tv_noEvents = (TextView) eventListView.findViewById(R.id.tv_no_items);
                    showFavouriteEvents(lv_events, tv_noEvents);
                }
                return eventListView;
            default:
                return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFavNewsItemLongClickListener = (OnFavNewsItemLongClickListener) context;
        onFavEventItemClickListener = (FavInfoListFragment.onFavEventItemClickListener) context;
        onFavEventItemLongClickListener = (FavInfoListFragment.onFavEventItemLongClickListener) context;
    }

    public void refreshNewsAdapter(int newsPos){
        newsAdapter.deleteNewsItem(newsPos);
        newsAdapter.notifyDataSetChanged();
    }

    public void refreshEventsAdapter(int eventPos){
        eventsAdapter.deleteEventItem(eventPos);
        eventsAdapter.notifyDataSetChanged();
    }

    private void showFavouriteNews(final ListView newsListView, final TextView tv_noItems, final WebView webView){
        progressDialog = ProgressDialog.show(mContext, null, "Loading...", true, true);
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation("favNews");
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "get fav news success");
                    if (objects.isEmpty()) {
                        progressDialog.dismiss();
                        tv_noItems.setVisibility(View.VISIBLE);
                        return;
                    }
                    Collections.sort(objects, new Comparator<ParseObject>() {
                        @Override
                        public int compare(ParseObject lhs, ParseObject rhs) {
                            return rhs.getUpdatedAt().compareTo(lhs.getUpdatedAt());
                        }
                    });
                    newsObjects = objects;
                    List<RSSReader.Item> favNewsList = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        favNewsList.add(new RSSReader.Item(objects.get(i).getString("title"),
                                objects.get(i).getString("link"), objects.get(i).getString("description"),
                                objects.get(i).getString("imgLink")));
                    }
                    int[] readFlagsArray = new int[objects.size()];
                    for (int i = 0; i < objects.size(); i++) {
                        readFlagsArray[i] = 0;
                    }
                    newsAdapter = new MyListViewAdapter(favNewsList, mContext, readFlagsArray);
                    progressDialog.dismiss();
                    newsListView.setAdapter(newsAdapter);
                    newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            view.setSelected(true);
                            newsAdapter.setReadFlagArray(position); // set the clicked item as read
                            newsAdapter.notifyDataSetChanged();
                            MyListViewAdapter.ViewHolder holder = (MyListViewAdapter.ViewHolder) view.getTag();
                            if (holder.link != null) {
                                webView.loadUrl(holder.link);
                            }
                        }
                    });

                    newsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            ParseObject object = newsObjects.get(position);
                            onFavNewsItemLongClickListener.onFavNewsItemLongClicked(
                                    position, object.getObjectId(), "favNews");
                            return true;
                        }
                    });

                } else {
                    Log.e(TAG, "exception on getting fav news");
                    Toast.makeText(mContext, R.string.toast_get_related_parse_objects_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFavouriteEvents(final ListView eventsListView, final TextView tv_noItems){
        progressDialog = ProgressDialog.show(mContext, null, "Loading...", true, true);
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation("favEvents");
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    Log.d(TAG, "get fav events success");
                    if (objects.isEmpty()){
                        Log.e(TAG, "fav events is empty");
                        progressDialog.dismiss();
                        tv_noItems.setVisibility(View.VISIBLE);
                        return;
                    }
                    Collections.sort(objects, new Comparator<ParseObject>() {
                        @Override
                        public int compare(ParseObject lhs, ParseObject rhs) {
                            return lhs.getString("startTime").compareTo(rhs.getString("startTime"));
                        }
                    });
                    eventsObjects = objects;
                    final List<EventItem> favEventsList = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++){
                        favEventsList.add(new EventItem(objects.get(i).getString("id"),
                                objects.get(i).getString("name"), objects.get(i).getString("location"),
                                objects.get(i).getString("description"), objects.get(i).getString("coverLink"),
                                objects.get(i).getString("startTime"), objects.get(i).getString("endTime"),
                                objects.get(i).getInt("attendingCount"), objects.get(i).getInt("maybeCount"),
                                objects.get(i).getInt("declinedCount")));
                    }
                    int[] readFlagsArray = new int[objects.size()];
                    for (int i = 0; i < objects.size(); i++){
                        readFlagsArray[i] = 0;
                    }
                    eventsAdapter = new EventListAdapter(favEventsList, mContext, readFlagsArray);
                    progressDialog.dismiss();
                    eventsListView.setAdapter(eventsAdapter);
                    eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            view.setSelected(true);
                            eventsAdapter.setReadFlagArray(position); // set the clicked item as read
                            onFavEventItemClickListener.onFavEventItemClicked(favEventsList.get(position).description);
                        }
                    });

                    eventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            ParseObject object = eventsObjects.get(position);
                            onFavEventItemLongClickListener.onFavEventItemLongClickListener(
                                    position,object.getObjectId(),"favEvents");
                            return true;
                        }
                    });

                } else {
                    Log.e(TAG, "exception on getting fav events");
                    Toast.makeText(mContext, R.string.toast_get_related_parse_objects_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
