package com.app.exptracker.model;

public class Months {

    String monthname;
    String monthindex;

    String yearName;
    String yearindex;

    public Months(String monthname, String monthindex) {
        this.monthname = monthname;
        this.monthindex = monthindex;
    }

    public Months(String yearName, String yearindex, String i) {
        this.yearName = yearName;
        this.yearindex = yearindex;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public String getYearindex() {
        return yearindex;
    }

    public void setYearindex(String yearindex) {
        this.yearindex = yearindex;
    }

    public String getMonthname() {
        return monthname;
    }

    public void setMonthname(String monthname) {
        this.monthname = monthname;
    }

    public String getMonthindex() {
        return monthindex;
    }

    public void setMonthindex(String monthindex) {
        this.monthindex = monthindex;
    }
}
