package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerDetails extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        getCustomers();
    }
    void getCustomers(){
        (findViewById(R.id.screen_data)).setVisibility(View.GONE);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Customer/GetCustomerDetailsByIDForMobile";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.CUSTOMERID)+"\"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = { };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                try {
                    (findViewById(R.id.screen_data)).setVisibility(View.VISIBLE);
                    final JSONObject customerObject=new JSONObject(common.json);
                    ((TextView)findViewById(R.id.company_name)).setText(customerObject.getString("CompanyName").equals("null")?"-":customerObject.getString("CompanyName"));
                    ((TextView)findViewById(R.id.contact_title)).setText(customerObject.getString("ContactTitle").equals("null")?"":customerObject.getString("ContactTitle"));
                    ((TextView)findViewById(R.id.contact_person_name)).setText(customerObject.getString("ContactPerson").equals("null")?"-":customerObject.getString("ContactPerson"));
                    ((TextView)findViewById(R.id.email)).setText(customerObject.getString("ContactEmail").equals("null")?"-":customerObject.getString("ContactEmail"));
                            if(!customerObject.getString("ContactEmail").equals("null")) {
                                (findViewById(R.id.email)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                    "mailto", customerObject.getString("ContactEmail"), null));
                                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }});
                            }
                    ((TextView)findViewById(R.id.website)).setText(customerObject.getString("Website").equals("null")?"-":customerObject.getString("Website"));
                            if(!customerObject.getString("Website").equals("null")) {
                                (findViewById(R.id.website)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String url = customerObject.getString("Website");
                                            if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }});
                            }
                    ((TextView)findViewById(R.id.landline)).setText(customerObject.getString("LandLine").equals("null")?"-":customerObject.getString("LandLine"));
                        if(!customerObject.getString("LandLine").equals("null")){
                            (findViewById(R.id.landline)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Uri number = Uri.parse("tel:" + customerObject.getString("LandLine"));
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                        startActivity(callIntent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }});
                        }
                    ((TextView)findViewById(R.id.mobile)).setText(customerObject.getString("Mobile").equals("null")?"-":customerObject.getString("Mobile"));
                        if(!customerObject.getString("Mobile").equals("null")){
                            (findViewById(R.id.mobile)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Uri number = Uri.parse("tel:" + customerObject.getString("Mobile"));
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                        startActivity(callIntent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }});
                        }
                    ((TextView)findViewById(R.id.other_numbers)).setText(customerObject.getString("OtherPhoneNos").equals("null")?"-":customerObject.getString("OtherPhoneNos"));
                    ((TextView)findViewById(R.id.billing_address)).setText(customerObject.getString("BillingAddress").equals("null")?"-":customerObject.getString("BillingAddress"));
                    ((TextView)findViewById(R.id.shipping_address)).setText(customerObject.getString("ShippingAddress").equals("null")?"-":customerObject.getString("ShippingAddress"));
                    ((TextView)findViewById(R.id.payment_term)).setText(customerObject.getJSONObject("PaymentTermsObj").getString("NoOfDays").equals("null")?"-":getResources().getString(R.string._days,customerObject.getJSONObject("PaymentTermsObj").getString("NoOfDays")));
                    ((TextView)findViewById(R.id.notes)).setText(customerObject.getString("GeneralNotes").equals("null")?"-":customerObject.getString("GeneralNotes"));
                    ((TextView)findViewById(R.id.outstanding)).setText(customerObject.getString("OutStanding").equals("null")?"-":getResources().getString(R.string.rupees,customerObject.getString("OutStanding")));
                } catch (JSONException e) {
                    Common.toastMessage(CustomerDetails.this, R.string.failed_server);
                    e.printStackTrace();
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(CustomerDetails.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(CustomerDetails.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}
