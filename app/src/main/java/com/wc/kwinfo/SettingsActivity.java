package com.wc.kwinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wc.kwinfo.login.LogoutDialogFragment;

public class SettingsActivity extends AppCompatActivity
        implements LogoutDialogFragment.OnLogoutClickListener{
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private Context mContext;
    private Button loginButton;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        final Switch switchWifi = (Switch) findViewById(R.id.switch_wifi);
        SharedPreferences sharedPreferences = getSharedPreferences("main_settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isWifiOnly = sharedPreferences.getBoolean("wifiOnly", false);
        if (isWifiOnly){
            switchWifi.setChecked(true);
        } else {
            switchWifi.setChecked(false);
        }
        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchWifi.isChecked()){
                    editor.putBoolean("wifiOnly", true);
                } else {
                    editor.putBoolean("wifiOnly", false);
                }
                editor.apply();
            }
        });

        mContext = getApplicationContext();
        loginButton = (Button) findViewById(R.id.button_facebook_login);
        if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(
                            ParseUser.getCurrentUser(), SettingsActivity.this, null, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
                                Toast.makeText(mContext, R.string.toast_link_facebook_success, Toast.LENGTH_SHORT).show();
                                loginButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(mContext, R.string.toast_link_facebook_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            loginButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    public void onLogoutClicked(View v){
        // logout
        LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();
        logoutDialogFragment.show(getFragmentManager(), "LogoutDialogFragment");
    }

    @Override
    public void onLogoutClicked() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "logout done");
            }
        });
//        ParseUser.logOut();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("finish", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
