package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Customers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        ArrayList<String[]> customerData=new ArrayList<>();
        for(int i=0;i<15;i++){
            String[] data=new String[3];
            data[0]=Integer.toString(i);
            data[1]="Customer Name "+i;
            data[2]=Integer.toString((int)(Math.random()*1000000000)%1000000000);
            customerData.add(data);
        }
        CustomAdapter adapter=new CustomAdapter(Customers.this,customerData,"Customers");
        ListView customersList=(ListView)findViewById(R.id.customers_list);
        customersList.setAdapter(adapter);
    }
    public void callClick(View view){
        Uri number = Uri.parse("tel:" + view.getTag().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
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
}
