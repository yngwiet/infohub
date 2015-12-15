package com.wc.kwinfo.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wc.kwinfo.R;


public class NewsSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_settings);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final CheckBox kwNews = (CheckBox) findViewById(R.id.checkBoxKW);
        final CheckBox healthNews = (CheckBox) findViewById(R.id.checkBoxHealth);
        final CheckBox sciTechNews = (CheckBox) findViewById(R.id.checkBoxSciTech);
        final CheckBox businessNews = (CheckBox) findViewById(R.id.checkBoxBusiness);
        SharedPreferences sharedPreferences = getSharedPreferences("news_setting", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isKwNews = sharedPreferences.getBoolean("kwNews", true);
        Boolean isHealthNews = sharedPreferences.getBoolean("healthNews", true);
        Boolean isSciTechNews = sharedPreferences.getBoolean("sciTechNews", true);
        Boolean isBusinessNews = sharedPreferences.getBoolean("businessNews", true);
        if (isKwNews){
            kwNews.setChecked(true);
        } else{
            kwNews.setChecked(false);
        }
        if (isHealthNews){
            healthNews.setChecked(true);
        } else{
            healthNews.setChecked(false);
        }
        if (isSciTechNews){
            sciTechNews.setChecked(true);
        } else{
            sciTechNews.setChecked(false);
        }
        if (isBusinessNews){
            businessNews.setChecked(true);
        } else{
            businessNews.setChecked(false);
        }
        kwNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (kwNews.isChecked()){
                    editor.putBoolean("kwNews", true);
                } else{
                    editor.putBoolean("kwNews", false);
                }
                editor.commit();
            }
        });

        healthNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (healthNews.isChecked()){
                    editor.putBoolean("healthNews", true);
                } else{
                    editor.putBoolean("healthNews", false);
                }
                editor.commit();
            }
        });

        sciTechNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sciTechNews.isChecked()){
                    editor.putBoolean("sciTechNews", true);
                } else{
                    editor.putBoolean("sciTechNews", false);
                }
                editor.commit();
            }
        });

        businessNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (businessNews.isChecked()){
                    editor.putBoolean("businessNews", true);
                } else{
                    editor.putBoolean("businessNews", false);
                }
                editor.commit();
            }
        });
    }

    //
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

}
