package com.example.fleago;

import java.util.ArrayList;

public class Market {

    private String district;
    private String introduction;
    private String name;
    private String page_url;
    private String location;
    ArrayList<String> event_type ;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
