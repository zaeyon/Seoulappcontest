package com.example.seoulapp;

import com.google.firebase.storage.StorageReference;

public class SearchListViewItem {
    //private StorageReference search_profile;
    private String search_profile;
    private String search_name;
    private String search_building;
    private String search_type;


    public void setSearchProfile(String profile) {
        search_profile = profile;
    }

    public void setSearchName(String name) {
        search_name = name;
    }

    public void setSearchBuilding(String building) { search_building = building; }

    public void setSearchType(String type) { search_type = type; }

    public String getSearchProfile() {
        return this.search_profile;
    }

    public String getSearchName() {
        return this.search_name;
    }

    public String getSearchBuilding() { return this.search_building; }

    public String getSearchType() { return this.search_type; }





}
