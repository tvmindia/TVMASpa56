package com.tech.thrithvam.spaccounts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ApprovalExpenseDetails extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CircularProgressButton approveButton;
    View headerDetails;
    LinearLayout headerView;
    LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_expense_details);
        getSupportActionBar().setTitle("Approval: "+getIntent().getExtras().getString(Common.REFNO));
         headerView=(LinearLayout)findViewById(R.id.header);
         inflater=getLayoutInflater();
        headerDetails=inflater.inflate(R.layout.item_approval_expense_header,null);
        getApprovalDetails();



    }
    void getApprovalDetails(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Expense/GetOtherExpenseByIDForMobile";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.APPROVALID)+"\"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                try {
                    findViewById(R.id.header_card).setVisibility(View.VISIBLE);
                    JSONObject jsonObject1 = new JSONObject(common.json);
                    ((TextView)headerDetails.findViewById(R.id.exp_no)).setText(jsonObject1.getString("RefNo"));
                    ((TextView)headerDetails.findViewById(R.id.amount)).setText(getResources().getString(R.string.rupees,jsonObject1.getString("Amount")));
                    ((TextView)headerDetails.findViewById(R.id.description)).setText(jsonObject1.getString("Description"));
                    ((TextView)headerDetails.findViewById(R.id.exp_date)).setText(jsonObject1.getString("ExpenseDate"));
                    ((TextView)headerDetails.findViewById(R.id.employee_name)).setText(jsonObject1.getJSONObject("employee").getString("Name"));
                    ((TextView)headerDetails.findViewById(R.id.account_head)).setText(getResources().getString(R.string.account_label,jsonObject1.getJSONObject("chartOfAccountsObj").getString("TypeDesc")));

                    //adding to screen
                    headerView.addView(headerDetails);
                    View approveButtonView=inflater.inflate(R.layout.item_approval_button,null);
                    approveButton=(CircularProgressButton)approveButtonView.findViewById(R.id.btnWithText);
                    approveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(ApprovalExpenseDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
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
                } catch (JSONException e) {
                    Toast.makeText(ApprovalExpenseDetails.this, "Some error occurred\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(ApprovalExpenseDetails.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(ApprovalExpenseDetails.this,
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
        String webService="/API/Expense/ApproveOtherExpenseForMobile";
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
                        Intent intent = new Intent(ApprovalExpenseDetails.this, Approvals.class);
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
                Common.toastMessage(ApprovalExpenseDetails.this,common.msg);
                Common.toastMessage(ApprovalExpenseDetails.this, R.string.failed_try_again);
                approveButton.setProgress(-1);
            }};
        common.AsynchronousThread(ApprovalExpenseDetails.this,
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
