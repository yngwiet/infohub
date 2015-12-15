package com.wc.kwinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.wc.kwinfo.account.MyAccountActivity;
import com.wc.kwinfo.events.EventsActivity;
import com.wc.kwinfo.login.LoginActivity;
import com.wc.kwinfo.news.NewsCategoryActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;
    private static final int TIME_INTERVAL = 2000;// time interval for click back button twice to exit
    private long mBackPressed;
    private Toast mExitToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mExitToast = new Toast(mContext);
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish){
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
            return;
        }

        InternetController.getInstance().setContext(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_account:
                // my account activity
                Intent intent1 = new Intent(this, MyAccountActivity.class);
                startActivity(intent1);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
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

    public void onNewsBtnClick(View view){
        Intent intent = new Intent(this, NewsCategoryActivity.class);
        startActivity(intent);
    }

    public void onEventsBtnClick(View view){
        if (AccessToken.getCurrentAccessToken() != null){
            if (!InternetController.getInstance().checkNetwork()){
                Toast.makeText(mContext, R.string.toast_network_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, R.string.toast_link_facebook, Toast.LENGTH_SHORT).show();
        }

    }


}
