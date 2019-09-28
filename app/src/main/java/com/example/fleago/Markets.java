package com.example.fleago;

import java.util.ArrayList;

public class Markets {
    // Firebase 칼럼
    private String day;
    private String discription;
    private String end_time;
    private String end_date;
    private ArrayList<String> gps ;
    private String month;
    private String name;
    private String start_date;
    private String start_location;
    private String start_time;
    private Integer week;
    private String url;
    private int distance;
    private ArrayList<String> event_type;




    public void setEvent_type(ArrayList<String> event_type) { this.event_type = event_type; }

    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getDiscription() { return discription; }
    public void setDiscription(String discription) { this.discription = discription; }

    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }

    public void setEnd_time(String end_time) { this.end_time = end_time; }
    public String getEnd_time() { return end_time; }

    public ArrayList<String> getGps() { return gps; }
    public void setGps(ArrayList<String> gps) { this.gps = gps; }

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

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public Markets(){ }

    public Markets(String day,String discription, String end_time, String end_date, ArrayList<String> gps, String month, String name, String start_date, String start_location, String start_time, int week, String url,ArrayList<String> event_type){

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
        this.url=url;
        this.event_type=event_type;

    }

    public String toString() {
        return name+day;
    }

    public boolean hasGps() {
        // gps 정보가 db에 있는가
        if (this.gps == null) {
            return false;
        } else if (this.gps.size() == 0) {
            return false;
        }else if (this.gps.get(0).equals("N") || this.gps.get(1).equals("N")) {
            return false;
        }

        return true;
    }

    public boolean hasTime() {
        // 운영시간에 대한 정보가 db에 있는가
        if (this.start_time == null || this.end_time == null) {
            return false;
        } else if (this.start_time.length() == 0 || this.end_time.length() == 0) {
            return false;
        } else if (this.start_time.equals("None") || this.end_time.equals("None")) {
            return false;
        }

        return true;
    }

    // market과 markets 사이의 차이를 임시로 메꿈.
    public ArrayList<String> getEvent_type() {
        return event_type;
    }
}

