package com.example.fleago;

import java.util.ArrayList;

public class Markets {
    private String day;
    private String discription;
    private String end_time;
    private String end_date;
    ArrayList<Double> gps ;
    private String month;
    private String name;
    private String start_date;
    private String start_location;
    private String start_time;
    private Integer week;

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getDiscription() { return discription; }
    public void setDiscription(String discription) { this.discription = discription; }

    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }

    public void setEnd_time(String end_time) { this.end_time = end_time; }
    public String getEnd_time() { return end_time; }

    public ArrayList<Double> getGps() { return gps; }
    public void setGps(ArrayList<Double> gps) { this.gps = gps; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStart_date() { return start_date; }
    public void setStart_date(String start_date) { this.start_date = start_date; }

    public String getStart_location() { return start_location; }
    public void setStart_location(String start_location) { this.start_location = start_location; }

    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public Markets(){ }

    public Markets(String day,String discription, String end_time, String end_date, ArrayList<Double> gps, String month, String name, String start_date, String start_location, String start_time, int week){

        this.day=day;
        this.discription=discription;
        this.end_time=end_time;
        this.end_date=end_date;
        this.gps=gps;
        this.month=month;
        this.name=name;
        this.start_date=start_date;
        this.start_location=start_location;
        this.start_time=start_time;
        this.week=week;
    }

    public String toString() {
        return name+day;
    }

}

