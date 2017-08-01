package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString("UserName","");
        ((TextView)findViewById(R.id.welcome)).setText(getResources().getString(R.string.welcome,userName));

        LineChart chart = (LineChart) findViewById(R.id.chart);



        List<Entry> entries = new ArrayList<Entry>();


        // turn your data into Entry objects
        entries.add(new Entry(5,4));
        entries.add(new Entry(6,2));
        entries.add(new Entry(7,7));
        entries.add(new Entry(8,3));
        entries.add(new Entry(9,6));
        entries.add(new Entry(10,5));

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.BLUE);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        chart.animateX(3000, Easing.EasingOption.Linear);


        //chart Spinner
        List<String> categories = new ArrayList<String>();
        categories.add("Monthly Sale");
        categories.add("Weekly Sale");
        categories.add("Years Sale");
        categories.add("Daily Sale");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, categories);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        Spinner chartType=(Spinner)findViewById(R.id.chart_type);
        chartType.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chartType.getBackground().setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            chartType.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void InvoiceSalesClick(View view){
        Intent intent=new Intent(this,InvoiceSummary.class);
        intent.putExtra("salesORpurchase",Common.SALES);
        startActivity(intent);
    }
    public void InvoicePurchaseClick(View view){
        Intent intent=new Intent(this,InvoiceSummary.class);
        intent.putExtra("salesORpurchase",Common.PURCHASE);
        startActivity(intent);
    }
    public void ExpenseClick(View view){
        Intent intent=new Intent(this,ExpenseSummary.class);
        startActivity(intent);
    }
    public void CustomersClick(View view){
        Intent intent=new Intent(this,Customers.class);
        startActivity(intent);
    }

}
