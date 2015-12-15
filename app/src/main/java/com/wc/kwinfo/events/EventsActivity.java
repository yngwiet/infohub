package com.wc.kwinfo.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wc.kwinfo.InternetController;
import com.wc.kwinfo.ItemLongClickDialogFragment;
import com.wc.kwinfo.R;

public class EventsActivity extends AppCompatActivity
        implements EventListFragment.OnEventItemClickListener,
        EventListFragment.OnEventItemLongClickListener,
        ItemLongClickDialogFragment.OnDialogListItemClickListener{
    private static final String TAG = EventsActivity.class.getSimpleName();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Events");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = getApplicationContext();
        if (savedInstanceState == null){
            Fragment eventListFragment = new EventListFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, eventListFragment, "eventList").commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_events, menu);
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
                Fragment eventListFragment = new EventListFragment();
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, eventListFragment, "eventList").commit();
                return true;
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
    public void onClick(String description) {
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
    public void onDialogListItemClicked(int dialogItemPos, int infoItemPos) {
        switch (dialogItemPos){
            case 0: // mark as favourite
                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager().findFragmentByTag("eventList");
                if (eventListFragment != null){
                    eventListFragment.storeEvent(infoItemPos);
                }
                break;
            case 1: // share to friends
                break;
            case 2: // set a reminder
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogListItemClicked(int dialogItemPos, int infoItemPos, String objectId, String type) {
        // no need of this interface
    }

    @Override
    public void onLongClick(int itemPos) {
        ItemLongClickDialogFragment itemLongClickDialogFragment = new ItemLongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemPos", itemPos);
        bundle.putInt("itemsResId", R.array.eventsList_fragment_dialog_list);
        itemLongClickDialogFragment.setArguments(bundle);
        itemLongClickDialogFragment.show(getFragmentManager(), "ItemLongClickDialogFragment");
    }
}
