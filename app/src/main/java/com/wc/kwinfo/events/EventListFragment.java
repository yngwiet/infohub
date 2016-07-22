package com.wc.kwinfo.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
// import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wc.kwinfo.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wang on 2015/10/24.
 */
public class EventListFragment extends Fragment{

    private static final String TAG = EventListFragment.class.getSimpleName();
    private View fragmentView = null;
    private Context mContext;
    private ProgressDialog progressDialog;
    private ListView lv_events;
    private EventListAdapter eventListAdapter;
    private EditText et_searchKey;
    private ArrayList<EventItem> eventItemArrayList = null;
    private OnEventItemClickListener onEventItemClickListener;
    private OnEventItemLongClickListener onEventItemLongClickListener;
    private SharedPreferences.Editor editor;

    public interface OnEventItemClickListener {
        void onClick(String description);
    }

    public interface OnEventItemLongClickListener{
        void onLongClick(int itemPos);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        mContext = getContext();
        if (fragmentView == null){
            fragmentView = inflater.inflate(R.layout.fragment_event_list, container, false);
            lv_events = (ListView) fragmentView.findViewById(android.R.id.list);
            // Button btn_search = (Button) fragmentView.findViewById(R.id.btn_search);
            et_searchKey = (EditText) fragmentView.findViewById(R.id.et_search_key);
            et_searchKey.setOnEditorActionListener(editTextSearchKeyListener);
            // btn_search.setOnClickListener(btn_searchClickListener);
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("EventSearchKey", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String searchKey = sharedPreferences.getString("searchKey", "");

        if (eventItemArrayList != null) {
            // this means the fragment is popped up from back stack.
            // do nothing
        }
        else if (!searchKey.isEmpty()){
            et_searchKey.setText(searchKey);

            Bundle parameters = buildParameters(searchKey);
            requestFacebookEvents(parameters);
        }

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onEventItemClickListener = (OnEventItemClickListener) context;
        onEventItemLongClickListener = (OnEventItemLongClickListener) context;
    }

    public void storeEvent(int eventPos){
        final ParseObject favEvent = new ParseObject("FavEvents");
        favEvent.put("id", eventItemArrayList.get(eventPos).id);
        favEvent.put("name", eventItemArrayList.get(eventPos).name);
        favEvent.put("location", eventItemArrayList.get(eventPos).location);
        favEvent.put("description", eventItemArrayList.get(eventPos).description);
        favEvent.put("coverLink", eventItemArrayList.get(eventPos).coverLink);
        favEvent.put("startTime", eventItemArrayList.get(eventPos).startTime);
        favEvent.put("endTime", eventItemArrayList.get(eventPos).endTime);
        favEvent.put("attendingCount", eventItemArrayList.get(eventPos).attendingCount);
        favEvent.put("maybeCount", eventItemArrayList.get(eventPos).maybeCount);
        favEvent.put("declinedCount", eventItemArrayList.get(eventPos).declinedCount);
        favEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "save FavEvents done");
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("favEvents");
                relation.add(favEvent);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "user add FavEvents done");
                        Toast.makeText(mContext, R.string.toast_events_marked, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*View.OnClickListener btn_searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchKey = et_searchKey.getText().toString();
            if (!searchKey.isEmpty()){
                editor.putString("searchKey", searchKey);
                editor.apply();
                Bundle parameters = buildParameters(searchKey);
                requestFacebookEvents(parameters);
            } else {
                Toast.makeText(mContext, R.string.toast_search_key_alert, Toast.LENGTH_SHORT).show();
            }
        }
    };*/

    /**
     * listen EditText search action
     */
    TextView.OnEditorActionListener editTextSearchKeyListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchKey = et_searchKey.getText().toString();
                if (!searchKey.isEmpty()){
                    editor.putString("searchKey", searchKey);
                    editor.apply();
                    Bundle parameters = buildParameters(searchKey);
                    requestFacebookEvents(parameters);
                } else {
                    Toast.makeText(mContext, R.string.toast_search_key_alert, Toast.LENGTH_SHORT).show();
                }
                handled = true;
            }

            return handled;
        }
    };

    private Bundle buildParameters(String searchKey){
        Bundle parameters = new Bundle();
        parameters.putString("q", searchKey);
        parameters.putString("type","event");
        parameters.putString("fields", "name,place,start_time,end_time,description,cover,"
                + "attending_count,maybe_count,declined_count");
        parameters.putString("limit", "5000");
        parameters.putString("method", "GET");
        return parameters;
    }

    private void requestFacebookEvents(Bundle parameters){

        progressDialog = ProgressDialog.show(mContext, null, "Loading...", true, false);

        GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/search",
                parameters,
                HttpMethod.POST,
                new GraphRequest.Callback(){
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (progressDialog != null){
                            progressDialog.dismiss();
                        }
                        if (response != null){
                            try {
                                JSONObject jsonObj = response.getJSONObject(); // ??? sometimes NullPointer Exception
                                Log.e(TAG, "json object str: " + jsonObj.toString());
                                EventParser eventParser = EventParser.getInstance();
                                eventItemArrayList = eventParser.parseJsonObject(jsonObj);

                                int[] readFlagsArray = new int[eventItemArrayList.size()];
                                for (int i = 0; i < eventItemArrayList.size(); i++){
                                    readFlagsArray[i] = 0;
                                }
                                eventListAdapter = new EventListAdapter
                                        (eventItemArrayList, mContext, readFlagsArray);
                                lv_events.setAdapter(eventListAdapter);

                                hideKeyboard();

                                lv_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        view.setSelected(true);
                                        eventListAdapter.setReadFlagArray(position);
                                        String description = eventItemArrayList.get(position).description;
                                        onEventItemClickListener.onClick(description);
                                    }
                                });

                                lv_events.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        onEventItemLongClickListener.onLongClick(position);
                                        return true;
                                    }
                                });

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
