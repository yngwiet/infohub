package com.wc.kwinfo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.wc.kwinfo.MainActivity;
import com.wc.kwinfo.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wang on 2015/11/16.
 */
public class LoginFragment extends LoginSignUpFragmentBase{
    public interface LoginFragmentListener{
        void onSignUpClicked(String username, String password);
    }
    private static final String TAG = LoginFragment.class.getSimpleName();
    private LoginFragmentListener loginFragmentListener;
    private EditText usernameField;
    private EditText passwordField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginButton = (Button) v.findViewById(R.id.btn_login);
        Button signupButton = (Button) v.findViewById(R.id.btn_signup);
        usernameField = (EditText) v.findViewById(R.id.et_login_username);
        passwordField = (EditText) v.findViewById(R.id.et_login_password);

        // determine if the user already logged in here.

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                if (username.length() == 0) {
                    showToast("Please enter your user name");
                } else if (password.length() == 0) {
                    showToast("Please enter your password");
                } else {
                    // login
                    onLoadingListener.onLoadingStart();
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            onLoadingListener.onLoadingFinish();
                            if (user != null){
                                onLoginSuccess.onLoginSuccess(R.string.toast_login_success);
                            } else {
                                if (e != null){
                                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                                        showToast("Login failed, wrong username or password");
                                        passwordField.selectAll();
                                        passwordField.requestFocus();
                                    } else {
                                        showToast("Could not login, please try again later");
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                loginFragmentListener.onSignUpClicked(username, password);
            }
        });

        Button facebookLoginButton = (Button) v.findViewById(R.id.button_facebook_login);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), null, new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.e(TAG, "facebook login user null");
                            showToast(getResources().getString(R.string.toast_login_fail));
                        } else if (user.isNew()) {
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name");
                            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", parameters,
                                    HttpMethod.GET, new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    String username = "Facebook User";
                                    if (response != null){
                                        JSONObject jsonObj = response.getJSONObject();
                                        try {
                                            username = jsonObj.getString("name");
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    user.setUsername(username);
                                    try {
                                        user.save();// remember to save after change
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    onLoginSuccess.onLoginSuccess(R.string.toast_sign_up_login_success);
                                }
                            }).executeAsync();
                        } else {
                            onLoginSuccess.onLoginSuccess(R.string.toast_login_success);
                        }
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginFragmentListener = (LoginFragmentListener) context;
        onLoadingListener = (OnLoadingListener) context;
        onLoginSuccess = (OnLoginSuccess) context;
    }

}
