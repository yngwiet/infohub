package com.wc.kwinfo.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.wc.kwinfo.R;

import java.util.ArrayList;

/**
 * Created by wang on 2015/11/19.
 */
public class AccountOverviewFragment extends Fragment{
    private static final String TAG = AccountOverviewFragment.class.getSimpleName();
    public interface OnAccountItemClickListener{
        void onAccountItemClicked(int id);
    }
    private OnAccountItemClickListener onAccountItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_account, container, false);
        TextView tv_username = (TextView) v.findViewById(R.id.tv_username);
        ListView lv_content = (ListView) v.findViewById(android.R.id.list);
        String username = "Hello! "+ ParseUser.getCurrentUser().getUsername();
        tv_username.setText(username);
        ArrayList<String> itemArrayList = initializeItemsArrayList();
        lv_content.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemArrayList));
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onAccountItemClickListener.onAccountItemClicked(position);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAccountItemClickListener = (OnAccountItemClickListener) context;
    }

    private ArrayList<String> initializeItemsArrayList(){
        ArrayList<String> itemArrayList = new ArrayList<>();
        itemArrayList.add("My favourite news");
        itemArrayList.add("My favourite events");
        return itemArrayList;
    }
}
