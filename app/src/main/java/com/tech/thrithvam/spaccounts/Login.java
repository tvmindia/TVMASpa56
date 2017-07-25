package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.dd.CircularProgressButton;

public class Login extends AppCompatActivity {
    CircularProgressButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //delete this
        startActivity(new Intent(this,HomeScreen.class));

        loginButton=(CircularProgressButton)findViewById(R.id.btnWithText);
        final EditText emailInput=(EditText)findViewById(R.id.input_email);
        final EditText passwordInput=(EditText)findViewById(R.id.input_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput.clearFocus();
                passwordInput.clearFocus();
                emailInput.setEnabled(false);
                passwordInput.setEnabled(false);
                loginButton.setClickable(false);
                //Loading
                loginButton.setIndeterminateProgressMode(true);
                loginButton.setProgress(50);
                final Handler handler = new Handler();
                if(emailInput.getText().toString()
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
                            emailInput.setEnabled(true);
                            passwordInput.setEnabled(true);
                            loginButton.setClickable(true);
                            emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                }
            }
        });
    }
}
