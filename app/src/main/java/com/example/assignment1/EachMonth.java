package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EachMonth extends AppCompatActivity {
    private TextView tvIncome;
    private TextView tvSurplus;
    private ListView expenseList;
    private ArrayList<ExpenseItem> alExpenses = new ArrayList<>();

    private CustomArrayAdapter aaExpenses;
    private double income=0.0;
    private DataBaseHelper dbh;
    private NewSheet ns;
    private ExpenseItem ei;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //fetch all from view
        TextView tvMonth = findViewById(R.id.tv_month);
        TextView tvYear = findViewById(R.id.tv_year);
        expenseList = findViewById(R.id.lv_expense);
        Button changeIncome = findViewById(R.id.btn_changeIncome);
        FloatingActionButton fab = findViewById(R.id.fab_addExpense);
        Button btnDeleteSheet = findViewById(R.id.btn_delete_sheet);
        tvSurplus = findViewById(R.id.tv_surplus);
        tvIncome = findViewById(R.id.tv_income);






        //fetch appropriate id
        Intent intent = getIntent();
        int sheetId = intent.getExtras().getInt("monthId");



        //get sheet from the database
        dbh = new DataBaseHelper(EachMonth.this);
        String query = "SELECT * FROM ALLSHEETS WHERE _id  = "+ sheetId;
        SQLiteDatabase sdb = dbh.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = sdb.rawQuery(query, null);
        if(c.moveToFirst()) {
            c.moveToFirst();
            ns = new NewSheet(c.getInt(0), c.getString(1), Integer.parseInt(c.getString(2)));
            ns.setIncome(Double.parseDouble(c.getString(3)));
            income = Double.parseDouble(c.getString(3));
        }
        this.expenseRefresh();
        this.updateExpenses(income);



        //show appropriate data
        tvMonth.setText("Month : " + ns.getMonth());
        tvYear.setText("Year : "+ ns.getYear());
        tvIncome.setText("Monthly Income($): "+ ns.getIncome());

        


        // overridden on click method
        changeIncome.setOnClickListener(v -> {
            this.changeIncomeDialog(ns);
            this.updateExpenses(income);
            this.expenseRefresh();
        });








        //Handling the fab
        fab.setOnClickListener(view -> {

            this.addExpenseDialog(ns);

            this.updateExpenses(income);
            this.expenseRefresh();
        });


        //handling delete sheet
        btnDeleteSheet.setOnClickListener(view ->{
            boolean deleteSheet = dbh.deleteEntry("ALLSHEETS", "_id = "+ns.getId());
            deleteSheet = dbh.deleteEntry("EXPENSES", "expense_month = '"+ns.getMonth()+"' AND year = "+ns.getYear()+"");
            Intent newIntent = new Intent(EachMonth.this, MainActivity.class);
            startActivity(newIntent);
        });


        //handling clicking the listView
        expenseList.setOnItemClickListener((parent, view, position, id) -> {
            this.editExpenseDialog(ns,alExpenses.get(position));
            this.updateExpenses(income);

        });



    }


    // private method for adding expenses
    private void editExpenseDialog(NewSheet ns, ExpenseItem ei){

        this.expenseRefresh();
        // builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Edit/Delete Expense");

        LayoutInflater layoutInflater =  (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View promptView = layoutInflater.inflate(R.layout.add_expense, null);


        builder.setView(promptView);

        //fetch all the fields from the views
        final EditText et_context = promptView.findViewById(R.id.et_context);
        final EditText et_amount = promptView.findViewById(R.id.et_amount);
        final TextView tv_date = promptView.findViewById(R.id.tv_date);
        final CalendarView cv_calender = promptView.findViewById(R.id.cv_calender);
        final CheckBox cb_isRegular = promptView.findViewById(R.id.cb_is_regular);


        et_context.setText(ei.getContext());
        et_amount.setText(ei.getAmount());
        tv_date.setText(ei.getDate());

        //make sure user can select date only within the sheets limit
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("MMM").parse(ns.getMonth()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int monthInt = cal.get(Calendar.MONTH);

        Calendar mycal = new GregorianCalendar(ns.getYear(), monthInt, 1);

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String d1 = ns.getMonth()+ " "+daysInMonth+", "+ns.getYear();
        String d2 = ns.getMonth()+ " 1, "+ns.getYear();

        DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        Date maxDate = null;
        Date minDate = null;
        try {
            maxDate = sdf.parse(d1);
            minDate = sdf.parse(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cv_calender.setMaxDate(maxDate.getTime());
        cv_calender.setMinDate(minDate.getTime());

        //update checkbox
        if(ei.isRegular()){
            cb_isRegular.setChecked(true);
        }else{
            cb_isRegular.setChecked(false);
        }




        //when a date is selected
        cv_calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int newMonth = month + 1;
                tv_date.setText( year + "-" + newMonth + "-" + dayOfMonth);
            }
        });



        // add in the positive button
        builder.setPositiveButton("Accept", (dialog, which) -> {
            dbh = new DataBaseHelper(EachMonth.this);
            ExpenseItem e = new ExpenseItem(et_context.getText().toString(), et_amount.getText().toString(), tv_date.getText().toString());
            e.setRegular(cb_isRegular.isChecked());
            String query = "UPDATE EXPENSES SET " +
                    "context = '"+e.getContext()+"', " +
                    "amount = '"+e.getAmount()+"', " +
                    "expense_date = '"+e.getDate()+"'," +
                    "expense_month = '"+e.getMonth()+"'," +
                    "year = '"+e.getYear()+"'," +
                    "is_regular = "+(e.isRegular()? 1 : 0)+
                    " WHERE _id = "+ei.getId();
            SQLiteDatabase sdb = dbh.getWritableDatabase();
            sdb.execSQL(query);
            this.expenseRefresh();

        });

        // add in the negative button
        builder.setNeutralButton("Cancel", (dialog, which) -> Toast.makeText(EachMonth.this, "User Cancelled Input",
                Toast.LENGTH_SHORT).show());

        //add in negative button
        builder.setNegativeButton("Delete", (dialog, which)->{
            dbh = new DataBaseHelper(EachMonth.this);
            boolean deleteSheet = dbh.deleteEntry("EXPENSES", "_id = "+ei.getId()+"");
            Toast.makeText(EachMonth.this, "Expense Deleted",
                    Toast.LENGTH_SHORT).show();
            this.expenseRefresh();
        });


        // create the dialog and display it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // private method for adding expenses
    private void addExpenseDialog(NewSheet ns){

        this.expenseRefresh();
        // builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Expense");


        LayoutInflater layoutInflater =  (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View promptView = layoutInflater.inflate(R.layout.add_expense, null);


        builder.setView(promptView);


        //fetch all the fields from the views
        final EditText et_context = promptView.findViewById(R.id.et_context);
        final EditText et_amount = promptView.findViewById(R.id.et_amount);
        final TextView tv_date = promptView.findViewById(R.id.tv_date);
        final CalendarView cv_calender = promptView.findViewById(R.id.cv_calender);
        final CheckBox cb_isRegular = promptView.findViewById(R.id.cb_is_regular);


        //when a date is selected
        cv_calender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            int newMonth = month + 1;
            tv_date.setText( year + "-" + newMonth + "-" + dayOfMonth);
        });

        //make sure user can select date only within the sheets limit
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("MMM").parse(ns.getMonth()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int monthInt = cal.get(Calendar.MONTH);

        Calendar mycal = new GregorianCalendar(ns.getYear(), monthInt, 1);

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String d1 = ns.getMonth()+ " "+daysInMonth+", "+ns.getYear();
        String d2 = ns.getMonth()+ " 1, "+ns.getYear();

        DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        Date maxDate = null;
        Date minDate = null;
        try {
            maxDate = sdf.parse(d1);
            minDate = sdf.parse(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cv_calender.setMaxDate(maxDate.getTime());
        cv_calender.setMinDate(minDate.getTime());

        // add in the positive button
        builder.setPositiveButton("Accept", (dialog, which) -> {
           ei = new ExpenseItem(et_context.getText().toString(), et_amount.getText().toString(), tv_date.getText().toString());
           ei.setRegular(cb_isRegular.isChecked());
           dbh = new DataBaseHelper(EachMonth.this);
           Boolean insertEI = dbh.addNewExpense(ei);
           this.expenseRefresh();
        });

        // add in the negative button
        builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(EachMonth.this, "User Cancelled Input",
                Toast.LENGTH_SHORT).show());


        // create the dialog and display it
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //refresh expense view
    private void expenseRefresh(){
        dbh = new DataBaseHelper(EachMonth.this);
        alExpenses = new ArrayList<>();
        String query = "SELECT * FROM EXPENSES WHERE expense_month = '"+ns.getMonth()+"' AND year = "+ns.getYear()+"";
        SQLiteDatabase sdb = dbh.getWritableDatabase();
        //get all expenses of the month
        Cursor c =  sdb.rawQuery(query, null);

        if(c.moveToFirst()) {
            do{
                ei = new ExpenseItem(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
                ei.setRegular(c.getInt(6) != 0);
                alExpenses.add(ei);
            }while(c.moveToNext());
        }

        // create an array adapter for al_strings and set it on the listview
        expenseList = findViewById(R.id.lv_expense);
        aaExpenses = new CustomArrayAdapter(this,  alExpenses);
        expenseList.setAdapter(aaExpenses);
        aaExpenses.notifyDataSetChanged();
    }



    //private method for changing income
    private void changeIncomeDialog(NewSheet ns){
        // builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the title on this dialog
        builder.setTitle("Change Income");

        final EditText eAmount = new EditText(this);
        eAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        eAmount.setText("0");
        builder.setView(eAmount);

        // add in the positive button
        builder.setPositiveButton("Accept", (dialog, which) -> {
            if (eAmount.getText().toString() != null){
                income = Double.parseDouble(eAmount.getText().toString());

                //update to the db
                ns.setIncome(income);
                dbh = new DataBaseHelper(EachMonth.this);
                SQLiteDatabase sdb = dbh.getWritableDatabase();
                String query = "UPDATE ALLSHEETS SET income = "+income+" WHERE _id = "+ns.getId();
                sdb.execSQL(query);
                tvIncome = findViewById(R.id.tv_income);
                tvIncome.setText("Monthly Income($): "  +ns.getIncome());
            }
        });
        // add in the negative button
        builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(EachMonth.this, "user cancelled input",
                Toast.LENGTH_SHORT).show());

        // create the dialog and display it
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    //update all expenses
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void updateExpenses(Double income){
        double regular = 0, surplus = 0, nonRegular = 0, total = 0;

        for(ExpenseItem e : alExpenses){
            if(e.isRegular()){
                regular += Double.parseDouble(e.getAmount());
            }else{
                nonRegular += Double.parseDouble(e.getAmount());
            }
        }
        total = regular + nonRegular;
        surplus = income - total;
        tvSurplus = findViewById(R.id.tv_surplus);
        TextView tvRegular = findViewById(R.id.tv_regular);
        TextView tvNonRegular = findViewById(R.id.tv_non_regular);

        tvRegular.setText("Regular Expenses($): "+regular);
        tvNonRegular.setText("Non-Regular Expenses($): "+nonRegular);

        if (surplus < 0){
            tvSurplus.setText("Deficit($) : " +(-1*surplus));
            tvSurplus.setTextColor(getResources().getColor(R.color.red, null));
        }else{
            tvSurplus.setText("Surplus($) : " +(surplus));
            tvSurplus.setTextColor(getResources().getColor(R.color.teal_700, null));

        }

    }

}
