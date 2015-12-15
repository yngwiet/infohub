package com.wc.kwinfo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.parse.ParseUser;
import com.robotium.solo.Solo;
import com.wc.kwinfo.login.LoginActivity;
import com.wc.kwinfo.news.NewsActivity;
import com.wc.kwinfo.news.NewsCategoryActivity;
import com.wc.kwinfo.news.NewsSettingsActivity;

import org.hamcrest.Matchers;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by wang on 2015/11/1.
 */
public class NewsCategoryTest extends ActivityInstrumentationTestCase2<LoginActivity>{

    private Solo solo;

    public NewsCategoryTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNewsCategoryWithNewsSetting(){
        /*if (AccessToken.getCurrentAccessToken() == null) {
            solo.assertCurrentActivity("Expected LoginActivity", LoginActivity.class);
            solo.clickOnText("skip");
        }*/
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            solo.assertCurrentActivity("Expected LoginActivity", LoginActivity.class);
            solo.clickOnEditText(0);
            solo.enterText(0, "max");
            solo.enterText(1, "zzzzzz");
            solo.clickOnButton("Login");
            solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        }
        solo.clickOnButton(0);
        solo.assertCurrentActivity("Expected NewsCategoryActivity", NewsCategoryActivity.class);
        solo.clickOnMenuItem(getActivity().getResources().getString(R.string.action_settings));
        solo.assertCurrentActivity("Expected NewsSettingsActivity", NewsSettingsActivity.class);
        solo.clickOnCheckBox(0);
        boolean isKWNewsChecked = solo.isCheckBoxChecked(0);
        solo.goBack();
        ArrayList<TextView> a = solo.clickInList(1);
        if (isKWNewsChecked){
            assertThat(a.get(0).getText(), Matchers.<CharSequence>is("K-W area"));
        }else{
            assertThat(a.get(0).getText(), Matchers.<CharSequence>is("Health"));
        }
        solo.assertCurrentActivity("Expected NewsActivity", NewsActivity.class);
    }
}
