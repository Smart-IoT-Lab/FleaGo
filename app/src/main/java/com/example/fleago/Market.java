package com.example.fleago;

import java.util.ArrayList;

public class Market {

    private String district;
    private String introduction;
    private String name;
    private String page_url;
    private String location;
    ArrayList<String> event_type =  new ArrayList<>();
    ArrayList<String> gps = new ArrayList<>();  // 0:경도, 1:위도
    private String start_time;
    private String end_time;

    private int distance;

    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getGps() {
        return gps;
    }
    public void setGps(ArrayList<String> gps) {
        this.gps = gps;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
    public void setEvent_type(ArrayList<String> event_type) {
        this.event_type = event_type;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public String getDistrict() {
        return district;
    }
    public ArrayList<String> getEvent_type() {
        return event_type;
    }
    public String getIntroduction() {
        return introduction;
    }
    public String getName() {
        return name;
    }
    public String getPage_url() {
        return page_url;
    }
    public String getEnd_time() {
        return end_time;
    }
    public String getStart_time() {
        return start_time;
    }

    public Market(){ }

    public Market(String district, ArrayList<String> event_type, String introduction, String name, String page_url){
        this.district=district;
        this.event_type=event_type;
        this.introduction=introduction;
        this.name=name;
        this.page_url=page_url;
    }

    public String toString() {
        return name+ event_type;
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
}
