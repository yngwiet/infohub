package com.wc.kwinfo.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wc.kwinfo.InternetController;
import com.wc.kwinfo.ItemLongClickDialogFragment;
import com.wc.kwinfo.R;

import java.util.List;

public class NewsActivity extends AppCompatActivity implements
        ItemLongClickDialogFragment.OnDialogListItemClickListener{
    private final String TAG = NewsActivity.class.getSimpleName();
    private Context mContext = null;
    private static String NewsUrl = "";
    private MyListViewAdapter newsAdapter;
    private List<RSSReader.Item> updatedNews;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mContext = this.getApplicationContext();
        NewsUrl = getIntent().getStringArrayExtra("info")[1];

        // set action bar
        setTitle(getIntent().getStringArrayExtra("info")[0]);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = ProgressDialog.show(NewsActivity.this, null, "Loading...", true, true);
        new XmlDownloadTask().execute(NewsUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lv_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear_disc_cache:
                ImageLoader.getInstance().clearDiskCache();
                return true;
            case R.id.item_update:
                if (!InternetController.getInstance().checkNetwork()){
                    Toast.makeText(mContext, R.string.toast_network_not_available, Toast.LENGTH_SHORT).show();
                    return true;
                }
                new XmlDownloadTask().execute(NewsUrl);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart()");
        super.onRestart();
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogListItemClicked(int dialogItemPos, int infoItemPos, String objectId, String type) {
        // no need to implement this interface in this activity
    }

    @Override
    public void onDialogListItemClicked(int dialogItemPos, int infoItemPos) {
        switch (dialogItemPos){
            case 0: // mark as favourite
                final ParseObject favNews = new ParseObject("FavNews");
                favNews.put("title", updatedNews.get(infoItemPos).title);
                favNews.put("link", updatedNews.get(infoItemPos).link);
                favNews.put("description", updatedNews.get(infoItemPos).description);
                favNews.put("imgLink", updatedNews.get(infoItemPos).imgLink);
                favNews.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "save FavNews done");
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseRelation<ParseObject> relation = user.getRelation("favNews");
                        relation.add(favNews);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.e(TAG, "user add FavNews done");
                                Toast.makeText(mContext, R.string.toast_news_marked, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case 1:
                // share to friends
                break;
            default:
                break;
        }

    }

    private class XmlDownloadTask extends AsyncTask<String, Void, List<RSSReader.Item>> {
        @Override
        protected List<RSSReader.Item> doInBackground(String... urls) {
            RSSReader reader = RSSReader.getInstance();
            return reader.start(urls[0]);
        }

        @Override
        protected void onPostExecute(List<RSSReader.Item> s) {
            progressDialog.dismiss();
            updatedNews = s;
            // set content to list view
            ListView newsListView;
            newsListView = (ListView)findViewById(R.id.lv_news);
            int[] readFlagsArray = new int[s.size()];
            for (int i = 0; i < s.size(); i++){
                readFlagsArray[i] = 0;
            }
            newsAdapter = new MyListViewAdapter(s, mContext, readFlagsArray);
            newsListView.setAdapter(newsAdapter);
            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    newsAdapter.setReadFlagArray(position); // set the clicked item as read
                    MyListViewAdapter.ViewHolder holder = (MyListViewAdapter.ViewHolder) view.getTag();
                    WebView myWebView = (WebView) findViewById(R.id.webview_news);
                    if (holder.link != null) {
                        myWebView.loadUrl(holder.link);
                    }
                }
            });

            newsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ItemLongClickDialogFragment itemLongClickDialogFragment = new ItemLongClickDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemPos", position);
                    bundle.putInt("itemsResId", R.array.news_activity_dialog_list);
                    itemLongClickDialogFragment.setArguments(bundle);
                    itemLongClickDialogFragment.show(getFragmentManager(), "ItemLongClickDialogFragment");
                    return true;
                }
            });

            // ImageLoader listener
            newsListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        }
    }

}
