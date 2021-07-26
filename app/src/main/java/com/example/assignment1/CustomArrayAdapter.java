package com.example.assignment1;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
// class definition
public class CustomArrayAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<ExpenseItem> al_expenses;
    static class ViewHolder {
        public TextView tv_context;
        public TextView tv_amount;
        public TextView tv_date;
    }
    // constructor
    public CustomArrayAdapter(Context c, ArrayList<ExpenseItem> al) {
        context = c;
        al_expenses = al;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convert_view, ViewGroup parent) {

        ViewHolder holder;

        if(convert_view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convert_view = inflater.inflate(R.layout.custom_expense_layout, parent,
                    false);
            holder.tv_context = convert_view.findViewById(R.id.tv_context);
            holder.tv_amount = convert_view.findViewById(R.id.tv_amount);
            holder.tv_date = convert_view.findViewById(R.id.tv_date);


            convert_view.setTag(holder);

        }else {

            holder = (ViewHolder) convert_view.getTag();

        }

        // set all the data on the fields before returning it
        holder.tv_context.setText(al_expenses.get(position).toString());
        holder.tv_amount.setText("$"+al_expenses.get(position).getAmount());
        holder.tv_date.setText(al_expenses.get(position).getDate());


        // return the constructed view
        return convert_view;


    }

    public int getCount() { return al_expenses.size(); }

    public long getItemId(int position) { return position; }

    public Object getItem(int position) { return al_expenses.get(position); }
}