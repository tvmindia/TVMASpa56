package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class Approvals extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);
        getApprovals();
    }
    void getApprovals(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "/API/Supplier/GetAllPendingSupplierPaymentsForMobile";
        String postData = "";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"ID",//0
                "EntryNo",//1
                "PaymentMode",//2
                "PaymentDateFormatted",//3
                "TotalPaidAmt",//4
                "supplierObj"//5
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                if(common.dataArrayList.size()==0)
                {
                    findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                }
                CustomAdapter adapter=new CustomAdapter(Approvals.this,common.dataArrayList,Common.APPROVALLIST);
                ListView approvalList=(ListView)findViewById(R.id.approvals_list);
                approvalList.setAdapter(adapter);
                approvalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent approvalDetailsIntent=new Intent(Approvals.this,ApprovalDetails.class);
                        approvalDetailsIntent.putExtra(Common.APPROVALID,common.dataArrayList.get(position)[0]);
                        approvalDetailsIntent.putExtra(Common.ENTRYNO,common.dataArrayList.get(position)[1]);
                        approvalDetailsIntent.putExtra(Common.PAYMENT_MODE,common.dataArrayList.get(position)[2]);
                        approvalDetailsIntent.putExtra(Common.PAYMENT_DATE,common.dataArrayList.get(position)[3]);
                        approvalDetailsIntent.putExtra(Common.AMOUNT,common.dataArrayList.get(position)[4]);
                        approvalDetailsIntent.putExtra(Common.COMPANY_DETAILS,common.dataArrayList.get(position)[5]);
                        startActivity(approvalDetailsIntent);
                    }
                });
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(Approvals.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(Approvals.this,
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
        if(isTaskRoot())
        {
            Intent intent=new Intent(this,HomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }
}
