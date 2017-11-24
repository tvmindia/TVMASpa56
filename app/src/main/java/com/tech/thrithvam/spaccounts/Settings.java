package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch internalCompanySwitch=(Switch) findViewById(R.id.company_switch);

        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        String intComInc=sharedpreferences.getString(Common.INTERNALCOMPANY,"0");
        internalCompanySwitch.setChecked(intComInc.equals("1")?true:false);

        internalCompanySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editor.putString(Common.INTERNALCOMPANY, "1");
                    editor.apply();
                } else {
                    editor.putString(Common.INTERNALCOMPANY, "0");
                    editor.apply();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
