package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class Customers extends AppCompatActivity {
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        ArrayList<String[]> customerData=new ArrayList<>();
        for(int i=0;i<15;i++){
            String[] data=new String[4];
            data[0]=Integer.toString(i);
            data[1]="Customer Name "+i;
            data[2]=Integer.toString((int)(Math.random()*1000000000)%1000000000);
            int amount=(int)((Math.random()*10000000)%10000000);
            if(amount%2==0){
                data[3]=Integer.toString(amount);
            }
            else {
                data[3]="-"+Integer.toString(amount);
            }
            customerData.add(data);
        }
        adapter=new CustomAdapter(Customers.this,customerData,Common.CUSTOMERSLIST);
        ListView customersList=(ListView)findViewById(R.id.customers_list);
        customersList.setAdapter(adapter);
    }
    public void callClick(View view){
        Uri number = Uri.parse("tel:" + view.getTag().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
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
}
