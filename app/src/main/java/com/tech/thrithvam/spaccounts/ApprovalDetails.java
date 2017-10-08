package com.tech.thrithvam.spaccounts;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApprovalDetails extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CircularProgressButton approveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);
        getApprovalDetails();
        getSupportActionBar().setTitle("Approval: "+getIntent().getExtras().getString(Common.ENTRYNO));
        LinearLayout headerView=(LinearLayout)findViewById(R.id.header);
        LayoutInflater inflater=getLayoutInflater();
        View headerDetails=inflater.inflate(R.layout.item_approval_header,null);
        ((TextView)headerDetails.findViewById(R.id.entry_no)).setText(
                getIntent().getExtras().getString(Common.ENTRYNO).equals("null")?"-":getIntent().getExtras().getString(Common.ENTRYNO));
        ((TextView)headerDetails.findViewById(R.id.payment_date)).setText(
                getIntent().getExtras().getString(Common.PAYMENT_DATE).equals("null")?"-":getIntent().getExtras().getString(Common.PAYMENT_DATE));
        ((TextView)headerDetails.findViewById(R.id.payment_mode)).setText(
                getIntent().getExtras().getString(Common.PAYMENT_MODE).equals("null")?"-":getIntent().getExtras().getString(Common.PAYMENT_MODE));
        ((TextView)headerDetails.findViewById(R.id.amount)).setText(
                getIntent().getExtras().getString(Common.AMOUNT).equals("null")?"-":getResources().getString(R.string.rupees,getIntent().getExtras().getString(Common.AMOUNT)));
        String companyName="";
        if(!getIntent().getExtras().getString(Common.COMPANY_DETAILS).equals("null"))
        {
            try {
                JSONObject jsonObject=new JSONObject(getIntent().getExtras().getString(Common.COMPANY_DETAILS));
                companyName=jsonObject.getString("CompanyName");
                ((TextView)headerDetails.findViewById(R.id.company_name)).setText(companyName);
            } catch (JSONException e) {
                ((TextView)headerDetails.findViewById(R.id.company_name)).setText("-");
            }
        }
        else {
            ((TextView)headerDetails.findViewById(R.id.company_name)).setText("");
        }
        headerView.addView(headerDetails);
        View approveButtonView=inflater.inflate(R.layout.item_approval_button,null);
        approveButton=(CircularProgressButton)approveButtonView.findViewById(R.id.btnWithText);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ApprovalDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                        .setMessage(getResources().getString(R.string.approve_q))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            approve();
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .setCancelable(true).show();
            }
        });
        headerView.addView(approveButtonView);

    }
    void getApprovalDetails(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "/API/Supplier/GetAllSupplierInvoiceAdjustedByPaymentID";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.APPROVALID)+"\"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                ArrayList<String[]> dataArrayList=new ArrayList<>();
                try {
                    JSONArray records=new JSONArray(common.json);
                    if(records.length()==0){
                        Common.toastMessage(ApprovalDetails.this,"No details Available");
                        findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        return;
                    }
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject jsonObject1 = records.getJSONObject(i);
                        JSONObject jsonObject2= jsonObject1.getJSONObject("supplierPaymentsDetailObj");
                        String[] data = new String[5];
                        data[0] = jsonObject2.getString("InvoiceNo");
                        data[1] = jsonObject2.getString("InvoiceAmount");
                        data[2] = jsonObject2.getString("PrevPayment");
                        data[3] = jsonObject2.getString("CurrPayment");
                        data[4] = jsonObject2.getString("BalancePayment");
                        dataArrayList.add(data);
                    }
                } catch (JSONException e) {
                    Toast.makeText(ApprovalDetails.this, "Some error occured", Toast.LENGTH_SHORT).show();
                }
                CustomAdapter adapter=new CustomAdapter(ApprovalDetails.this,dataArrayList,Common.APPROVALDETAILLIST);
                ListView approvalList=(ListView)findViewById(R.id.approval_detail_list);
                approvalList.setAdapter(adapter);
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(ApprovalDetails.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(ApprovalDetails.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    void approve(){
        approveButton.setClickable(false);
        //Loading
        approveButton.setIndeterminateProgressMode(true);
        approveButton.setProgress(50);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common=new Common();
        String webService="/API/Supplier/GetApprovedSupplierPayment";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.APPROVALID)+"\"}";
        String[] dataColumns={};
        Runnable postThread=new Runnable() {
            @Override
            public void run() {
                approveButton.setProgress(100);

                Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ApprovalDetails.this, Approvals.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }, 2000);
            }
        };
        Runnable postThreadFailed=new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(ApprovalDetails.this,common.msg);
                Common.toastMessage(ApprovalDetails.this, R.string.failed_try_again);
                approveButton.setProgress(-1);
            }};
        common.AsynchronousThread(ApprovalDetails.this,
                webService,
                postData,
                null,
                dataColumns,
                postThread,
                postThreadFailed);
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
