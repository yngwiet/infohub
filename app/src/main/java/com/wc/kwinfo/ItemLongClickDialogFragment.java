package com.wc.kwinfo;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by wang on 2015/11/21.
 */
public class ItemLongClickDialogFragment extends DialogFragment {
    private static final String TAG = ItemLongClickDialogFragment.class.getSimpleName();
    public interface OnDialogListItemClickListener{
        void onDialogListItemClicked(int dialogItemPos, int infoItemPos);
        void onDialogListItemClicked(int dialogItemPos, int infoItemPos, String objectId, String type);
    }
    private OnDialogListItemClickListener onDialogListItemClickListener;
    private int infoItemPos;
    private String objectId = null;
    private String type;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        infoItemPos = bundle.getInt("itemPos");
        if (bundle.containsKey("objectId")){
            objectId = bundle.getString("objectId");
            type = bundle.getString("type");
        }
        Log.e(TAG, "infoItemPos: "+infoItemPos);
        int itemsResId = bundle.getInt("itemsResId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle(R.string.dialog_title);
        builder.setItems(itemsResId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (objectId != null){
                    onDialogListItemClickListener.onDialogListItemClicked(which, infoItemPos, objectId, type);
                }else {
                    onDialogListItemClickListener.onDialogListItemClicked(which, infoItemPos);
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onDialogListItemClickListener = (OnDialogListItemClickListener) activity;
    }
}
