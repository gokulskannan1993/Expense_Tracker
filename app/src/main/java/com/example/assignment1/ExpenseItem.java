package com.example.assignment1;


import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Date;



public class ExpenseItem {


    private String date, month, context, amount;
    private int monthNumber, year, id;
    private boolean isRegular;


    //constructor
    ExpenseItem(int id,String context, String amount, String date, boolean isRegular){
        this.id = id;
        this.context = context;
        this.amount = amount;
        this.date = date;
        this.isRegular = isRegular;
        monthNumber = (Integer.parseInt(this.date.split("-")[1])+1);
        month = this.getMonth();
        year = Integer.parseInt(this.date.split("-")[0]);

    }
    ExpenseItem(int id,String context, String amount, String date){
        this.id = id;
        this.context = context;
        this.amount = amount;
        this.date = date;
        monthNumber = (Integer.parseInt(this.date.split("-")[1])+1);
        month = this.getMonth();
        year = Integer.parseInt(this.date.split("-")[0]);

    }
    ExpenseItem(String context, String amount, String date, boolean isRegular){
        this.context = context;
        this.amount = amount;
        this.date = date;
        this.isRegular = isRegular;
        monthNumber = (Integer.parseInt(this.date.split("-")[1]));
        month = this.getMonth();
        year = Integer.parseInt(this.date.split("-")[0]);

    }

    ExpenseItem(String context, String amount, String date){
        this.context = context;
        this.amount = amount;
        this.date = date;
        monthNumber = (Integer.parseInt(this.date.split("-")[1]));
        month = this.getMonth();
        year = Integer.parseInt(this.date.split("-")[0]);

    }


    //getters and setters
    public String getMonth() {
        switch(this.monthNumber){
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRegular() {
        return isRegular;
    }

    public void setRegular(boolean regular) {
        isRegular = regular;
    }

    @Override
    public String toString() {
        if(this.isRegular()) {
            return this.getContext() + " (R)";
        }else{
            return this.getContext() ;
        }
    }
}
