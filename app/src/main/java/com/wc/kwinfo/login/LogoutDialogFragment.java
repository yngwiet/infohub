package com.wc.kwinfo.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.wc.kwinfo.R;

/**
 * Created by wang on 2015/11/21.
 */
public class LogoutDialogFragment extends DialogFragment {

    private static final String TAG = LogoutDialogFragment.class.getSimpleName();
    public interface OnLogoutClickListener{
        void onLogoutClicked();
    }
    private OnLogoutClickListener onLogoutClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_msg_logout);
        builder.setPositiveButton(R.string.dialog_btn_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onLogoutClickListener.onLogoutClicked();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onLogoutClickListener = (OnLogoutClickListener) activity;
    }
}
