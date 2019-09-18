package com.example.seoulapp;

public class SearchListViewItem {
    private String search_profile;
    private String search_name;

    public void setSearchProfile(String profile) {
        search_profile = profile;
    }

    public void setSearchName(String name) {
        search_name = name;
    }

    public String getSearchProfile() {
        return this.search_profile;
    }

    public String getSearchName() {
        return this.search_name;
    }
}
