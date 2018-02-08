package com.tech.thrithvam.spaccounts;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Locale;

public class Approvals extends AppCompatActivity {
    static ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        getSupportActionBar().setTitle("Approvals");
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString("UserName","");
        if(userName.equals("")){
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


   public static class PlaceholderFragment extends Fragment {
       /**
        * The fragment argument representing the section number for this
        * fragment.
        */
       private static final String ARG_SECTION_NUMBER = "section_number";

       public PlaceholderFragment() {
       }

       /**
        * Returns a new instance of this fragment for the given section
        * number.
        */
       public static PlaceholderFragment newInstance(int sectionNumber) {
           PlaceholderFragment fragment = new PlaceholderFragment();
           Bundle args = new Bundle();
           args.putInt(ARG_SECTION_NUMBER, sectionNumber);
           fragment.setArguments(args);
           return fragment;
       }

       @Override
       public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
           final View rootView = inflater.inflate(R.layout.fragment_approval_list, container, false);//same list for both pending and other expense approvals
           final LinearLayout fragmentLinear=(LinearLayout)rootView.findViewById(R.id.fragment_linear);
           //final ListView invoiceList=(ListView)rootView.findViewById(R.id.invoice_list);
           //lists.add(invoiceList);
           if( getArguments().getInt(ARG_SECTION_NUMBER)==1) {//pending approals
               getApprovals(rootView);
           }
           else if(getArguments().getInt(ARG_SECTION_NUMBER)==2) {//open
               getOtherExpenseApprovals(rootView);
           }
           return rootView;
       }
       void getApprovals(final View rootView){
           //Threading------------------------------------------------------------------------------------------------------
           final Common common = new Common();
           String webService = "/API/Supplier/GetAllPendingSupplierPaymentsForMobile";
           String postData = "";
           AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
           String[] dataColumns = {"ID",//0
                   "EntryNo",//1
                   "PaymentMode",//2
                   "PaymentDateFormatted",//3
                   "TotalPaidAmt",//4
                   "supplierObj",//5
                   "GeneralNotes"//6
           };
           Runnable postThread = new Runnable() {
               @Override
               public void run() {
                   if(common.dataArrayList.size()==0)
                   {
                       rootView.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                   }
                   CustomAdapter adapter=new CustomAdapter(getContext(),common.dataArrayList,Common.APPROVALLIST);
                   ListView approvalList=(ListView)rootView.findViewById(R.id.approvals_list);
                   approvalList.setAdapter(adapter);
                   approvalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           Intent approvalDetailsIntent=new Intent(getContext(),ApprovalDetails.class);
                           approvalDetailsIntent.putExtra(Common.APPROVALID,common.dataArrayList.get(position)[0]);
                           approvalDetailsIntent.putExtra(Common.ENTRYNO,common.dataArrayList.get(position)[1]);
                           approvalDetailsIntent.putExtra(Common.PAYMENT_MODE,common.dataArrayList.get(position)[2]);
                           approvalDetailsIntent.putExtra(Common.PAYMENT_DATE,common.dataArrayList.get(position)[3]);
                           approvalDetailsIntent.putExtra(Common.AMOUNT,common.dataArrayList.get(position)[4]);
                           approvalDetailsIntent.putExtra(Common.COMPANY_DETAILS,common.dataArrayList.get(position)[5]);
                           approvalDetailsIntent.putExtra(Common.GENERAL_NOTES,common.dataArrayList.get(position)[6]);
                           startActivity(approvalDetailsIntent);
                       }
                   });
               }
           };
           Runnable postThreadFailed = new Runnable() {
               @Override
               public void run() {
                   Common.toastMessage(getContext(), R.string.failed_server);
               }
           };

           common.AsynchronousThread(getContext(),
                   webService,
                   postData,
                   loadingIndicator,
                   dataColumns,
                   postThread,
                   postThreadFailed);
           asyncTasks.add(common.asyncTask);
           //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       }
       void getOtherExpenseApprovals(final View rootView){
           //Threading------------------------------------------------------------------------------------------------------
           final Common common = new Common();
           String webService = "/API/Expense/GetAllPendingForApprovalExpenseByPostForMobile";
           String postData = "";
           AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
           String[] dataColumns = {"ID",//0
                   "RefNo",//1
                   "ExpenseDate",//2
                   "Description",//3
                   "Amount" //4
           };
           Runnable postThread = new Runnable() {
               @Override
               public void run() {
                   if(common.dataArrayList.size()==0)
                   {
                       rootView.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                   }
                   CustomAdapter adapter=new CustomAdapter(getContext(),common.dataArrayList,Common.OTHEREXPENSEAPPROVALLIST);
                   ListView approvalList=(ListView)rootView.findViewById(R.id.approvals_list);
                   approvalList.setAdapter(adapter);
                   approvalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           Intent approvalDetailsIntent=new Intent(getContext(),ApprovalExpenseDetails.class);
                           approvalDetailsIntent.putExtra(Common.APPROVALID,common.dataArrayList.get(position)[0]);
                           approvalDetailsIntent.putExtra(Common.REFNO,common.dataArrayList.get(position)[1]);
                           startActivity(approvalDetailsIntent);
                       }
                   });
               }
           };
           Runnable postThreadFailed = new Runnable() {
               @Override
               public void run() {
                   Common.toastMessage(getContext(), R.string.failed_server);
               }
           };

           common.AsynchronousThread(getContext(),
                   webService,
                   postData,
                   loadingIndicator,
                   dataColumns,
                   postThread,
                   postThreadFailed);
           asyncTasks.add(common.asyncTask);
           //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       }
   }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show pages
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Payment Approval";
                case 1:
                    return "Other Expense Approval";
            }
            return null;
        }
    }


//Menu and Back
    SearchView searchView;
    static ArrayList<ListView> lists=new ArrayList<>();//not used yet
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
