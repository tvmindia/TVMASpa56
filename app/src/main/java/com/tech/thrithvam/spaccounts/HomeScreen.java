package com.tech.thrithvam.spaccounts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

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



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);

        // attaching data adapter to spinner
        Spinner chartType=(Spinner)findViewById(R.id.chart_type);
        chartType.setAdapter(dataAdapter);
    }
}
