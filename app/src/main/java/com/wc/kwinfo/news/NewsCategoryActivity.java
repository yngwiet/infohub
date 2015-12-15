package com.wc.kwinfo.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.wc.kwinfo.InternetController;
import com.wc.kwinfo.R;

import java.util.ArrayList;

public class NewsCategoryActivity extends AppCompatActivity {
    private static final String TAG = NewsCategoryActivity.class.getSimpleName();
    private ArrayList<MyGridViewAdapter.GridItem> gridData = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_category);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_news_category_settings:
                Intent intent = new Intent(this, NewsSettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gridData.clear();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e(TAG, "onResume");
        getGridView();
    }

    private void getGridView(){
        SharedPreferences sharedPreferences = getSharedPreferences("news_setting", Context.MODE_PRIVATE);
        Boolean isKwNews = sharedPreferences.getBoolean("kwNews", true);
        Boolean isHealthNews = sharedPreferences.getBoolean("healthNews", true);
        Boolean isSciTechNews = sharedPreferences.getBoolean("sciTechNews", true);
        Boolean isBusinessNews = sharedPreferences.getBoolean("businessNews", true);
        if (isKwNews){
            gridData.add(new MyGridViewAdapter.GridItem("K-W area", R.mipmap.kw_category));
        }
        if (isHealthNews){
            gridData.add(new MyGridViewAdapter.GridItem("Health", R.mipmap.health_category));
        }
        if (isSciTechNews){
            gridData.add(new MyGridViewAdapter.GridItem("Sci-Tech", R.mipmap.sci_tech_category));
        }
        if (isBusinessNews){
            gridData.add(new MyGridViewAdapter.GridItem("Business", R.mipmap.business_category));
        }

        GridView newsCategory = (GridView) findViewById(R.id.gridView_news_category);
        newsCategory.setAdapter(new MyGridViewAdapter(gridData, getApplicationContext()));

        newsCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!InternetController.getInstance().checkNetwork()){
                    Toast.makeText(mContext, R.string.toast_network_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                MyGridViewAdapter.ViewHolder holder = (MyGridViewAdapter.ViewHolder)view.getTag();
                switch(holder.categoryName.getText().toString()){
                    case "K-W area":
                        Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                        intent.putExtra("info",new String[]{"K-W News","http://www.cbc.ca/cmlink/rss-canada-kitchenerwaterloo"});
                        startActivity(intent);
                        break;
                    case "Health":
                        Intent intent1 = new Intent(getApplicationContext(), NewsActivity.class);
                        intent1.putExtra("info",new String[]{"Health News","http://www.cbc.ca/cmlink/rss-health"});
                        startActivity(intent1);
                        break;
                    case "Sci-Tech":
                        Intent intent2 = new Intent(getApplicationContext(), NewsActivity.class);
                        intent2.putExtra("info",new String[]{"Sci&Tech News","http://www.cbc.ca/cmlink/rss-technology"});
                        startActivity(intent2);
                        break;
                    case "Business":
                        Intent intent3 = new Intent(getApplicationContext(), NewsActivity.class);
                        intent3.putExtra("info",new String[]{"Business News","http://www.cbc.ca/cmlink/rss-business"});
                        startActivity(intent3);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
