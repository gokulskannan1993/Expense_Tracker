package com.example.assignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {


    public DataBaseHelper(@Nullable Context context) {
        super(context, "expenses_db", null, 1);
    }



    //Generate new table
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createAllSheets(db);
        this.createExpensesTable(db);
    }

    //when version is changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //create allsheets table
    public void createAllSheets(SQLiteDatabase db){
        String create_All_Sheets_Table = "CREATE TABLE ALLSHEETS(" +
                "_id integer primary key autoincrement," +
                "month string," +
                "year string, " +
                "income string" +
                ")";
        db.execSQL(create_All_Sheets_Table);
    }


    //create expenses db
    public void createExpensesTable(SQLiteDatabase db){
        String create_expenses_table = "CREATE TABLE EXPENSES ( " +
                "_id  integer primary key autoincrement," +
                "context string," +
                "amount string," +
                "expense_date string," +
                "expense_month string," +
                "year integer," +
                "is_regular integer" +
                ")";
        db.execSQL(create_expenses_table);
    }

    //create a new entry in the ALLSHEETS
    public boolean addNewSheet(NewSheet sheet){
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", sheet.getMonth());
        cv.put("year", sheet.getYear());
        cv.put("income", "0.0");
        sdb.insert("ALLSHEETS", null, cv);
        return true;
    }







    //add a new expense into a table
    public boolean addNewExpense(ExpenseItem ei){
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("context", ei.getContext());
        cv.put("amount", ei.getAmount());
        cv.put("expense_date", ei.getDate());
        cv.put("expense_month", ei.getMonth());
        cv.put("year", ei.getYear());
        cv.put("is_regular", ei.isRegular());
        sdb.insert("EXPENSES", null, cv);
        return true;
    }
    
    





    //Delete from table
    public boolean deleteEntry(String tableName,  String where){
        SQLiteDatabase sdb = this.getWritableDatabase();
        String query = "DELETE FROM " +tableName +" WHERE " +where;
        sdb.execSQL(query);
        return true;
    }
}
