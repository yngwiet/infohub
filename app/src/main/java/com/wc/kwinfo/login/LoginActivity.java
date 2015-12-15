package com.wc.kwinfo.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.wc.kwinfo.MainActivity;
import com.wc.kwinfo.R;

public class LoginActivity extends FragmentActivity implements
        LoginFragment.LoginFragmentListener, LoginSignUpFragmentBase.OnLoadingListener,
        LoginSignUpFragmentBase.OnLoginSuccess{
    private final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private static final int TIME_INTERVAL = 2000;// time interval for click back button twice to exit
    private long mBackPressed;
    private Toast mExitToast;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();

        /*if (AccessToken.getCurrentAccessToken() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mExitToast = new Toast(mContext);

        if (savedInstanceState == null){
            Fragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(!getSupportFragmentManager().popBackStackImmediate()){
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                mExitToast.cancel();
                finish();
                return;
            }
            else {
                mExitToast.makeText(mContext, "click again to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    public void onSignUpClicked(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onLoginSuccess(int resourceId) {
        Toast.makeText(mContext, resourceId, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoadingStart() {
        progressDialog = ProgressDialog.show(this, null, "Loading...", true, false);
    }

    @Override
    public void onLoadingFinish() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
