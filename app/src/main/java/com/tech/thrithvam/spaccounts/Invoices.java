package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Invoices extends AppCompatActivity {

    static ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        getSupportActionBar().setTitle("Invoices: "+getIntent().getExtras().getString(Common.NAME));
        getInvoices();
    }
    void getInvoices(){
        final ListView invoiceList=(ListView)findViewById(R.id.invoice_list);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService="";
        String postData="";
        switch (getIntent().getExtras().getInt(Common.CUSTOMER_OR_SUPPLIER)){
            case Common.CUSTOMER:webService="API/InvoiceSummary/GetOutstandingInvoicesForMobile";
                postData = "{\"customerObj\":{\"ID\":\""+getIntent().getExtras().getString(Common.CUSTOMERID)+"\"}}";
                break;
            case Common.SUPPLIER:webService="API/PurchaseSummary/GetSupplierOutstandingInvoicesForMobile";
                postData = "{\"suppliersObj\":{\"ID\":\""+getIntent().getExtras().getString(Common.SUPPLIERID)+"\"}}";
                break;
        }
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                try {
                    jsonObject=new JSONObject(common.json);
                    JSONArray invoices = jsonObject.getJSONArray("OutstandingList");
                    if(invoices.length()==0){
                        Common.toastMessage(Invoices.this,getResources().getString(R.string.no_items));
                        (findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                        return;
                    }
                    ArrayList<String[]> invoiceListData = new ArrayList<>();
                    for (int i = 0; i < invoices.length(); i++) {
                        JSONObject jsonObject1 = invoices.getJSONObject(i);
                        String[] data = new String[9];
                        data[0] = jsonObject1.getString("ID");
                        data[1] = jsonObject1.getString("InvoiceNo");
                        switch (getIntent().getExtras().getInt(Common.CUSTOMER_OR_SUPPLIER)){
                            case Common.CUSTOMER:
                                data[2] = jsonObject1.getJSONObject("customerObj").getString("ID");
                                data[3] = jsonObject1.getJSONObject("customerObj").getString("ContactPerson");
                                break;
                            case Common.SUPPLIER:
                                data[2] = jsonObject1.getJSONObject("suppliersObj").getString("ID");
                                data[3] = jsonObject1.getJSONObject("suppliersObj").getString("ContactPerson");
                                break;
                        }
                        data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                        data[5] = jsonObject1.getString("BalanceDue");
                        data[6] = jsonObject1.getString("PaidAmount");
                        data[8] = jsonObject1.getString("DueDays");
                        invoiceListData.add(data);
                    }
                    CustomAdapter adapter = new CustomAdapter(Invoices.this, invoiceListData, (getIntent().getExtras().getInt(Common.CUSTOMER_OR_SUPPLIER)==Common.CUSTOMER?Common.SALESLIST:Common.PURCHASELIST));
                    invoiceList.setAdapter(adapter);
                    (findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(Invoices.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(Invoices.this,
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
        getMenuInflater().inflate(R.menu.menu_home_call, menu);
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
        else if (id == R.id.menu_call) {
            String phoneNumber=getIntent().getExtras().getString(Common.PHONENUMBER);
            if(phoneNumber.equals("")) return false;
            Uri number = Uri.parse("tel:" + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);
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
