package com.wc.kwinfo;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by wang on 2015/11/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Parse SDK initialization; Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(getApplicationContext());
        // ImageLoader configuration
        ImageLoaderConfiguration imageLoaderConfiguration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }
}
