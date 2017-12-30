package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    static final String YEAR="Last year",MONTH="Last month",SIXMONTH="Last six months";
    Spinner chartType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        FirebaseMessaging.getInstance().subscribeToTopic("common");

        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString("UserName","");
        ((TextView)findViewById(R.id.welcome)).setText(getResources().getString(R.string.welcome,userName));


        //chart Spinner
        List<String> chartTypeOptions = new ArrayList<String>();
        chartTypeOptions.add(MONTH);chartTypeOptions.add(SIXMONTH);chartTypeOptions.add(YEAR);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_small, chartTypeOptions);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        chartType=(Spinner)findViewById(R.id.chart_type);
        chartType.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chartType.getBackground().setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            chartType.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        chartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getChartData(){
        final LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setVisibility(View.GONE);

        String duration="";
        switch (chartType.getSelectedItem().toString()){
            case YEAR:duration="Year";
                break;
            case MONTH:duration="1Month";
                break;
            case SIXMONTH:duration="6month";
                break;
        }
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/HomeScreenChart/SalesSummaryChartForMobile";
        String postData = "{\"duration\":\""+duration+"\",\"IsInternalComp\":"+Common.getInternalCompanySettings(HomeScreen.this)+"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"Period",//0
                "Amount",//1
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                if(common.dataArrayList.size()<=1){
                    return;
                }
                try {
                    chart.setVisibility(View.VISIBLE);
                    List<Entry> entries = new ArrayList<Entry>();
                    final HashMap<Integer, String> numMap = new HashMap<>();
                    for (int i = 0; i < common.dataArrayList.size(); i++) {
                        entries.add(new Entry(i, Float.parseFloat(common.dataArrayList.get(i)[1])));
                        numMap.put(i, common.dataArrayList.get(i)[0]);
                    }
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return numMap.get((int) value);
                        }
                    });
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    YAxis rightAxis = chart.getAxisRight();
                    rightAxis.setEnabled(false);

                    LineDataSet dataSet = new LineDataSet(entries, "Sales");
                    dataSet.setColor(Color.BLUE);
                    dataSet.setValueTextColor(Color.parseColor("#290000"));
                    dataSet.setValueTextSize(11);
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    // hide legend
                    Legend legend = chart.getLegend();
                    legend.setEnabled(false);
                    chart.getDescription().setEnabled(false);

                    chart.invalidate(); // refresh
                    chart.animateX(3000, Easing.EasingOption.Linear);
                }
                catch (Exception e){
                    Toast.makeText(HomeScreen.this, "Error in drawing chart", Toast.LENGTH_SHORT).show();
                    chart.setVisibility(View.INVISIBLE);
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(HomeScreen.this, R.string.failed_server);
                (findViewById(R.id.chart_card)).setVisibility(View.GONE);
            }
        };

        common.AsynchronousThread(HomeScreen.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
    public void SuppliersClick(View view){
        Intent intent=new Intent(this,Suppliers.class);
        startActivity(intent);
    }
    public void ApprovalClick(View view){
        Intent intent=new Intent(this,Approvals.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent intent=new Intent(this,Settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}
