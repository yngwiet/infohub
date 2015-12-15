package com.wc.kwinfo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.parse.ParseUser;
import com.robotium.solo.Solo;
import com.wc.kwinfo.account.MyAccountActivity;
import com.wc.kwinfo.events.EventsActivity;
import com.wc.kwinfo.login.LoginActivity;
import com.wc.kwinfo.news.NewsActivity;
import com.wc.kwinfo.news.NewsCategoryActivity;

import org.hamcrest.Matchers;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by wang on 2015/11/29.
 * This test class assumes that a Facebook user has already logged in.
 */
public class FavoriteInfoStoreDeleteTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    public FavoriteInfoStoreDeleteTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    private Solo solo;

    public void testStoreDeleteNews(){
        // click News
        solo.clickOnButton(0);
        // into NewsCategoryActivity
        solo.assertCurrentActivity("Expected NewsCategoryActivity", NewsCategoryActivity.class);
        // click K-W area category. Note: clickInList start from 1.
        solo.clickInList(1);
        // into NewsActivity
        solo.assertCurrentActivity("Expected NewsActivity", NewsActivity.class);
        // long click on first news
        ArrayList<TextView> expectedFavNews = solo.clickLongInList(0);
        // get first news's title
        String expectedFavNewsTitle = expectedFavNews.get(0).getText().toString();
        // click mark as favorite
        solo.clickInList(1);
        // back to NewsCategoryActivity
        solo.goBack();
        // back to MainActivity
        solo.goBack();
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        // click account menu item
        solo.clickOnView(solo.getView(R.id.action_account));
//        solo.clickOnActionBarItem(R.id.action_account); // doesn't work
        solo.assertCurrentActivity("Expected MyAccountActivity", MyAccountActivity.class);
        // click my favorite news
        solo.clickInList(1);
//        ArrayList<TextView> actualFavNews = solo.clickInList(0); // first fav news
        // wait for list view
        solo.waitForView(R.id.lv_news);
        // long click on first fav news
        ArrayList<TextView> actualFavNews = solo.clickLongInList(1);
        // get its title
        String actualFavNewsTitle = actualFavNews.get(0).getText().toString();
        // assert
        assertThat(actualFavNewsTitle, Matchers.is(expectedFavNewsTitle));
//        solo.clickLongInList(0);
        // click delete
        solo.clickInList(1);
        // wait for refresh
        solo.waitForView(R.id.lv_news);
        // long click the first fav news
        actualFavNews = solo.clickLongInList(1);
        // get its title
        actualFavNewsTitle = actualFavNews.get(0).getText().toString();
        // assert
        assertNotSame(expectedFavNewsTitle, actualFavNewsTitle);
    }

    public void testStoreDeleteEvents(){
        // click Events
        solo.clickOnButton(1);
        // into EventsActivity
        solo.assertCurrentActivity("Expected EventsActivity", EventsActivity.class);
        // wait for event list view
        solo.waitForView(android.R.id.list);
        // long click on first event
        ArrayList<TextView> expectedFavEvents = solo.clickLongInList(1);
        // get first event name
        String expectedFavEventName = expectedFavEvents.get(0).getText().toString();
        // click mark as favorite
        solo.clickInList(1);
        // go back to MainActivity
        solo.goBack();
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        // click on account menu item
        solo.clickOnView(solo.getView(R.id.action_account));
//        solo.clickOnActionBarItem(R.id.action_account); // doesn't work
        // into MyAccountActivity
        solo.assertCurrentActivity("Expected MyAccountActivity", MyAccountActivity.class);
        // click my favorite events
        solo.clickInList(2);
//        ArrayList<TextView> actualFavNews = solo.clickInList(0); // first fav news
        ArrayList<TextView> actualFavEvents = solo.clickLongInList(1);
        String actualFavEventsName = actualFavEvents.get(0).getText().toString();
        assertThat(actualFavEventsName, Matchers.is(expectedFavEventName));
//        solo.clickLongInList(0);
        solo.clickInList(1); // delete first fav news
        actualFavEvents = solo.clickInList(1);
        actualFavEventsName = actualFavEvents.get(0).getText().toString();
        assertNotSame(expectedFavEventName, actualFavEventsName);
    }
}
