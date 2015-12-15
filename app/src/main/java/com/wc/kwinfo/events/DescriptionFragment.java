package com.wc.kwinfo.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wc.kwinfo.R;

/**
 * Created by wang on 2015/10/27.
 */
public class DescriptionFragment extends Fragment {

    private static final String TAG = DescriptionFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView fragmentView = (TextView) inflater.inflate(R.layout.fragment_event_description, container, false);
        Bundle bundle = getArguments();
        String description = bundle.getString("description");
        fragmentView.setText(description);
        fragmentView.setMovementMethod(new ScrollingMovementMethod());

        return fragmentView;
    }
}
