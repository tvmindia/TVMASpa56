package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.dd.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    CircularProgressButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startActivity(new Intent(this,HomeScreen.class));

        loginButton=(CircularProgressButton)findViewById(R.id.btnWithText);
        final EditText usernameInput=(EditText)findViewById(R.id.input_username);
        final EditText passwordInput=(EditText)findViewById(R.id.input_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput.clearFocus();
                passwordInput.clearFocus();
                usernameInput.setEnabled(false);
                passwordInput.setEnabled(false);
                loginButton.setClickable(false);
                //Loading
                loginButton.setIndeterminateProgressMode(true);
                loginButton.setProgress(50);

                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="/API/Login/HomeLogin";
                String postData =  "{\"LoginName\":\"" + usernameInput.getText().toString() + "\",\"Password\":\""+ passwordInput.getText().toString()+ "\"}";
//                AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setProgress(100);

                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(common.json);
                            String userName=jsonObject.optString("UserName");

                            //Login success
                            final Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Login.this, HomeScreen.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }, 2000);

                            //Storing for session
                            SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("UserName", userName);
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Runnable postThreadFailed=new Runnable() {
                    @Override
                    public void run() {
                        Common.toastMessage(Login.this,common.msg);
                        Common.toastMessage(Login.this, R.string.failed_try_again);

                        loginButton.setProgress(-1);
                        //Setting button to refresh when password/email change
                        usernameInput.setEnabled(true);
                        passwordInput.setEnabled(true);
                        loginButton.setClickable(true);
                        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.setProgress(0);
                            }
                        });
                        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.setProgress(0);
                            }
                        });

                    }};

                common.AsynchronousThread(Login.this,
                        webService,
                        postData,
                        null,
                        dataColumns,
                        postThread,
                        postThreadFailed);
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

/*
                final Handler handler = new Handler();
                if(usernameInput.getText().toString()
                        .equals(passwordInput.getText().toString())){
                    //Login success
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setProgress(100);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                Intent intent=new Intent (Login.this,HomeScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);       }
                            }, 1000);
                        }
                    }, 3000);
                }
                else {
                    //Login Failed
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginButton.setProgress(-1);
                            //Setting button to refresh when password/email change
                            usernameInput.setEnabled(true);
                            passwordInput.setEnabled(true);
                            loginButton.setClickable(true);
                            usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                        loginButton.setProgress(0);
                                }
                            });
                            passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                        loginButton.setProgress(0);
                                }
                            });
                        }
                    }, 3000);
                }*/
            }
        });
    }
}
