package com.wc.kwinfo.login;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by wang on 2015/11/16.
 */
public class LoginSignUpFragmentBase extends Fragment {
    public interface OnLoadingListener{
        void onLoadingStart();
        void onLoadingFinish();
    }
    public interface OnLoginSuccess{
        void onLoginSuccess(int resourceId);
    }
    protected void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
    protected OnLoadingListener onLoadingListener;
    protected OnLoginSuccess onLoginSuccess;

}
