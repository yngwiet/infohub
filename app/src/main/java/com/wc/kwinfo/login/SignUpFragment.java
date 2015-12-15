package com.wc.kwinfo.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.wc.kwinfo.R;

/**
 * Created by wang on 2015/11/16.
 */
public class SignUpFragment extends LoginSignUpFragmentBase {
    private static final String TAG = SignUpFragment.class.getSimpleName();
    private EditText usernameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText emailField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Bundle bundle = getArguments();
        String username = bundle.getString("username");
        String password = bundle.getString("password");
        usernameField = (EditText) v.findViewById(R.id.et_sign_up_username);
        usernameField.setText(username);
        passwordField = (EditText) v.findViewById(R.id.et_sign_up_password);
        passwordField.setText(password);
        confirmPasswordField = (EditText) v.findViewById(R.id.et_sign_up_confirm_password);
        emailField = (EditText) v.findViewById(R.id.et_sign_up_email);
        Button createAccountButton = (Button) v.findViewById(R.id.btn_create_account);
        createAccountButton.setOnClickListener(createAccListener);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onLoadingListener = (OnLoadingListener) context;
        onLoginSuccess = (OnLoginSuccess) context;
    }

    View.OnClickListener createAccListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String passwordAgain = confirmPasswordField.getText().toString();
            String email = emailField.getText().toString();
            if (username.length() == 0) {
                showToast("Please enter your username");
            } else if (password.length() == 0) {
                showToast("Please enter your password");
            } else if (password.length() < 6) {
                showToast("Password must be at least 6 characters long");
            } else if (passwordAgain.length() == 0) {
                showToast("Please re-enter your password");
            } else if (!password.equals(passwordAgain)) {
                showToast("Password don't match, please try again");
                confirmPasswordField.selectAll();
                confirmPasswordField.requestFocus();
            } else if (email.length() == 0) {
                showToast("Please enter your email");
            } else {
                // user sign up
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                Log.e(TAG, "sign up: username: " + username + ", email: "+email);
                onLoadingListener.onLoadingStart();
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){ // sign up success
                            onLoadingListener.onLoadingFinish();
                            onLoginSuccess.onLoginSuccess(R.string.toast_sign_up_login_success);
                        } else { // something is wrong
                            onLoadingListener.onLoadingFinish();
                            switch (e.getCode()) {
                                case ParseException.INVALID_EMAIL_ADDRESS:
                                    showToast("Email is invalid, please correct it and try again");
                                    break;
                                case ParseException.USERNAME_TAKEN:
                                    showToast("Username is already taken, please choose a different one");
                                    break;
                                case ParseException.EMAIL_TAKEN:
                                    showToast("Email is already taken, please choose another one");
                                    break;
                                default:
                                    showToast("Could not sign up, please try again later"+e.getCode());
                                    break;
                            }
                        }
                    }
                });

            }

        }
    };

}
