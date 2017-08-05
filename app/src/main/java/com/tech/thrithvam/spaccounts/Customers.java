package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class Customers extends AppCompatActivity {
    CustomAdapter adapter;
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
       getCustomers();
    }
    void getCustomers(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Customer/GetCustomerDetailsMobile";
        String postData = "";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"ID",//0
                                "CompanyName",//1
                                "ContactPerson",//2
                                "Mobile",//3
                                "BillingAddress",//4
                                "OutStanding"//5
                                };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                adapter=new CustomAdapter(Customers.this,common.dataArrayList,Common.CUSTOMERSLIST);
                ListView customersList=(ListView)findViewById(R.id.customers_list);
                customersList.setAdapter(adapter);
                customersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(Customers.this,Invoices.class);
                        intent.putExtra(Common.CUSTOMER_OR_SUPPLIER,Common.CUSTOMER);
                        intent.putExtra(Common.CUSTOMERID,common.dataArrayList.get(position)[0]);
                        intent.putExtra(Common.NAME,common.dataArrayList.get(position)[1]);
                        intent.putExtra(Common.PHONENUMBER,common.dataArrayList.get(position)[3].equals("null")?"":common.dataArrayList.get(position)[3]);
                        startActivity(intent);
                    }
                });
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(Customers.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(Customers.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    public void callClick(View view){
        if(view.getTag().toString().equals("")) return;
        Uri number = Uri.parse("tel:" + view.getTag().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }
    public void customerDetails(View view){
        Intent intent=new Intent(Customers.this,CustomerDetails.class);
        intent.putExtra(Common.CUSTOMERID,view.getTag().toString());
        startActivity(intent);
    }
    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_search, menu);
        //Searching-------------------
        searchView=(SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null){//for searching
                    adapter.getFilter(1).filter(searchView.getQuery().toString().trim());
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
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
