package com.tech.thrithvam.spaccounts;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}
