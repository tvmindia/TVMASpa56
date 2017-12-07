package com.tech.thrithvam.spaccounts;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InvoiceSummary extends AppCompatActivity {

    static TextView startDate,endDate;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static int salesORpurchase;
    static ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        salesORpurchase=getIntent().getExtras().getInt("salesORpurchase");
        if(salesORpurchase==Common.SALES){
            getSupportActionBar().setTitle("Sales Summary");
        }
        else {
            getSupportActionBar().setTitle("Purchase Summary");
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    SearchView searchView;
    static ArrayList<ListView> lists=new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                for(int i=lists.size()-1;i>=0;i--) {
                    final int Fi=i;
                    CustomAdapter adapter = (CustomAdapter) lists.get(i).getAdapter();
                    if (adapter != null) {//for searching
                        adapter.getFilter(Arrays.asList(1, 3, 7)).filter(searchView.getQuery().toString().trim(),new Filter.FilterListener() {
                            public void onFilterComplete(int count) {
                                if(count>0){
                                    try {
                                        TabLayout tabHost = (TabLayout) findViewById(R.id.tabs);
                                        tabHost.getTabAt(Fi).select();
                                    }
                                    catch (Exception e){
//                                        Common.toastMessage(InvoiceSummary.this,e.getMessage());
                                    }
                                }
                            }
                        });
                    }
                }
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
            final View rootView = inflater.inflate(R.layout.fragment_invoice_summary, container, false);
            final LinearLayout fragmentLinear=(LinearLayout)rootView.findViewById(R.id.fragment_linear);
            final ListView invoiceList=(ListView)rootView.findViewById(R.id.invoice_list);
            lists.add(invoiceList);
            if( getArguments().getInt(ARG_SECTION_NUMBER)==1){//outstanding
                if(salesORpurchase==Common.SALES) {
                    //Threading------------------------------------------------------------------------------------------------------
                    final Common common = new Common();
                    String webService = "API/InvoiceSummary/GetOutstandingInvoicesForMobile";
                    String postData = "{\"customerObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                    AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                    String[] dataColumns = {};
                    Runnable postThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject;
                            try {
                                jsonObject=new JSONObject(common.json);
                                JSONArray invoices = jsonObject.getJSONArray("OutstandingList");
                                if(invoices.length()==0){
                                    Common.toastMessage(getContext(),getContext().getResources().getString(R.string.no_items)+" in outstanding list");
                                    (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                    return;
                                }
                                ArrayList<String[]> invoiceListData = new ArrayList<>();
                                for (int i = 0; i < invoices.length(); i++) {
                                    JSONObject jsonObject1 = invoices.getJSONObject(i);
                                    String[] data = new String[8];
                                    data[0] = jsonObject1.getString("ID");
                                    data[1] = jsonObject1.getString("InvoiceNo");
                                    data[2] = jsonObject1.getJSONObject("customerObj").getString("ID");
                                    data[3] = jsonObject1.getJSONObject("customerObj").getString("ContactPerson");
                                    data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                    data[5] = jsonObject1.getString("BalanceDue");
                                    data[6] = jsonObject1.getString("PaidAmount");
                                    data[7] = jsonObject1.getJSONObject("customerObj").getString("CompanyName");
                                    invoiceListData.add(data);
                                }
                                CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.SALESLIST);
                                invoiceList.setAdapter(adapter);

                                View invoiceHeader=inflater.inflate(R.layout.item_invoice_header,null);
                                fragmentLinear.addView(invoiceHeader);
                                JSONObject summary = jsonObject.getJSONObject("Summary");
                                ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Outstanding: "+ summary.getString("AmountFormatted"));
                                ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                                (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                else if(salesORpurchase==Common.PURCHASE) {
                    //Threading------------------------------------------------------------------------------------------------------
                    final Common common = new Common();
                    String webService = "API/PurchaseSummary/GetSupplierOutstandingInvoicesForMobile";
                    String postData = "{\"suppliersObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                    AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                    String[] dataColumns = {};
                    Runnable postThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject;
                            try {
                                jsonObject=new JSONObject(common.json);
                                JSONArray invoices = jsonObject.getJSONArray("OutstandingList");
                                if(invoices.length()==0){
                                    Common.toastMessage(getContext(),getContext().getResources().getString(R.string.no_items)+" in outstanding list");
                                    (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                    return;
                                }
                                ArrayList<String[]> invoiceListData = new ArrayList<>();
                                for (int i = 0; i < invoices.length(); i++) {
                                    JSONObject jsonObject1 = invoices.getJSONObject(i);
                                    String[] data = new String[8];
                                    data[0] = jsonObject1.getString("ID");
                                    data[1] = jsonObject1.getString("InvoiceNo");
                                    data[2] = jsonObject1.getJSONObject("suppliersObj").getString("ID");
                                    data[3] = jsonObject1.getJSONObject("suppliersObj").getString("ContactPerson");
                                    data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                    data[5] = jsonObject1.getString("BalanceDue");
                                    data[6] = jsonObject1.getString("PaidAmount");
                                    data[7] = jsonObject1.getJSONObject("suppliersObj").getString("CompanyName");
                                    invoiceListData.add(data);
                                }
                                CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.PURCHASELIST);
                                invoiceList.setAdapter(adapter);

                                View invoiceHeader=inflater.inflate(R.layout.item_invoice_header,null);
                                fragmentLinear.addView(invoiceHeader);
                                JSONObject summary = jsonObject.getJSONObject("Summary");
                                ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Outstanding: "+ summary.getString("AmountFormatted"));
                                ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                                (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Runnable postThreadFailed = new Runnable() {
                        @Override
                        public void run() {
                            Common.toastMessage(getContext(), R.string.failed_server );
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
                else {
                    ((Activity)getContext()).finish();
                }
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2) {//open
                if (salesORpurchase == Common.SALES) {
                    //Threading------------------------------------------------------------------------------------------------------
                    final Common common = new Common();
                    String webService = "API/InvoiceSummary/GetOpenInvoicesForMobile";
                    String postData = "{\"customerObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                    AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                    String[] dataColumns = {};
                    Runnable postThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject;
                            try {
                                jsonObject=new JSONObject(common.json);
                                JSONArray invoices = jsonObject.getJSONArray("OpeningList");
                                if(invoices.length()==0){
                                    Common.toastMessage(getContext(),getContext().getResources().getString(R.string.no_items)+" in open list");
                                    (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                    return;
                                }
                                ArrayList<String[]> invoiceListData = new ArrayList<>();
                                for (int i = 0; i < invoices.length(); i++) {
                                    JSONObject jsonObject1 = invoices.getJSONObject(i);
                                    String[] data = new String[8];
                                    data[0] = jsonObject1.getString("ID");
                                    data[1] = jsonObject1.getString("InvoiceNo");
                                    data[2] = jsonObject1.getJSONObject("customerObj").getString("ID");
                                    data[3] = jsonObject1.getJSONObject("customerObj").getString("ContactPerson");
                                    data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                    data[5] = jsonObject1.getString("BalanceDue");
                                    data[6] = jsonObject1.getString("PaidAmount");
                                    data[7] = jsonObject1.getJSONObject("customerObj").getString("CompanyName");
                                    invoiceListData.add(data);
                                }
                                CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.SALESLIST);
                                invoiceList.setAdapter(adapter);


                                View invoiceHeader = inflater.inflate(R.layout.item_invoice_header, null);
                                fragmentLinear.addView(invoiceHeader);
                                JSONObject summary = jsonObject.getJSONObject("Summary");
                                ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Open: "+ summary.getString("AmountFormatted"));
                                ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                                (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                else if(salesORpurchase==Common.PURCHASE) {
                    //Threading------------------------------------------------------------------------------------------------------
                    final Common common = new Common();
                    String webService = "API/PurchaseSummary/GetSupplierOpeningInvoicesForMobile";
                    String postData = "{\"suppliersObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                    AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                    String[] dataColumns = {};
                    Runnable postThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject;
                            try {
                                jsonObject=new JSONObject(common.json);
                                JSONArray invoices = jsonObject.getJSONArray("OpeningList");
                                if(invoices.length()==0){
                                    Common.toastMessage(getContext(),getContext().getResources().getString(R.string.no_items)+" in open list");
                                    (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                    return;
                                }
                                ArrayList<String[]> invoiceListData = new ArrayList<>();
                                for (int i = 0; i < invoices.length(); i++) {
                                    JSONObject jsonObject1 = invoices.getJSONObject(i);
                                    String[] data = new String[8];
                                    data[0] = jsonObject1.getString("ID");
                                    data[1] = jsonObject1.getString("InvoiceNo");
                                    data[2] = jsonObject1.getJSONObject("suppliersObj").getString("ID");
                                    data[3] = jsonObject1.getJSONObject("suppliersObj").getString("ContactPerson");
                                    data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                    data[5] = jsonObject1.getString("BalanceDue");
                                    data[6] = jsonObject1.getString("PaidAmount");
                                    data[7] = jsonObject1.getJSONObject("suppliersObj").getString("CompanyName");
                                    invoiceListData.add(data);
                                }
                                CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.PURCHASELIST);
                                invoiceList.setAdapter(adapter);


                                View invoiceHeader = inflater.inflate(R.layout.item_invoice_header, null);
                                fragmentLinear.addView(invoiceHeader);
                                JSONObject summary = jsonObject.getJSONObject("Summary");
                                ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Open: "+ summary.getString("AmountFormatted"));
                                ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                                (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                else {
                    ((Activity)getContext()).finish();
                }
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3) {//date-wise
                final View invoiceHeader = inflater.inflate(R.layout.item_invoice_header, null);
                fragmentLinear.addView(invoiceHeader);
                (invoiceHeader.findViewById(R.id.dates_layout)).setVisibility(View.VISIBLE);
                (invoiceHeader.findViewById(R.id.amount)).setVisibility(View.GONE);
                (invoiceHeader.findViewById(R.id.count)).setVisibility(View.GONE);
                startDate=(TextView)invoiceHeader.findViewById(R.id.start_date);
                endDate=(TextView)invoiceHeader.findViewById(R.id.end_date);
                Calendar today = Calendar.getInstance();
                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                startDate.setText(formatted.format(today.getTime()));
                endDate.setText(formatted.format(today.getTime()));
                getDateWiseInvoices(rootView,invoiceHeader,invoiceList);
                View.OnClickListener onClickListener=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TextView requiredDate=(TextView)v;
                        final String oldText=requiredDate.getText().toString();
                        final Calendar today = Calendar.getInstance();
                        final Calendar selectedDate=Calendar.getInstance();
                        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, monthOfYear);
                                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                //Setting display text-------
                                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                requiredDate.setText(formatted.format(selectedDate.getTime()));
                                //Validation--------------
                                if(!startDate.getText().toString().equals(getResources().getString(R.string.start_date))
                                        &&!endDate.getText().toString().equals(getResources().getString(R.string.end_date))){
                                    try {
                                        Date sDate=formatted.parse(startDate.getText().toString());
                                        Date eDate=formatted.parse(endDate.getText().toString());
                                        if(sDate.after(eDate)){
                                            Common.toastMessage(getContext(),R.string.give_valid);
                                            requiredDate.setText(oldText);
                                            return;
                                        }
                                    } catch (ParseException e) {
                                        requiredDate.setText(oldText);
                                        return;
                                    }
                                    getDateWiseInvoices(rootView,invoiceHeader,invoiceList);
                                }
                            }
                        };
                        new DatePickerDialog(getContext(), dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();

                    }
                };
                startDate.setOnClickListener(onClickListener);
                endDate.setOnClickListener(onClickListener);
            }
            return rootView;
        }
        void getDateWiseInvoices(final View rootView,final View invoiceHeader,final ListView invoiceList){
            (rootView.findViewById(R.id.no_items)).setVisibility(View.GONE);
            (invoiceHeader.findViewById(R.id.amount)).setVisibility(View.GONE);
            (invoiceHeader.findViewById(R.id.count)).setVisibility(View.GONE);
            (rootView.findViewById(R.id.list_card)).setVisibility(View.GONE);
            invoiceList.setAdapter(null);
            if (salesORpurchase == Common.SALES) {
                //Threading------------------------------------------------------------------------------------------------------
                final Common common = new Common();
                String webService = "/API/InvoiceSummary/GetCustomerInvoicesByDateWiseForMobile";
                String postData = "{\"FromDate\":\""+startDate.getText().toString()+"\",\"ToDate\":\""+endDate.getText().toString()+"\",\"customerObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                String[] dataColumns = {};
                Runnable postThread = new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject;
                        try {
                            jsonObject=new JSONObject(common.json);
                            JSONArray invoices = jsonObject.getJSONArray("List");
                            if(invoices.length()==0){
                                (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                return;
                            }
                            ArrayList<String[]> invoiceListData = new ArrayList<>();
                            for (int i = 0; i < invoices.length(); i++) {
                                JSONObject jsonObject1 = invoices.getJSONObject(i);
                                String[] data = new String[8];
                                data[0] = jsonObject1.getString("ID");
                                data[1] = jsonObject1.getString("InvoiceNo");
                                data[2] = jsonObject1.getJSONObject("customerObj").getString("ID");
                                data[3] = jsonObject1.getJSONObject("customerObj").getString("ContactPerson");
                                data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                data[5] = jsonObject1.getString("BalanceDue");
                                data[6] = jsonObject1.getString("PaidAmount");
                                data[7] = jsonObject1.getJSONObject("customerObj").getString("CompanyName");
                                invoiceListData.add(data);
                            }
                            CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.SALESLIST);
                            invoiceList.setAdapter(adapter);

                            JSONObject summary = jsonObject.getJSONObject("Summary");
                            ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Amount: "+ summary.getString("AmountFormatted"));
                            ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                            (invoiceHeader.findViewById(R.id.amount)).setVisibility(View.VISIBLE);
                            (invoiceHeader.findViewById(R.id.count)).setVisibility(View.VISIBLE);
                            (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            else if(salesORpurchase==Common.PURCHASE) {
                //Threading------------------------------------------------------------------------------------------------------
                final Common common = new Common();
                String webService = "API/PurchaseSummary/GetSupplierPurchaseByDateWiseForMobile";
                String postData = "{\"FromDate\":\""+startDate.getText().toString()+"\",\"ToDate\":\""+endDate.getText().toString()+"\",\"suppliersObj\":{\"IsInternalComp\":\""+Common.getInternalCompanySettings(getContext())+"\"}}";
                AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                String[] dataColumns = {};
                Runnable postThread = new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject;
                        try {
                            jsonObject=new JSONObject(common.json);
                            JSONArray invoices = jsonObject.getJSONArray("List");
                            if(invoices.length()==0){
                                (rootView.findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                                return;
                            }
                            ArrayList<String[]> invoiceListData = new ArrayList<>();
                            for (int i = 0; i < invoices.length(); i++) {
                                JSONObject jsonObject1 = invoices.getJSONObject(i);
                                String[] data = new String[8];
                                data[0] = jsonObject1.getString("ID");
                                data[1] = jsonObject1.getString("InvoiceNo");
                                data[2] = jsonObject1.getJSONObject("suppliersObj").getString("ID");
                                data[3] = jsonObject1.getJSONObject("suppliersObj").getString("ContactPerson");
                                data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                data[5] = jsonObject1.getString("BalanceDue");
                                data[6] = jsonObject1.getString("PaidAmount");
                                data[7] = jsonObject1.getJSONObject("suppliersObj").getString("CompanyName");
                                invoiceListData.add(data);
                            }
                            CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.PURCHASELIST);
                            invoiceList.setAdapter(adapter);


                            JSONObject summary = jsonObject.getJSONObject("Summary");
                            ((TextView)invoiceHeader.findViewById(R.id.amount)).setText("Amount: "+ summary.getString("AmountFormatted"));
                            ((TextView)invoiceHeader.findViewById(R.id.count)).setText("No of Invoices: "+ summary.getString("count"));
                            (invoiceHeader.findViewById(R.id.amount)).setVisibility(View.VISIBLE);
                            (invoiceHeader.findViewById(R.id.count)).setVisibility(View.VISIBLE);
                            (rootView.findViewById(R.id.list_card)).setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            else {
                ((Activity)getContext()).finish();
            }
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
            // Show 2 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Outstanding";
                case 1:
                    return "Open";
                case 2:
                    return "Date-wise";
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}
