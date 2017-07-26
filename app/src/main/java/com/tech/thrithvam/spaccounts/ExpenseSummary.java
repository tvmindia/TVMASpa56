package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ExpenseSummary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        //Spinner
        List<String> categories = new ArrayList<String>();
        categories.add("One Month");
        categories.add("One Week");
        categories.add("One Day");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, categories);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        Spinner dataType=(Spinner)findViewById(R.id.type_spinner);
        dataType.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataType.getBackground().setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            dataType.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dataValuesLinear=(LinearLayout)findViewById(R.id.data_values_linear);
        for(int i=0;i<10;i++){
            View dataItem=inflater.inflate(R.layout.item_label_value,null);
            dataValuesLinear.addView(dataItem);
        }
    }
}
