package com.tech.thrithvam.spaccounts;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CustomAdapter extends BaseAdapter {
    private Context adapterContext;
    private static LayoutInflater inflater=null;
    private ArrayList<String[]> objects;
    private String calledFrom;
    private SimpleDateFormat formatted;
    private Calendar cal;
    CustomAdapter(Context context, ArrayList<String[]> objects, String calledFrom) {
        // super(context, textViewResourceId, objects);
        initialization(context, objects, calledFrom);
    }
    void initialization(Context context, ArrayList<String[]> objects, String calledFrom){
        adapterContext=context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.objects=objects;
        this.filteredObjects=objects;
        this.calledFrom=calledFrom;
//        formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//        cal= Calendar.getInstance();
    }
    @Override
    public int getCount() {
        return filteredObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        //Customers List-----------
        TextView customerName,contactPersonName,phone,address,amount;
        ImageView callButton;
        //Suppliers List------------
        TextView supplierName;
        //Sales list-------------
        TextView invoiceNo,contactPerson,balAmount,paidAmount,dueDate;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        final int fPos=position;
        switch (calledFrom) {
            //--------------------------for customer list items------------------
            case Common.CUSTOMERSLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_customer, null);
                    holder.customerName = (TextView) convertView.findViewById(R.id.customer_name);
                    holder.phone=(TextView)convertView.findViewById(R.id.phone);
                    holder.amount=(TextView)convertView.findViewById(R.id.amount);
                    holder.callButton=(ImageView)convertView.findViewById(R.id.call_button);
                    holder.contactPerson=(TextView)convertView.findViewById(R.id.contact_person_name);
                    holder.address=(TextView)convertView.findViewById(R.id.address);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.customerName.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.phone.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.contactPerson.setText((filteredObjects.get(position)[3].equals("null")?"-":filteredObjects.get(position)[3]));
                holder.address.setText((filteredObjects.get(position)[4].equals("null")?"-":filteredObjects.get(position)[4]));
                holder.callButton.setTag((filteredObjects.get(position)[3].equals("null")?"":filteredObjects.get(position)[3]));
                if(!filteredObjects.get(position)[5].equals("null")){
                    holder.amount.setText(adapterContext.getResources().getString(R.string.rupees,filteredObjects.get(position)[5]));
                    if(Double.parseDouble(filteredObjects.get(position)[5])<0){
                        holder.amount.setTextColor(Color.RED);
                    }
                    else {
                        holder.amount.setTextColor(Color.DKGRAY);
                    }
                }
                else {
                    holder.amount.setText("-");
                    holder.amount.setTextColor(Color.GRAY);
                }
                break;
            //--------------------------for sales invoice list items------------------
            case Common.SALESLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_sales_invoice, null);
                    holder.invoiceNo = (TextView) convertView.findViewById(R.id.invoice_no);
                    holder.contactPerson=(TextView)convertView.findViewById(R.id.contact_person_name);
                    holder.balAmount=(TextView)convertView.findViewById(R.id.balance_amount);
                    holder.paidAmount = (TextView) convertView.findViewById(R.id.paid_amount);
                    holder.dueDate=(TextView)convertView.findViewById(R.id.due_date);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.invoiceNo.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.contactPerson.setText((filteredObjects.get(position)[3].equals("null")?"-":filteredObjects.get(position)[3]));
                holder.balAmount.setText((filteredObjects.get(position)[5].equals("null")?"-":adapterContext.getResources().getString(R.string.rupees,filteredObjects.get(position)[5])));
                holder.paidAmount.setText((filteredObjects.get(position)[6].equals("null")?"-":adapterContext.getResources().getString(R.string.paid_amount,filteredObjects.get(position)[6])));
                holder.dueDate.setText((filteredObjects.get(position)[4].equals("null")?"-":adapterContext.getResources().getString(R.string.due_date,filteredObjects.get(position)[4])));
                break;
            //--------------------------for purchase invoice list items------------------
            case Common.PURCHASELIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_purchase_invoice, null);
                    holder.invoiceNo = (TextView) convertView.findViewById(R.id.invoice_no);
                    holder.contactPerson=(TextView)convertView.findViewById(R.id.contact_person_name);
                    holder.balAmount=(TextView)convertView.findViewById(R.id.balance_amount);
                    holder.paidAmount = (TextView) convertView.findViewById(R.id.paid_amount);
                    holder.dueDate=(TextView)convertView.findViewById(R.id.due_date);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.invoiceNo.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.contactPerson.setText((filteredObjects.get(position)[3].equals("null")?"-":filteredObjects.get(position)[3]));
                holder.balAmount.setText((filteredObjects.get(position)[5].equals("null")?"-":adapterContext.getResources().getString(R.string.rupees,filteredObjects.get(position)[5])));
                holder.paidAmount.setText((filteredObjects.get(position)[6].equals("null")?"-":adapterContext.getResources().getString(R.string.paid_amount,filteredObjects.get(position)[6])));
                holder.dueDate.setText((filteredObjects.get(position)[4].equals("null")?"-":adapterContext.getResources().getString(R.string.due_date,filteredObjects.get(position)[4])));
                break;
            //--------------------------for customer list items------------------
            case Common.SUPPLIERSLIST:
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_supplier, null);
                    holder.supplierName = (TextView) convertView.findViewById(R.id.supplier_name);
                    holder.phone=(TextView)convertView.findViewById(R.id.phone);
                    holder.amount=(TextView)convertView.findViewById(R.id.amount);
                    holder.callButton=(ImageView)convertView.findViewById(R.id.call_button);
                    holder.contactPerson=(TextView)convertView.findViewById(R.id.contact_person_name);
                    holder.address=(TextView)convertView.findViewById(R.id.address);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.supplierName.setText((filteredObjects.get(position)[1].equals("null")?"-":filteredObjects.get(position)[1]));
                holder.phone.setText((filteredObjects.get(position)[2].equals("null")?"-":filteredObjects.get(position)[2]));
                holder.contactPerson.setText((filteredObjects.get(position)[3].equals("null")?"-":filteredObjects.get(position)[3]));
                holder.address.setText((filteredObjects.get(position)[4].equals("null")?"-":filteredObjects.get(position)[4]));
                holder.callButton.setTag((filteredObjects.get(position)[3].equals("null")?"":filteredObjects.get(position)[3]));
                if(!filteredObjects.get(position)[5].equals("null")){
                    holder.amount.setText(adapterContext.getResources().getString(R.string.rupees,filteredObjects.get(position)[5]));
                  /*  if(Double.parseDouble(filteredObjects.get(position)[5])<0){
                        holder.amount.setTextColor(Color.RED);
                    }
                    else {
                        holder.amount.setTextColor(Color.DKGRAY);
                    }*/
                }
                else {
                    holder.amount.setText("-");
                    holder.amount.setTextColor(Color.GRAY);
                }
                break;
            default:
                break;
        }
        return convertView;
    }

    //Filtering--------------------------------------
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<String[]> filteredObjects;
    private int dataItemPosition;
    Filter getFilter(int dataItem) {
        dataItemPosition=dataItem;
        return mFilter;
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            int count = objects.size();
            final ArrayList<String[]> filteredList = new ArrayList<String[]>(count);

            for (int i = 0; i < count; i++) {
                if (objects.get(i)[dataItemPosition].toLowerCase().contains(filterString)) {
                    filteredList.add(objects.get(i));
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredObjects = (ArrayList<String[]>) results.values;
            notifyDataSetChanged();
        }
    }
}
