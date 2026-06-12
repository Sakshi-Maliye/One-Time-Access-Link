package com.example.one_time_link.model;

public class OneTimeLink {
    private String token;
    private boolean used;

    public OneTimeLink(String token) {
        this.token = token;
        this.used = false;
    }

    public String getToken() {
        return token;
    }

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        this.used = true;
    }
}