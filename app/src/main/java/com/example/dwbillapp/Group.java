package com.example.dwbillapp;


import java.util.ArrayList;

public class Group {
    private String gid;
    private String name;
    private String currency;
    private ArrayList<Member> arrayOfMembers;

    public Group() {
    }

    public Group(String gid, String name, String currency, ArrayList<Member> arrayOfMembers) {
        this.gid = gid;
        this.name = name;
        this.currency = currency;
        this.arrayOfMembers = arrayOfMembers;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
