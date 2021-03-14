package com.example.dwbillapp;

public class Member {
    private String uid;
    private double outcome;
    private int settled;

    public Member() {
    }

    public Member(String uid, double outcome, int settled) {
        this.uid = uid;
        this.outcome = outcome;
        this.settled=settled;
    }

    public Member(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getOutcome() {
        return outcome;
    }

    public void setOutcome(double outcome) {
        this.outcome = outcome;
    }

    public int getSettled() {
        return settled;
    }

    public void setSettled(int settled) {
        this.settled = settled;
    }
}
