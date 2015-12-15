package com.wc.kwinfo;

import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;
import com.robotium.solo.Solo;
import com.wc.kwinfo.login.LoginActivity;

/**
 * Created by wang on 2015/11/29.
 */
public class SignUpLoginTest extends ActivityInstrumentationTestCase2<LoginActivity>{

    public SignUpLoginTest() {
        super(LoginActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testSignUpLogin(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){ // need to logout first
            solo.clickOnMenuItem(getActivity().getResources().getString(R.string.action_settings));
            solo.assertCurrentActivity("Expected SettingsActivity", SettingsActivity.class);
            solo.clickOnButton("Logout");
            solo.clickOnView(solo.getView(android.R.id.button1));
        }
        solo.assertCurrentActivity("Expected LoginActivity", LoginActivity.class);
        solo.clickOnButton("Sign Up");
        solo.enterText(0, "max1");
        solo.enterText(1, "111111");
        solo.enterText(2, "111111");
        solo.enterText(3, "yngwiewxh@gmail.com");
        solo.clickOnButton("Create Account");
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
    }
}
