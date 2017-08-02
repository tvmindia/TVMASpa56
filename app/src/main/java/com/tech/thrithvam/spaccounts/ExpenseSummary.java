package com.tech.thrithvam.spaccounts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseSummary extends AppCompatActivity {
    static int DATES=123,DAYS=456;
    TextView startDate,endDate;
    static final String DAYS30="30 Days",DAYS60="60 Days",DAYS180="180 Days",DAYS365="365 Days";
    Spinner dataType;
     ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        startDate=(TextView)findViewById(R.id.start_date);
        endDate=(TextView)findViewById(R.id.end_date);

        //Spinner
        List<String> categories = new ArrayList<String>();
        categories.add(DAYS30);categories.add(DAYS60);categories.add(DAYS180);categories.add(DAYS365);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, categories);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        dataType=(Spinner)findViewById(R.id.days_spinner);
        dataType.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataType.getBackground().setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            dataType.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        dataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDataFromServer(DAYS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void getDates(View view){
        final TextView requiredDate=(TextView)view;
        final String oldText=requiredDate.getText().toString();
        final Calendar today = Calendar.getInstance();
        final Calendar selectedDate=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Setting display text-------
                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                requiredDate.setText(formatted.format(selectedDate.getTime()));
                //Validation--------------
                if(!startDate.getText().toString().equals(getResources().getString(R.string.start_date))
                        &&!endDate.getText().toString().equals(getResources().getString(R.string.end_date))){
                    try {
                        Date sDate=formatted.parse(startDate.getText().toString());
                        Date eDate=formatted.parse(endDate.getText().toString());
                        if(!sDate.before(eDate)){
                            Common.toastMessage(ExpenseSummary.this,R.string.give_valid);
                            requiredDate.setText(oldText);
                            return;
                        }
                    } catch (ParseException e) {
                        requiredDate.setText(oldText);
                        return;
                    }
                    getDataFromServer(DATES);
                }
            }
        };
        new DatePickerDialog(ExpenseSummary.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }
    void getDataFromServer(int datesOrDays){
        String postData="";
        if(datesOrDays==DATES){
            (findViewById(R.id.dates_layout)).setBackgroundResource(R.drawable.boarder_accent);
            (findViewById(R.id.days_layout)).setBackgroundResource(0);
            postData="{\"chartOfAccountsObj\":{\"startdate\":\""+startDate.getText().toString()+"\",\"enddate\":\""+endDate.getText().toString()+"\"}}}";
        }
        else if(datesOrDays==DAYS){
            (findViewById(R.id.days_layout)).setBackgroundResource(R.drawable.boarder_accent);
            (findViewById(R.id.dates_layout)).setBackgroundResource(0);
            int days=0;
            switch (dataType.getSelectedItem().toString()){
                case DAYS30:days=30;
                    break;
                case DAYS60:days=60;
                    break;
                case DAYS180:days=180;
                    break;
                case DAYS365:days=365;
                    break;
            }
            postData="{\"chartOfAccountsObj\":{\"days\":\""+days+"\"}}}";
        }
        final LinearLayout dataValuesLinear=(LinearLayout)findViewById(R.id.data_values_linear);
        dataValuesLinear.removeAllViews();
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Expense/GetExpenseDetailsForMobile";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray expensesDataJson = new JSONArray(common.json);
                    if(expensesDataJson.length()==0){
                        Common.toastMessage(ExpenseSummary.this,getResources().getString(R.string.no_items));
                        return;
                    }
                    ArrayList<String[]> expenseListData = new ArrayList<>();
                    for (int i = 0; i < expensesDataJson.length(); i++) {
                        JSONObject jsonObject1 = expensesDataJson.getJSONObject(i);
                        String[] data = new String[2];
                        data[0] = jsonObject1.getString("Amount");
                        data[1] = jsonObject1.getJSONObject("chartOfAccountsObj").getString("Type");
                        expenseListData.add(data);
                    }
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                    for(int i=0;i<expenseListData.size();i++){
                        View dataItem=inflater.inflate(R.layout.item_label_value,null);
                        ((TextView)dataItem.findViewById(R.id.value)).setText(expenseListData.get(i)[0].equals("null")?"-":getResources().getString(R.string.rupees,expenseListData.get(i)[0]));
                        ((TextView)dataItem.findViewById(R.id.label)).setText(expenseListData.get(i)[1].equals("null")?"-":expenseListData.get(i)[1]);
                        dataValuesLinear.addView(dataItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(ExpenseSummary.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(ExpenseSummary.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            Intent intent=new Intent(this,HomeScreen.class);
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
