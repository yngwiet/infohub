package com.wc.kwinfo.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wc.kwinfo.R;
import com.wc.kwinfo.ItemLongClickDialogFragment;
import com.wc.kwinfo.events.DescriptionFragment;

/**
 * Created by wang on 2015/11/19.
 */
public class MyAccountActivity extends AppCompatActivity
        implements AccountOverviewFragment.OnAccountItemClickListener,
        FavInfoListFragment.OnFavNewsItemLongClickListener,
        ItemLongClickDialogFragment.OnDialogListItemClickListener,
        FavInfoListFragment.onFavEventItemClickListener,
        FavInfoListFragment.onFavEventItemLongClickListener{

    private final String TAG = MyAccountActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null){
            Fragment accountOverviewFragment = new AccountOverviewFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, accountOverviewFragment, "overview").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!getSupportFragmentManager().popBackStackImmediate()) {
                    finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onAccountItemClicked(int id) {
        FavInfoListFragment favInfoListFragment = new FavInfoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        favInfoListFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, favInfoListFragment, "favItemList");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDialogListItemClicked(int itemPos, int newsPos) {
        // no need to implement this interface in this activity
    }

    @Override
    public void onDialogListItemClicked(int dialogItemPos, final int infoItemPos, String objectId, final String type) {
        switch (dialogItemPos){
            case 0: // delete
                ParseUser user = ParseUser.getCurrentUser();
                final ParseRelation<ParseObject> relation = user.getRelation(type);
                ParseObject favItem = null;
                if (type.equals("favNews")){
                    favItem = ParseObject.createWithoutData("FavNews", objectId);
                } else if (type.equals("favEvents")){
                    favItem = ParseObject.createWithoutData("FavEvents", objectId);
                }
                relation.remove(favItem);
                progressDialog = ProgressDialog.show(MyAccountActivity.this, null, "Loading...", true, true);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.e(TAG, "delete and save success");
                            FavInfoListFragment favInfoListFragment = (FavInfoListFragment) getSupportFragmentManager()
                                    .findFragmentByTag("favItemList");
                            if (type.equals("favNews")){
                                favInfoListFragment.refreshNewsAdapter(infoItemPos);
                            }else if (type.equals("favEvents")){
                                favInfoListFragment.refreshEventsAdapter(infoItemPos);
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onFavNewsItemLongClicked(int position, String objectId, String type) {
        ItemLongClickDialogFragment itemLongClickDialogFragment = new ItemLongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemPos", position);
        bundle.putString("objectId", objectId);
        bundle.putString("type", type);
        bundle.putInt("itemsResId", R.array.fav_news_dialog_list);
        itemLongClickDialogFragment.setArguments(bundle);
        itemLongClickDialogFragment.show(getFragmentManager(), "ItemLongClickDialogFragment");
    }

    @Override
    public void onFavEventItemClicked(String description) {
        DescriptionFragment desFragment = new DescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("description", description);
        desFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, desFragment, "eventDescription");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFavEventItemLongClickListener(int position, String objectId, String type) {
        ItemLongClickDialogFragment itemLongClickDialogFragment = new ItemLongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemPos", position);
        bundle.putString("objectId", objectId);
        bundle.putString("type", type);
        bundle.putInt("itemsResId", R.array.fav_events_dialog_list);
        itemLongClickDialogFragment.setArguments(bundle);
        itemLongClickDialogFragment.show(getFragmentManager(), "ItemLongClickDialogFragment");
    }
}
