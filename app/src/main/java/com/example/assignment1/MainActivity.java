package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ListView lvSheets;

    private ArrayList<NewSheet> alSheets;
    private ArrayAdapter<NewSheet> aaSheets;
    private String npSelected = Calendar.getInstance().get(Calendar.YEAR)+"" , spSelected;
    private DataBaseHelper dbh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_sheets_layout);

        this.refreshView();


        //handling click events in lvSheets
        lvSheets.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(MainActivity.this, "Selected " +alSheets.get(position), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, EachMonth.class);
            intent.putExtra("monthId", alSheets.get(position).getId());
            startActivity(intent);
        });


        //Handling the fab
        FloatingActionButton fabAddMonth = findViewById(R.id.fab_add_month);
        fabAddMonth.setOnClickListener(v -> {
            addNewSheetDialog();
            this.refreshView();
        });

    }


    //fetch all sheets
    private void refreshView(){

        alSheets = new ArrayList<NewSheet>();
        //query every sheets
        dbh = new DataBaseHelper(MainActivity.this);
        String query = "SELECT * FROM ALLSHEETS";
        SQLiteDatabase sdb = dbh.getWritableDatabase();

        @SuppressLint("Recycle") Cursor c = sdb.rawQuery(query, null);

        //add each of the sheets to alSheets
        if(c.moveToFirst()){
            do{
                NewSheet ns = new NewSheet(c.getInt(0), c.getString(1), Integer.parseInt(c.getString(2)));
                ns.setIncome(Double.parseDouble(c.getString(3)));
                alSheets.add(ns);
            }while(c.moveToNext());

        }else{
            System.out.println("No items found");
        }
        lvSheets = findViewById(R.id.lv_all_expenses);
        aaSheets = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alSheets);
        lvSheets.setAdapter(aaSheets);
        aaSheets.notifyDataSetChanged();

    }



    // private method for adding expenses
    private void addNewSheetDialog(){

        // builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        // set the title on this dialog
        builder.setTitle("Select Month and Year");

        //to select the year
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(2021);
        numberPicker.setMinValue(1950);
        numberPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
        linearLayout.addView(numberPicker);

        //handling event
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            npSelected = newVal + "";
        });


        //to select the month
        Spinner sp = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.month_text,
                android.R.layout.simple_spinner_item);
        sp.setAdapter(adapter);
        int indexOfCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);
        sp.setSelection(indexOfCurrentMonth);
        linearLayout.addView(sp);

        //set linearLayout to builder
        builder.setView(linearLayout);

        //handling selection event
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spSelected = getMonth(Calendar.getInstance().get(Calendar.MONTH));
            }
        });


        // add in the positive button
        builder.setPositiveButton("Accept", (dialog, which) -> {
            NewSheet ns = new NewSheet(spSelected, Integer.parseInt(npSelected));
            dbh = new DataBaseHelper(MainActivity.this);
            Boolean insertNS = dbh.addNewSheet(ns);
            System.out.println(insertNS);
            this.refreshView();
        });

        // add in the negative button
        builder.setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(MainActivity.this, "User Cancelled Input",
                Toast.LENGTH_SHORT).show()) ;



        // create the dialog and display it
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    //return appropriate month
    public String getMonth(int monthNumber) {
        switch(monthNumber){
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "Unknown";
        }
    }
}
