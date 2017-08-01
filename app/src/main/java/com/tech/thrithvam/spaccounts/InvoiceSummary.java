package com.tech.thrithvam.spaccounts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvoiceSummary extends AppCompatActivity {

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

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_invoice_summary, container, false);
            LinearLayout fragmentLinear=(LinearLayout)rootView.findViewById(R.id.fragment_linear);
            final ListView invoiceList=(ListView)rootView.findViewById(R.id.invoice_list);
            if( getArguments().getInt(ARG_SECTION_NUMBER)==1){
                View invoiceHeader=inflater.inflate(R.layout.item_invoice_header,null);
                fragmentLinear.addView(invoiceHeader);
                if(salesORpurchase==Common.SALES) {
                    //Threading------------------------------------------------------------------------------------------------------
                    final Common common = new Common();
                    String webService = "API/InvoiceSummary/GetOutstandingInvoicesForMobile";
                    String postData = "";
                    AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
                    String[] dataColumns = {};
                    Runnable postThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject = null;
                            try {
                                JSONArray invoices = new JSONArray(common.json);
                                if(invoices.length()==0){
                                    Common.toastMessage(getContext(),R.string.no_items);
                                    return;
                                }
                                ArrayList<String[]> invoiceListData = new ArrayList<>();
                                for (int i = 0; i < invoices.length(); i++) {
                                    JSONObject jsonObject1 = invoices.getJSONObject(i);
                                    String[] data = new String[7];
                                    data[0] = jsonObject1.getString("ID");
                                    data[1] = jsonObject1.getString("InvoiceNo");
                                    data[2] = jsonObject1.getJSONObject("customerObj").getString("ID");
                                    data[3] = jsonObject1.getJSONObject("customerObj").getString("ContactPerson");
                                    data[4] = jsonObject1.getString("PaymentDueDateFormatted");
                                    data[5] = jsonObject1.getString("BalanceDue");
                                    data[6] = jsonObject1.getString("PaidAmount");
                                    invoiceListData.add(data);
                                }
                                CustomAdapter adapter = new CustomAdapter(getContext(), invoiceListData, Common.SALESLIST);
                                invoiceList.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Runnable postThreadFailed = new Runnable() {
                        @Override
                        public void run() {
                            Common.toastMessage(getContext(), R.string.failed_try_again);
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
            return rootView;
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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Outstanding";
                case 1:
                    return "Open";
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
