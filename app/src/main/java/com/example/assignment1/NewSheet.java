package com.example.assignment1;


public class NewSheet {
    private String month;
    private int id, year;
    private Double income;

    //constructors
    public NewSheet(int id, String month, int year) {
        this.month = month;
        this.id = id;
        this.year = year;
        this.income = 0.0;
    }

    public NewSheet(String month, int year) {
        this.month = month;
        this.year = year;
        this.income = 0.0;
    }

    //getters and setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }


    @Override
    public String toString() {
        return this.getMonth() +" "+ this.getYear();
    }
}
